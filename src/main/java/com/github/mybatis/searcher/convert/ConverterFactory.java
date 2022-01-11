package com.github.mybatis.searcher.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author WangChen
 * @since 2022-01-07 17:43
 **/
public class ConverterFactory {

    private static final Map<Class<?>, SearchConverter<?>> SEARCH_CONVERTER_MAP = new ConcurrentHashMap<>(9);
    private static final Map<Field, SearchConverter<?>> CUSTOM_CONVERTER_MAP = new ConcurrentHashMap<>();

    public static SearchConverter<?> lookup(Field field) {
        final SearchConverter<?> searchConverter = CUSTOM_CONVERTER_MAP.get(field);
        if (ObjectUtil.isNull(searchConverter)) {
            return SEARCH_CONVERTER_MAP.get(field.getType());
        }
        return searchConverter;
    }


    public static Object lookupToConvert(Field field, String param) {
        final SearchConverter<?> searchConverter = lookup(field);
        if (ObjectUtil.isNull(searchConverter)) {
            return null;
        }
        //自定义转换器转换失败不抛出异常
        try {
            return searchConverter.convert(param);
        } catch (Exception e) {
            return null;
        }
    }


    public static List<Object> lookupToConvert(Field field, List<String> params) {
        if (CollUtil.isEmpty(params)) {
            return Collections.emptyList();
        }
        final SearchConverter<?> searchConverter = lookup(field);
        if (ObjectUtil.isNull(searchConverter)) {
            return null;
        }
        return params.stream()
                .map(param -> {
                    try {
                        return searchConverter.convert(param);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    /**
     * 加载所有自定义转换器
     *
     * @param field           field
     * @param searchConverter searchConverter
     */
    public static void loadingCustomConverter(Field field, SearchConverter<?> searchConverter) {
        CUSTOM_CONVERTER_MAP.put(field, searchConverter);
    }

    static {
        SEARCH_CONVERTER_MAP.put(String.class, new StringToStringConverter());
        SEARCH_CONVERTER_MAP.put(Integer.class, new StringToIntegerConverter());
        SEARCH_CONVERTER_MAP.put(Long.class, new StringToLongConverter());
        SEARCH_CONVERTER_MAP.put(Float.class, new StringToFloatConverter());
        SEARCH_CONVERTER_MAP.put(Double.class, new StringToDoubleConverter());
        SEARCH_CONVERTER_MAP.put(Date.class, new StringToDateConverter());
        SEARCH_CONVERTER_MAP.put(LocalDate.class, new StringToLocalDateConverter());
        SEARCH_CONVERTER_MAP.put(LocalDateTime.class, new StringToLocalDateTimeConverter());
        SEARCH_CONVERTER_MAP.put(BigDecimal.class, new StringToBigDecimalConverter());
    }
}
