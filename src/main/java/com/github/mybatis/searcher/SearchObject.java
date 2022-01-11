package com.github.mybatis.searcher;

/**
 * @author WangChen
 * @since 2021-10-18 17:39
 **/
public final class SearchObject<T> implements Searchable<T> {

    private Class<T> clazz;
    private String search;

    public SearchObject(Class<T> clazz, String search) {
        this.clazz = clazz;
        this.search = search;
    }

    @Override
    public String getSearchString() {
        return search;
    }

    @Override
    public Class<T> target() {
        return clazz;
    }

}
