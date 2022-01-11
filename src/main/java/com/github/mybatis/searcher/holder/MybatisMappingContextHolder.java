package com.github.mybatis.searcher.holder;

/**
 * @author WangChen
 * @since 2021-12-04 14:40
 **/
public class MybatisMappingContextHolder {

    private static ThreadLocal<MappingContext> mappingHolder = new ThreadLocal<>();

    public static void constructMappingContext(MappingContext payload){
        mappingHolder.set(payload);
    }

    public static void constructMappingParameter(String property, Class<?> type, Object value) {
        mappingHolder.get().addMapping(property, type)
                .additionalParameters(property, value);

    }

    public static MappingContext getMappingContext(){
        return mappingHolder.get();
    }

    public static void clear(){
        mappingHolder.remove();
    }

}
