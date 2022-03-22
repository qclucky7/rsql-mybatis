package com.github.mybatis.searcher.tools;

import com.github.mybatis.searcher.SearchObject;
import com.github.mybatis.searcher.Searchable;

/**
 * @author WangChen
 * @since 2021-10-18 17:36
 **/
public final class Searcher {


    public static <T> SearchObjectBuilder<T> builder(Class<T> clazz) {
        return new SearchObjectBuilder<>(clazz);
    }


    public static class SearchObjectBuilder<T> implements Builder<Searchable<T>> {

        private final Class<T> clazz;
        private String searchString;

        public SearchObjectBuilder(Class<T> clazz) {
            this.clazz = clazz;
        }

        public SearchObjectBuilder<T> search(String searchString) {
            this.searchString = searchString;
            return this;
        }

        @Override
        public Searchable<T> build() {
            return new SearchObject<>(clazz, searchString);
        }
    }


}
