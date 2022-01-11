package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * @author WangChen
 * @since 2022-01-07 17:21
 **/
public class StringToLocalDateConverter implements SearchConverter<LocalDate> {
    @Override
    public LocalDate convert(String target) {
        if (NumberUtil.isLong(target)) {
            return Instant.ofEpochMilli(Long.parseLong(target)).atZone(ZoneOffset.systemDefault()).toLocalDate();
        }
        return null;
    }
}
