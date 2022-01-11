package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * @author WangChen
 * @since 2022-01-07 17:21
 **/
public class StringToLocalDateTimeConverter implements SearchConverter<LocalDateTime> {
    @Override
    public LocalDateTime convert(String target) {
        if (NumberUtil.isLong(target)) {
            return Instant.ofEpochMilli(Long.parseLong(target)).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        }
        return null;
    }
}
