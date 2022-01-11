package com.github.mybatis.searcher.solver;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author WangChen
 * @since 2021-10-14 12:18
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {TYPE})
public @interface Symbol {

    /**
     * 关键字
     *
     * @return String
     */
    SolverType value();

}
