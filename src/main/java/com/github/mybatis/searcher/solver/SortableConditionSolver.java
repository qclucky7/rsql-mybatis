package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

/**
 * @author WangChen
 * @since 2021-10-14 11:54
 **/
@Symbol(SolverType.SORTABLE)
public class SortableConditionSolver extends AbstractConditionSolver {

    private static final String DESC = "DESC";
    private static final String ASC = "ASC";
    private static final Set<String> SORT_CHARACTERS = CollUtil.newHashSet(DESC, ASC);

    @Override
    public ExpressionWrapper doHandle(PlainSelect plainSelect, Class<?> type, String columnName, List<String> arguments) {
        List<OrderByElement> orderByElements = plainSelect.getOrderByElements();
        if (CollUtil.isEmpty(orderByElements)) {
            plainSelect.setOrderByElements(CollUtil.newArrayList());
        }
        if (arguments.size() != 1) {
            return null;
        }
        String sortCharter = arguments.get(0).toUpperCase();
        if (!SORT_CHARACTERS.contains(sortCharter)) {
            return null;
        }
        OrderByElement orderByElement = new OrderByElement();
        switch (sortCharter) {
            case DESC:
                orderByElement.setAsc(false);
                orderByElement.setExpression(new Column(columnName));
                plainSelect.getOrderByElements().add(orderByElement);
                break;
            case ASC:
                orderByElement.setExpression(new Column(columnName));
                plainSelect.getOrderByElements().add(orderByElement);
                break;
            default:
        }
        //直接填充 不返回参数
        return null;
    }
}
