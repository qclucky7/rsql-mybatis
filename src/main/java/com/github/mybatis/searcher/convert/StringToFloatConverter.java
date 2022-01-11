package com.github.mybatis.searcher.convert;

import cn.hutool.core.util.NumberUtil;

/**
 * @author WangChen
 * @since 2022-01-07 17:16
 **/
public class StringToFloatConverter implements SearchConverter<Float>{
    @Override
    public Float convert(String target) {
        return NumberUtil.isNumber(target) ? Float.valueOf(target) : null;
    }
}
