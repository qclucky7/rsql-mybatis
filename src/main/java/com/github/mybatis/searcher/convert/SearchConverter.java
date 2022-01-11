package com.github.mybatis.searcher.convert;

/**
 * @author WangChen
 * @since 2022-01-07 16:25
 **/
public interface SearchConverter<R> extends Converter<String, R> {

    /**
     * 转换器
     *
     * @param target 目标类型
     * @return {@code R}
     */
    @Override
    R convert(String target);
}
