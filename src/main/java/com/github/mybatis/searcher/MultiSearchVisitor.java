package com.github.mybatis.searcher;

import cn.hutool.core.lang.SimpleCache;
import cn.hutool.core.lang.func.Func0;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mybatis.searcher.solver.SolverContext;
import com.github.mybatis.searcher.solver.SolverType;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WangChen
 * @since 2021-10-22 12:12
 **/
public class MultiSearchVisitor extends AbstractSearchVisitor {

    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, MultiSolverContext>> CACHE_ALIAS_CONTEXT = new ConcurrentHashMap<>(64);
    private static final ConcurrentMap<Class<?>, ConcurrentMap<String, String>> CACHE_TABLE_ALIAS = new ConcurrentHashMap<>(64);
    private ConcurrentMap<String, MultiSolverContext> aliasColumn;
    private ConcurrentMap<String, String> aliasTable;

    public MultiSearchVisitor(PlainSelect plainSelect, Class<?> target) {
        super(plainSelect, target);
        initAliasTableCache();
        initAliasContextCache();
    }

    private void initAliasTableCache() {
        aliasTable = CACHE_TABLE_ALIAS.computeIfAbsent(target, clazz -> {
            ConcurrentMap<String, String> aliasTable = new ConcurrentHashMap<>();
            FromItemVisitorImpl fromItemVisitor = new FromItemVisitorImpl(aliasTable);
            FromItem mainTable = plainSelect.getFromItem();
            mainTable.accept(fromItemVisitor);
            for (Join join : plainSelect.getJoins()) {
                join.getRightItem().accept(fromItemVisitor);
            }
            return aliasTable;
        });
    }

    private void initAliasContextCache() {
        aliasColumn = CACHE_ALIAS_CONTEXT.computeIfAbsent(target, clazz -> {
            ConcurrentMap<String, MultiSolverContext> aliasColumn = new ConcurrentHashMap<>();
            Field[] fields = ReflectUtil.getFields(target);
            for (Field field : fields) {
                final MultiSearchCondition multiSearchCondition = field.getAnnotation(MultiSearchCondition.class);
                if (ObjectUtil.isNull(multiSearchCondition)) {
                    continue;
                }
                loadingConverter(field, multiSearchCondition.converter());
                String alias = multiSearchCondition.alias();
                if (StrUtil.isBlank(alias)) {
                    alias = field.getName();
                }
                String columnName = multiSearchCondition.columnName();
                if (StrUtil.isBlank(columnName)) {
                    columnName = StrUtil.toUnderlineCase(field.getName());
                }
                String tableAlias = aliasTable.get(multiSearchCondition.tableName());
                if (StrUtil.isBlank(tableAlias)) {
                    continue;
                }
                MultiSolverContext analyzeContext = new MultiSolverContext();
                SearchType[] support = multiSearchCondition.available();
                analyzeContext.setTarget(target);
                analyzeContext.setField(field);
                analyzeContext.setColumnName(joiningAliasQuery(tableAlias, columnName));
                analyzeContext.setSupport(Stream.of(support).flatMap(type -> Stream.of(SolverType.getTargetSymbols(type))).collect(Collectors.toSet()));
                analyzeContext.setTableName(multiSearchCondition.tableName());
                aliasColumn.put(alias, analyzeContext);
            }
            return aliasColumn;
        });
    }


    @Override
    public SolverContext getContext(String selector) {
        return aliasColumn.get(selector);
    }

    private String joiningAliasQuery(String tableAlias, String columnName) {
        return StrUtil.builder(tableAlias, StrUtil.DOT, columnName).toString();
    }


    static class FromItemVisitorImpl extends FromItemVisitorAdapter {

        ConcurrentMap<String, String> aliasTable;

        public FromItemVisitorImpl(ConcurrentMap<String, String> aliasTable) {
            this.aliasTable = aliasTable;
        }

        @Override
        public void visit(Table table) {
            String tableName = table.getName().trim();
            aliasTable.put(tableName, table.getAlias() == null ? tableName : table.getAlias().getName().trim());
        }
    }
}
