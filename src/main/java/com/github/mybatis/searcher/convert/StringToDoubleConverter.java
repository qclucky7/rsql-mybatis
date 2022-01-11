package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

/**
 * @author WangChen
 * @since 2022-01-07 17:14
 **/
public class StringToDoubleConverter implements SearchConverter<Double>{
    @Override
    public Double convert(String target) {
        return NumberUtil.isDouble(target) ? Double.valueOf(target) : null;
    }
}
