package com.github.mybatis.searcher;

import cn.hutool.core.util.ObjectUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author WangChen
 * @since 2022-01-06 15:18
 **/
public class ParseRepeatCounter {

    private Map<String, Integer> parseCounter = new HashMap<>();

    public boolean alreadyParsed(String selectorSymbol) {
        Integer integer = parseCounter.get(selectorSymbol);
        if (ObjectUtil.isNull(integer) || integer == 0) {
            parseCounter.put(selectorSymbol, 1);
            return false;
        }
        return true;
    }
}
