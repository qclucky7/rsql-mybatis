package com.github.mybatis.searcher;

/**
 * @author WangChen
 * @since 2021-10-25 10:41
 **/
public enum SearchType {

    /**
     * 等于
     */
    EQUAL,

    /**
     * 不等于
     */
    NOT_EQUAL,

    /**
     * 大于等于
     */
    GREATER_THAN_OR_EQUAL,

    /**
     * 大于
     */
    GREATER_THAN,

    /**
     * 小于等于
     */
    LESS_THAN_OR_EQUAL,

    /**
     * 小于
     */
    LESS_THAN,

    /**
     * in查询
     */
    IN,

    /**
     * not in查询
     */
    OUT,

    /**
     * 区间
     */
    BETWEEN,

    /**
     * 模糊查询
     */
    LIKE,

    /**
     * 右模糊
     */
    LIKE_RIGHT,

    /**
     * 可排序的
     */
    SORTABLE;

}
