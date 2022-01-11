package com.github.mybatis.searcher.solver;

import java.lang.reflect.Field;
import java.util.Set;

/**
 * @author WangChen
 * @since 2021-10-22 13:57
 **/
public class SolverContext {

    private Class<?> target;
    private String columnName;
    private Field field;
    private Set<String> support;

    public Class<?> getTarget() {
        return target;
    }

    public void setTarget(Class<?> target) {
        this.target = target;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

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
        return "SolverContext{" +
                "target=" + target +
                ", columnName='" + columnName + '\'' +
                ", field=" + field +
                ", support=" + support +
                '}';
    }
}
