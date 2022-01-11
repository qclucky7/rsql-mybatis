package com.github.mybatis.searcher.convert;

/**
 * @author WangChen
 * @since 2022-01-07 17:08
 **/
public class StringToStringConverter implements SearchConverter<String>{
    @Override
    public String convert(String target) {
        return target;
    }
}
