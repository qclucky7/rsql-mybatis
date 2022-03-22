package com.github.mybatis.searcher;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mybatis.searcher.convert.ConverterFactory;
import com.github.mybatis.searcher.convert.EmptyConverter;
import com.github.mybatis.searcher.convert.SearchConverter;
import com.github.mybatis.searcher.holder.MybatisMappingContextHolder;
import com.github.mybatis.searcher.solver.*;
import com.github.mybatis.searcher.tools.Callback;
import cz.jirutka.rsql.parser.ast.*;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.util.cnfexpression.MultiOrExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author WangChen
 * @since 2021-10-22 11:19
 **/
public abstract class AbstractSearchVisitor extends NoArgRSQLVisitorAdapter<PlainSelect> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSearchVisitor.class);
    private static final SimpleCache<Class<?>, AliasColumn> CACHE_QUERYABLE_FIELDS = new SimpleCache<>(new HashMap<>());
    protected PlainSelect plainSelect;
    protected Class<?> target;
    private final ParseRepeatCounter parseRepeatCounter;

    public AbstractSearchVisitor(PlainSelect plainSelect, Class<?> target) {
        this.plainSelect = plainSelect;
        this.target = target;
        parseRepeatCounter = new ParseRepeatCounter();
        initQueryFieldsCache();
    }

    private void initQueryFieldsCache() {
        CACHE_QUERYABLE_FIELDS.get(target, (Func0<AliasColumn>) () -> {
            SearchableFields annotation = target.getAnnotation(SearchableFields.class);
            AliasColumn aliasColumn = new AliasColumn();
            Field[] fields = ReflectUtil.getFields(target);
            for (Field field : fields) {
                SearchableField searchableField = field.getAnnotation(SearchableField.class);
                if (ObjectUtil.isNull(searchableField)) {
                    if (ObjectUtil.isNotNull(annotation)) {
                        String alias = field.getName();
                        String columnName = StrUtil.toUnderlineCase(field.getName());
                        aliasColumn.put(alias, columnName);
                    }
                    continue;
                }
                String alias = searchableField.alias();
                if (StrUtil.isBlank(alias)) {
                    alias = field.getName();
                }
                String columnName = searchableField.columnName();
                if (StrUtil.isBlank(columnName)) {
                    columnName = StrUtil.toUnderlineCase(field.getName());
                }
                aliasColumn.put(alias, columnName);
            }
            return aliasColumn;

        });
    }

    @Override
    public PlainSelect visit(AndNode andNode) {
        for (Node node : andNode) {
            String nodeString = node.toString();
            //只解析一层OR查询
            if (nodeString.startsWith("(") && nodeString.endsWith(")")){
                MultiOrExpressionSearchVisitor multiOrExpressionSearchVisitor = new MultiOrExpressionSearchVisitor();
                node.accept(multiOrExpressionSearchVisitor);
                multiOrExpressionSearchVisitor.execute();
            } else {
                node.accept(AbstractSearchVisitor.this);
            }
        }
        return plainSelect;
    }

    @Override
    public PlainSelect visit(OrNode orNode) {
        //根节点不解析or
        return plainSelect;
    }

    @Override
    public PlainSelect visit(ComparisonNode comparisonNode) {
        String selector = comparisonNode.getSelector();
        List<String> arguments = comparisonNode.getArguments();
        if (CollUtil.isEmpty(arguments)){
            return plainSelect;
        }
        String symbol = comparisonNode.getOperator().getSymbol();
        if (parseRepeatCounter.alreadyParsed(selector + symbol)) {
            logger.info("[AbstractSearchVisitor] class: {}, selectorSymbol Repeat parsed : {}", target, selector + symbol);
            return plainSelect;
        }
        Solver solver = SolverDiscoverer.lookup(symbol);
        if (ObjectUtil.isNull(solver)) {
            return plainSelect;
        }
        if (solver instanceof ConditionSolver) {
            ConditionSolver conditionSolver = (ConditionSolver) solver;
            SolverContext solverContext = getContext(selector);
            if (ObjectUtil.isNull(solverContext)) {
                return plainSelect;
            }
            if (!solverContext.getSupport().contains(symbol)) {
                return plainSelect;
            }
            ExpressionDelegate expressionDelegate = conditionSolver.handle(plainSelect, new SolverContextWrapper(solverContext, arguments));
            Expression expression = expressionDelegate.getExpression();
            List<Object> params = expressionDelegate.getParams();
            Class<?> type = expressionDelegate.getType();
            if (CollUtil.isEmpty(params)) {
                return plainSelect;
            }
            Expression where = plainSelect.getWhere();
            if (ObjectUtil.isNull(where)) {
                plainSelect.setWhere(expression);
            } else {
                plainSelect.setWhere(new AndExpression(where, expression));
            }
            for (int i = 0; i < params.size(); i++) {
                MybatisMappingContextHolder.constructMappingParameter("searchParam" +  solverContext.getField().getName() + i, type, params.get(i));
            }

        } else if (solver instanceof ResultSetSolver) {
            ResultSetSolver resultSetSolver = (ResultSetSolver) solver;
            AliasColumn aliasColumn = CACHE_QUERYABLE_FIELDS.get(target);
            if (CollUtil.isEmpty(aliasColumn)) {
                return plainSelect;
            }
            resultSetSolver.handle(plainSelect, selector, aliasColumn, arguments);
        }

        return plainSelect;
    }


    /**
     * 装载自定义转换器
     * @param field field
     * @param targetSearchConverterClass {@code Class<? extends SearchConverter<?>>}
     * @throws IllegalAccessException IllegalAccessException
     */
    protected void loadingConverter(Field field, Class<? extends SearchConverter<?>> targetSearchConverterClass) throws IllegalAccessException {
        if (EmptyConverter.class.equals(targetSearchConverterClass)){
            return;
        }
        try {
            ConverterFactory.loadingCustomConverter(field, targetSearchConverterClass.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalAccessException(String.format("cannot initialize the current converter on %s", targetSearchConverterClass));
        }
    }


    /**
     * 获取查询上下文
     *
     * @param selector selector
     * @return SolverContext
     */
    public abstract SolverContext getContext(String selector);


    /**
     * 解析多or表达式
     */
    private class MultiOrExpressionSearchVisitor extends NoArgRSQLVisitorAdapter<PlainSelect> implements Callback {

        private Map<FieldSymbolKey, SolverContextWrapper> solverContextWrappers = new HashMap<>();

        @Override
        public PlainSelect visit(AndNode andNode) {
            //()内只解析or查询
            return AbstractSearchVisitor.this.plainSelect;
        }

        @Override
        public PlainSelect visit(OrNode orNode) {
            for (Node node : orNode) {
                node.accept(MultiOrExpressionSearchVisitor.this);
            }
            return AbstractSearchVisitor.this.plainSelect;
        }

        @Override
        public PlainSelect visit(ComparisonNode comparisonNode) {
            String selector = comparisonNode.getSelector();
            List<String> arguments = comparisonNode.getArguments();
            if (CollUtil.isEmpty(arguments)){
                return AbstractSearchVisitor.this.plainSelect;
            }
            String symbol = comparisonNode.getOperator().getSymbol();
            String parsedKey = StrUtil.builder("(")
                    .append(selector)
                    .append(symbol)
                    .append(")")
                    .toString();
            if (parseRepeatCounter.alreadyParsed(parsedKey)) {
                logger.info("[AbstractSearchVisitor.MultiOrExpressionSearchVisitor] class: {}, selectorSymbol repeat parsed : {}", target, parsedKey);
                return AbstractSearchVisitor.this.plainSelect;
            }
            SolverContext solverContext = getContext(selector);
            if (ObjectUtil.isNull(solverContext)) {
                return AbstractSearchVisitor.this.plainSelect;
            }
            if (!solverContext.getSupport().contains(symbol)) {
                return AbstractSearchVisitor.this.plainSelect;
            }
            solverContextWrappers.put(new FieldSymbolKey(selector, symbol), new SolverContextWrapper(solverContext, arguments));
            return AbstractSearchVisitor.this.plainSelect;
        }

        @Override
        public void execute() {
            MultiOrExpression multiOrExpression = new MultiOrExpression(new ArrayList<>(solverContextWrappers.size()));
            for (Map.Entry<FieldSymbolKey, SolverContextWrapper> entry : solverContextWrappers.entrySet()) {
                FieldSymbolKey fieldSymbolKey = entry.getKey();
                String symbol = fieldSymbolKey.getSymbol();
                String selector = fieldSymbolKey.getSelector();
                SolverContextWrapper contextWrapper = entry.getValue();
                Solver solver = SolverDiscoverer.lookup(symbol);
                if (ObjectUtil.isNull(solver)) {
                    continue;
                }
                if (solver instanceof ConditionSolver) {
                    ConditionSolver conditionSolver = (ConditionSolver) solver;
                    ExpressionDelegate expressionDelegate = conditionSolver.handle(plainSelect, contextWrapper);
                    if (ObjectUtil.isNull(expressionDelegate)) {
                        continue;
                    }
                    Expression expression = expressionDelegate.getExpression();
                    List<Object> params = expressionDelegate.getParams();
                    Class<?> type = expressionDelegate.getType();
                    if (CollUtil.isEmpty(params)) {
                        continue;
                    }
                    for (int i = 0; i < params.size(); i++) {
                        MybatisMappingContextHolder.constructMappingParameter("searchParam" + contextWrapper.getSolverContext().getField().getName() + i, type, params.get(i));
                    }
                    multiOrExpression.getList().add(expression);
                }
            }
            if (multiOrExpression.getList().isEmpty()){
                return;
            }
            Expression where = plainSelect.getWhere();
            if (ObjectUtil.isNull(where)) {
                plainSelect.setWhere(multiOrExpression);
            } else {
                plainSelect.setWhere(new AndExpression(where, multiOrExpression));
            }
        }

        class FieldSymbolKey {
            private final String selector;
            private final String symbol;

            public FieldSymbolKey(String selector, String symbol) {
                this.selector = selector;
                this.symbol = symbol;
            }

            public String getSelector() {
                return selector;
            }

            public String getSymbol() {
                return symbol;
            }

            @Override
            public String toString() {
                return "FieldSymbolKey{" +
                        "selector='" + selector + '\'' +
                        ", symbol='" + symbol + '\'' +
                        '}';
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof FieldSymbolKey)) {
                    return false;
                }
                FieldSymbolKey that = (FieldSymbolKey) o;
                return Objects.equals(selector, that.selector) &&
                        Objects.equals(symbol, that.symbol);
            }

            @Override
            public int hashCode() {
                return Objects.hash(selector, symbol);
            }
        }
    }


    public static class AliasColumn extends HashMap<String, String> {

    }
}
