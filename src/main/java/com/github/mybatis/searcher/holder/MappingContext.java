package com.github.mybatis.searcher.holder;

import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.session.Configuration;

import java.util.List;
import java.util.Map;

/**
 * @author WangChen
 * @since 2021-12-04 14:46
 **/
public class MappingContext {

    private Configuration configuration;
    private List<ParameterMapping> mappings;
    private Map<String, Object> additionalParameters;

    public MappingContext(Configuration configuration, List<ParameterMapping> mappings, Map<String, Object> additionalParameters) {
        this.configuration = configuration;
        this.mappings = mappings;
        this.additionalParameters = additionalParameters;
    }

    public MappingContext addMapping(String property, Class<?> type) {
        this.mappings.add(new ParameterMapping.Builder(this.configuration, property, type).build());
        return this;
    }

    public MappingContext additionalParameters(String property, Object value) {
        this.additionalParameters.put(property, value);
        return this;
    }

    public List<ParameterMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<ParameterMapping> mappings) {
        this.mappings = mappings;
    }

    public Map<String, Object> getAdditionalParameters() {
        return additionalParameters;
    }

    public void setAdditionalParameters(Map<String, Object> additionalParameters) {
        this.additionalParameters = additionalParameters;
    }
}
