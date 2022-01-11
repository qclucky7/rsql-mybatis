package com.github.mybatis.searcher.tools;

/**
 * @author WangChen
 * @since 2022-01-05 15:41
 **/
@FunctionalInterface
public interface Callback {

    /**
     * 回调函数
     */
    void execute();
}
