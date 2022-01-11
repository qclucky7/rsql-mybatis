package com.github.mybatis.searcher.tools;

/**
 * @author WangChen
 * @since 2022-01-05 09:24
 **/
public interface Builder<T> {

    /**
     * 建造者方法
     * @return T
     */
    T build();
}
