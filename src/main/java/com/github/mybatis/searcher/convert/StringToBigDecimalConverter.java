package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;

/**
 * @author WangChen
 * @since 2022-01-08 14:01
 **/
public class StringToBigDecimalConverter implements SearchConverter<BigDecimal>{
    @Override
    public BigDecimal convert(String target) {
        return NumberUtil.toBigDecimal(target);
    }
}
