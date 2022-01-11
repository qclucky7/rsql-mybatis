package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.mybatis.searcher.AbstractSearchVisitor;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;

import java.util.*;

/**
 * @author WangChen
 * @since 2021-12-09 10:22
 **/
public abstract class AbstractSearchFieldsSetSolver implements ResultSetSolver {

    public static final String SELECTOR_FIXED_NAME = "select";
    public static final String SELECT_ALL = "*";

    @Override
    public void handle(PlainSelect plainSelect, String selector, AbstractSearchVisitor.AliasColumn aliasColumn, List<String> arguments) {
        if (!SELECTOR_FIXED_NAME.equals(selector)) {
            return;
        }
        Set<String> querySearchParameters = CollUtil.newHashSet(arguments);
        if (CollUtil.isEmpty(aliasColumn) || querySearchParameters.contains(SELECT_ALL)) {
            return;
        }
        List<SelectItem> needSelectFields = new ArrayList<>();
        Set<String> provideSearchParameters = aliasColumn.keySet();
        Set<String> filteredSelectFields = getFilteredSelectFields(provideSearchParameters, querySearchParameters);
        if (CollUtil.isEmpty(filteredSelectFields)) {
            return;
        }
        Map<String, String> shouldSelect = MapUtil.filter(aliasColumn, entry -> filteredSelectFields.contains(entry.getKey()));
        if (CollUtil.isEmpty(shouldSelect)) {
            return;
        }
        Set<String> columns = new HashSet<>(shouldSelect.values());
        for (SelectItem selectItem : plainSelect.getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {

                @Override
                public void visit(AllTableColumns allTableColumns) {
                    Table table = allTableColumns.getTable();
                    for (String column : columns) {
                        SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
                        selectExpressionItem.setExpression(new Column(table, column));
                        needSelectFields.add(selectExpressionItem);
                    }
                }

                @Override
                public void visit(AllColumns allColumns) {
                    for (String column : columns) {
                        SelectExpressionItem selectExpressionItem = new SelectExpressionItem();
                        selectExpressionItem.setExpression(new Column(column));
                        needSelectFields.add(selectExpressionItem);
                    }
                }

                @Override
                public void visit(SelectExpressionItem selectExpressionItem) {
                    Alias alias = selectExpressionItem.getAlias();
                    if (ObjectUtil.isNull(alias)) {
                        selectExpressionItem.getExpression().accept(new ExpressionVisitorAdapter() {
                            @Override
                            public void visit(Column column) {
                                if (columns.contains(column.getColumnName())) {
                                    needSelectFields.add(selectItem);
                                }
                            }
                        });
                    } else {
                        if (columns.contains(alias.getName())) {
                            needSelectFields.add(selectItem);
                        }
                    }
                }
            });
        }
        plainSelect.setSelectItems(needSelectFields);
    }


    /**
     * 获取应过滤的搜索参数
     * @param provideSearchParameters 提供的搜索参数列表
     * @param querySearchParameters 传入的搜索参数列表
     * @return {@code Set<String>}
     */
    public abstract Set<String> getFilteredSelectFields(Set<String> provideSearchParameters, Set<String> querySearchParameters);
}
