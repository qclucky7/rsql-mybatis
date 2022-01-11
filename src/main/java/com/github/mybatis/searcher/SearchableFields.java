package com.github.mybatis.searcher;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author WangChen
 * @since 2021-12-09 14:30
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {TYPE})
@Inherited
public @interface SearchableFields {
}
