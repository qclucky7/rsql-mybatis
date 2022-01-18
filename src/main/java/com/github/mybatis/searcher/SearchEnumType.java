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
public @interface SearchEnumType {
}
