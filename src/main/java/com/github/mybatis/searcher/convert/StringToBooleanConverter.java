package com.github.mybatis.searcher.convert;

/**
 * @author WangChen
 * @since 2023-02-08 10:32
 **/
public class StringToBooleanConverter implements SearchConverter<Boolean> {
    @Override
    public Boolean convert(String target) {
        return Boolean.parseBoolean(target);
    }
}
