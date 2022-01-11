package com.github.mybatis.searcher.convert;

/**
 * @author WangChen
 * @since 2022-01-07 16:34
 **/
public class EmptyConverter implements SearchConverter<Object> {
    @Override
    public Object convert(String target) {
        return target;
    }
}
