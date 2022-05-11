package com.github.mybatis.searcher.convert;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(ConverterFactory.class);

    private static final Map<Class<?>, SearchConverter<?>> SEARCH_CONVERTER_MAP = new ConcurrentHashMap<>(13);
    private static final Map<Class<?>, SearchConverter<?>> CUSTOM_CONVERTER_MAP = new ConcurrentHashMap<>();

    public static SearchConverter<?> lookup(Class<?> type) {
        final SearchConverter<?> searchConverter = CUSTOM_CONVERTER_MAP.get(type);
        if (ObjectUtil.isNull(searchConverter)) {
            return SEARCH_CONVERTER_MAP.get(type);
        }
        return searchConverter;
    }


    public static Object lookupToConvert(Class<?> type, String param){
        final SearchConverter<?> searchConverter = lookup(type);
        if (ObjectUtil.isNull(searchConverter)) {
            return param;
        }
        //自定义转换器转换失败不抛出异常
        try {
            return searchConverter.convert(param);
        } catch (Exception e) {
            logger.error("[ConverterFactory] converter: {} param: {} error: {}", type, param, e.getMessage());
            if (logger.isDebugEnabled()){
                logger.error("[ConverterFactory] converter: {} param: {} error: {}", type, param, ExceptionUtil.stacktraceToString(e));
            }
            return param;
        }
    }


    public static Object lookupToConvert(Field field, String param) {
        if (ObjectUtil.isNull(field)){
            return null;
        }
        return lookupToConvert(field.getType(), param);
    }


    public static List<?> lookupToConvert(Class<?> type, List<String> params) {
        if (CollUtil.isEmpty(params)) {
            return Collections.emptyList();
        }
        final SearchConverter<?> searchConverter = lookup(type);
        if (ObjectUtil.isNull(searchConverter)) {
            return params;
        }
        return params.stream()
                .map(param -> {
                    try {
                        return searchConverter.convert(param);
                    } catch (Exception e) {
                        logger.error("[ConverterFactory] converter: {} param: {} error: {}", type, param, e.getMessage());
                        if (logger.isDebugEnabled()){
                            logger.error("[ConverterFactory] converter: {} param: {} error: {}", type, param, ExceptionUtil.stacktraceToString(e));
                        }
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public static List<?> lookupToConvert(Field field, List<String> params) {
        if (ObjectUtil.isNull(field)){
            return Collections.emptyList();
        }
        return lookupToConvert(field.getType(), params);
    }


    /**
     * 加载所有自定义转换器
     *
     * @param field           field
     * @param searchConverter searchConverter
     */
    public static void loadingCustomConverter(Field field, SearchConverter<?> searchConverter) {
        CUSTOM_CONVERTER_MAP.put(field.getType(), searchConverter);
    }

    static {
        SEARCH_CONVERTER_MAP.put(Integer.TYPE, new StringToIntegerConverter());
        SEARCH_CONVERTER_MAP.put(Long.TYPE, new StringToLongConverter());
        SEARCH_CONVERTER_MAP.put(Float.TYPE, new StringToFloatConverter());
        SEARCH_CONVERTER_MAP.put(Double.TYPE, new StringToDoubleConverter());
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
