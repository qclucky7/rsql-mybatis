package com.github.mybatis.searcher;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mybatis.searcher.solver.SolverType;
import cz.jirutka.rsql.parser.ast.*;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WangChen
 * @since 2021-10-22 17:55
 **/
public class QueryBodyVisitor extends NoArgRSQLVisitorAdapter<SearchBodyAccessor> {

    private static final SimpleCache<Class<?>, SimpleCache<String, QueryBodyContext>> CACHE = new SimpleCache<>();
    private final SimpleCache<String, QueryBodyContext> aliasQueryBody;
    private final SearchBodyAccessor searchBodyAccessor;
    private final ParseRepeatCounter parseRepeatCounter;

    public QueryBodyVisitor(Class<?> target) {
        searchBodyAccessor = new SearchBodyAccessor();
        parseRepeatCounter = new ParseRepeatCounter();
        aliasQueryBody = CACHE.get(target, (Func0<SimpleCache<String, QueryBodyContext>>) () -> {
            SimpleCache<String, QueryBodyContext> entries = new SimpleCache<>();
            Field[] fields = ReflectUtil.getFields(target);
            for (Field field : fields) {
                SearchCondition annotation = field.getAnnotation(SearchCondition.class);
                if (ObjectUtil.isNull(annotation)) {
                    continue;
                }
                SearchType[] support = annotation.available();
                QueryBodyContext queryBodyContext = new QueryBodyContext();
                String alias = annotation.alias();
                if (StrUtil.isBlank(alias)) {
                    alias = field.getName();
                }
                queryBodyContext.setField(field);
                queryBodyContext.setSupport(Stream.of(support).flatMap(type -> Stream.of(SolverType.getTargetSymbols(type))).collect(Collectors.toSet()));
                entries.put(alias, queryBodyContext);
            }
            return entries;
        });

    }

    @Override
    public SearchBodyAccessor visit(AndNode andNode) {
        for (Node node : andNode) {
            node.accept(this);
        }
        return searchBodyAccessor;
    }

    @Override
    public SearchBodyAccessor visit(OrNode orNode) {
        return searchBodyAccessor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SearchBodyAccessor visit(ComparisonNode comparisonNode) {
        String symbol = comparisonNode.getOperator().getSymbol();
        String selector = comparisonNode.getSelector();
        List<String> arguments = comparisonNode.getArguments();
        if (parseRepeatCounter.alreadyParsed(selector + symbol)){
            return searchBodyAccessor;
        }
        QueryBodyContext queryBodyContext = aliasQueryBody.get(selector);
        if (ObjectUtil.isNull(queryBodyContext)) {
            return searchBodyAccessor;
        }
        if (!queryBodyContext.getSupport().contains(symbol)) {
            return searchBodyAccessor;
        }
        String name = queryBodyContext.field.getName();
        Object attrValue = parsingValue(queryBodyContext.field, arguments);
        SearchBodyAttribute searchBodyAttribute = new SearchBodyAttribute(name, attrValue instanceof Collection ? (attrValue instanceof Set ? (Set<Object>) attrValue : (List<Object>) attrValue) : Collections.singletonList(attrValue), symbol);
        searchBodyAccessor.add(searchBodyAttribute);
        return searchBodyAccessor;
    }

    public Object parsingValue(Field field, List<String> arguments) {
        if (arguments.isEmpty()) {
            return null;
        }
        Class<?> type = field.getType();
        if (String.class.equals(type)) {
            return arguments.get(0);
        } else if (Long.class.equals(type) || Long.TYPE.equals(type)) {
            try {
                return Long.valueOf(arguments.get(0));
            } catch (Exception ex) {
                return 0L;
            }
        } else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {
            try {
                return Integer.valueOf(arguments.get(0));
            } catch (Exception ex) {
                return 0L;
            }
        } else if (Date.class.equals(type)) {
            String date = arguments.get(0);
            try {
                return new Date(Long.parseLong(date));
            } catch (Exception ex) {
                return new Date();
            }
        } else if (BigDecimal.class.equals(type)){
            try {
                return new BigDecimal(arguments.get(0));
            } catch (Exception ex){
                return BigDecimal.ZERO;
            }

        } else if (Collection.class.isAssignableFrom(type)) {
            Type genericType = field.getGenericType();
            if (ObjectUtil.isNull(genericType)) {
                return null;
            }
            if (genericType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericType;
                //数组泛型
                Type types = parameterizedType.getActualTypeArguments()[0];
                if (String.class.equals(types)) {
                    return arguments;
                } else if (Long.class.equals(types)) {
                    Stream<Long> stream = arguments.stream()
                            .filter(NumberUtil::isLong)
                            .map(argument -> {
                                try {
                                    return Long.valueOf(argument);
                                } catch (NumberFormatException e) {
                                    return 0L;
                                }
                            });
                    if (List.class.isAssignableFrom(type)) {
                        return stream.collect(Collectors.toList());
                    } else if (Set.class.isAssignableFrom(type)) {
                        return stream.collect(Collectors.toSet());
                    }
                } else if (Integer.class.equals(types)) {
                    Stream<Integer> integerStream = arguments.stream()
                            .filter(NumberUtil::isInteger)
                            .map(argument -> {
                                try {
                                    return Integer.valueOf(argument);
                                } catch (NumberFormatException e) {
                                    return 0;
                                }
                            });
                    if (List.class.isAssignableFrom(type)) {
                        return integerStream.collect(Collectors.toList());
                    } else if (Set.class.isAssignableFrom(type)) {
                        return integerStream.collect(Collectors.toSet());
                    }
                }
            }
        }
        return null;
    }


    static class QueryBodyContext {
        private Field field;
        private Set<String> support;

        public Field getField() {
            return field;
        }

        public void setField(Field field) {
            this.field = field;
        }

        public Set<String> getSupport() {
            return support;
        }

        public void setSupport(Set<String> support) {
            this.support = support;
        }

        @Override
        public String toString() {
            return "QueryBodyContext{" +
                    "field=" + field +
                    ", support=" + support +
                    '}';
        }
    }

}
