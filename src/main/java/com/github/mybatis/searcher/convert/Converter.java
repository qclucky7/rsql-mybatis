package com.github.mybatis.searcher.convert;

/**
 * @author WangChen
 * @since 2022-01-07 17:49
 **/
public interface Converter<T, R> {

    /**
     * 转换器
     *
     * @param target 目标类型
     * @return {@code R}
     */
    R convert(T target);
}
