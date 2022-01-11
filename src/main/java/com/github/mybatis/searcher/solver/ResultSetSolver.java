package com.github.mybatis.searcher.solver;

import com.github.mybatis.searcher.AbstractSearchVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;

import java.util.List;

/**
 * @author WangChen
 * @since 2021-12-09 14:59
 **/
public interface ResultSetSolver extends Solver {

    /**
     * 结果解析
     *
     * @param plainSelect plainSelect
     * @param selector    selector
     * @param aliasColumn aliasColumn
     * @param arguments   arguments
     */
    void handle(PlainSelect plainSelect, String selector, AbstractSearchVisitor.AliasColumn aliasColumn, List<String> arguments);
}
