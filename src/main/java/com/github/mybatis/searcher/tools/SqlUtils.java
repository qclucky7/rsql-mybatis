package com.github.mybatis.searcher.tools;

/**
 * @author WangChen
 * @since 2021-12-17 19:06
 **/
public final class SqlUtils {

    public static String concatLike(String str, SqlLike type) {
        switch (type) {
            case LEFT:
                return "%" + str;
            case RIGHT:
                return str + "%";
            default:
                return "%" + str + "%";
        }
    }

    /**
     * like type
     */
    public enum SqlLike {
        /**
         * like left
         */
        LEFT,
        /**
         * like right
         */
        RIGHT,
        /**
         * default
         */
        DEFAULT;
    }
}
