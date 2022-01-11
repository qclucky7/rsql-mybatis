package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

import java.util.Date;

/**
 * @author WangChen
 * @since 2022-01-07 17:12
 **/
public class StringToDateConverter implements SearchConverter<Date> {
    @Override
    public Date convert(String target) {
        if (NumberUtil.isLong(target)) {
            return new Date(Long.parseLong(target));
        }
        return null;
    }
}
