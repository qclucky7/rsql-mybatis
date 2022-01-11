package com.github.mybatis.searcher;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mybatis.searcher.convert.EmptyConverter;
import com.github.mybatis.searcher.convert.SearchConverter;
import com.github.mybatis.searcher.solver.SolverContext;
import com.github.mybatis.searcher.solver.SolverType;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WangChen
 * @since 2021-10-22 11:18
 **/
public class SingleSearchVisitor extends AbstractSearchVisitor {

    private static SimpleCache<Class<?>, SimpleCache<String, SolverContext>> CACHE_ALIAS_CONTEXT = new SimpleCache<>(new HashMap<>());
    private SimpleCache<String, SolverContext> aliasColumn;

    public SingleSearchVisitor(PlainSelect plainSelect, Class<?> target) {
        super(plainSelect, target);
        initAliasContextCache();
    }

    private void initAliasContextCache() {
        aliasColumn = CACHE_ALIAS_CONTEXT.get(target, (Func0<SimpleCache<String, SolverContext>>) () -> {
            SimpleCache<String, SolverContext> aliasColumn = new SimpleCache<>();
            Field[] fields = ReflectUtil.getFields(target);
            for (Field field : fields) {
                final SearchCondition searchCondition = field.getAnnotation(SearchCondition.class);
                if (ObjectUtil.isNull(searchCondition)) {
                    continue;
                }
                loadingConverter(field, searchCondition.converter());
                SolverContext solverContext = new SolverContext();
                String alias = searchCondition.alias();
                if (StrUtil.isBlank(alias)) {
                    alias = field.getName();
                }
                String columnName = searchCondition.columnName();
                if (StrUtil.isBlank(columnName)) {
                    columnName = StrUtil.toUnderlineCase(field.getName());
                }
                SearchType[] support = searchCondition.available();
                solverContext.setTarget(target);
                solverContext.setField(field);
                solverContext.setColumnName(columnName);
                solverContext.setSupport(Stream.of(support).flatMap(type -> Stream.of(SolverType.getTargetSymbols(type))).collect(Collectors.toSet()));
                aliasColumn.put(alias, solverContext);
            }
            return aliasColumn;
        });
    }

    @Override
    public SolverContext getContext(String selector) {
        return aliasColumn.get(selector);
    }
}
