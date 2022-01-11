package com.github.mybatis.searcher;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;

/**
 * @author WangChen
 * @since 2021-12-09 14:30
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD})
@Inherited
public @interface SearchableField {

    /**
     * 查询别名 不填默认属性名
     *
     * @return String
     */
    String alias() default "";

    /**
     * 列名 不填默认驼峰
     *
     * @return String
     */
    String columnName() default "";
}
