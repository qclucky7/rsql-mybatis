package com.github.mybatis.searcher;

import java.util.Collection;

/**
 * @author WangChen
 * @since 2022-01-06 14:18
 **/
public class SearchBodyAttribute implements SearchBodyAttributeAccessor {

    private final String attributeName;
    private final Collection<Object> attributeValue;
    private final String symbol;

    public SearchBodyAttribute(String attributeName, Collection<Object> attributeValue, String symbol) {
        this.attributeName = attributeName;
        this.attributeValue = attributeValue;
        this.symbol = symbol;
    }

    @Override
    public String getAttributeName() {
        return attributeName;
    }

    @Override
    public Collection<Object> getAttributeValues() {
        return attributeValue;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }


    @Override
    public String toString() {
        return "SearchBodyAttribute{" +
                "attributeName='" + attributeName + '\'' +
                ", attributeValue=" + attributeValue +
                ", symbol='" + symbol + '\'' +
                '}';
    }
}
