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
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author WangChen
 * @since 2021-10-22 12:12
 **/
public class MultiSearchVisitor extends AbstractSearchVisitor {

    private static final SimpleCache<Class<?>, SimpleCache<String, MultiSolverContext>> CACHE_ALIAS_CONTEXT = new SimpleCache<>(new HashMap<>());
    private static final SimpleCache<Class<?>, SimpleCache<String, String>> CACHE_TABLE_ALIAS = new SimpleCache<>(new HashMap<>());
    private SimpleCache<String, MultiSolverContext> aliasColumn;
    private SimpleCache<String, String> aliasTable;

    public MultiSearchVisitor(PlainSelect plainSelect, Class<?> target) {
        super(plainSelect, target);
        initAliasTableCache();
        initAliasContextCache();
    }

    private void initAliasTableCache() {
        aliasTable = CACHE_TABLE_ALIAS.get(target, (Func0<SimpleCache<String, String>>) () -> {
            SimpleCache<String, String> aliasTable = new SimpleCache<>();
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
        aliasColumn = CACHE_ALIAS_CONTEXT.get(target, (Func0<SimpleCache<String, MultiSolverContext>>) () -> {
            SimpleCache<String, MultiSolverContext> aliasColumn = new SimpleCache<>();
            Field[] fields = ReflectUtil.getFields(target);
            for (Field field : fields) {
                final MultiSearchCondition multiSearchCondition = field.getAnnotation(MultiSearchCondition.class);
                if (ObjectUtil.isNull(multiSearchCondition)) {
                    continue;
                }
                loadingConverter(field, multiSearchCondition.converter());
                MultiSolverContext analyzeContext = new MultiSolverContext();
                String alias = multiSearchCondition.alias();
                if (StrUtil.isBlank(alias)) {
                    alias = field.getName();
                }
                String columnName = multiSearchCondition.columnName();
                if (StrUtil.isBlank(columnName)) {
                    columnName = StrUtil.toUnderlineCase(field.getName());
                }
                String tableAlias = aliasTable.get(analyzeContext.getTableName());
                if (StrUtil.isBlank(tableAlias)) {
                    continue;
                }
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

        SimpleCache<String, String> aliasTable;

        public FromItemVisitorImpl(SimpleCache<String, String> aliasTable) {
            this.aliasTable = aliasTable;
        }

        @Override
        public void visit(Table table) {
            aliasTable.put(table.getName().trim(), table.getAlias().getName().trim());
        }
    }
}
