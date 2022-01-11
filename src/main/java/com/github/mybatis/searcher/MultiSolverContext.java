package com.github.mybatis.searcher;

import com.github.mybatis.searcher.solver.SolverContext;

/**
 * @author WangChen
 * @since 2021-10-25 11:10
 **/
public class MultiSolverContext extends SolverContext {

    /**
     * 表名
     */
    private String tableName;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public String toString() {
        return "MultiSolverContext{" +
                "tableName='" + tableName + '\'' +
                "} " + super.toString();
    }
}
