package com.github.mybatis.searcher;

import java.io.Serializable;

/**
 * @author WangChen
 * @since 2021-10-16 11:35
 **/
public interface Searchable<T> extends Serializable{

    /**
     * 获取查询sql
     * @return String
     */
    String getSearchString();

    /**
     * 目标类
     * @return {@code Class<T>}
     */
    Class<T> target();
}
