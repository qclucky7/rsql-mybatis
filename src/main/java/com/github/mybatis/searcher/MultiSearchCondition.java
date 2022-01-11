package com.github.mybatis.searcher;

import com.github.mybatis.searcher.convert.EmptyConverter;
import com.github.mybatis.searcher.convert.SearchConverter;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;

/**
 * @author WangChen
 * @since 2021-10-18 16:08
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD})
@Inherited
public @interface MultiSearchCondition {

    /**
     * 查询别名 不填默认属性名
     *
     * @return String
     */
    String alias() default "";

    /**
     * 表名
     *
     * @return String
     */
    String tableName();

    /**
     * 列名 不填默认驼峰
     *
     * @return String
     */
    String columnName() default "";


    /**
     * 可支持的查询类型
     *
     * @return SearchType[]
     */
    SearchType[] available();

    /**
     * 转换器
     *
     * @return {@code Class<? extends SearchConverter<?>>}
     */
    Class<? extends SearchConverter<?>> converter() default EmptyConverter.class;
}
