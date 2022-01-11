package com.github.mybatis.searcher;

import java.util.Collection;

/**
 * @author WangChen
 * @since 2022-01-06 14:26
 **/
public interface SearchBodyAttributeAccessor {

    /**
     * 属性名
     *
     * @return String
     */
    String getAttributeName();

    /**
     * 获取属性值
     *
     * @return Object
     */
    Collection<Object> getAttributeValues();


    /**
     * 获取操作符
     *
     * @return String
     */
    String getSymbol();

}
