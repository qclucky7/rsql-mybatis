package com.github.mybatis.searcher.interceptor;

import cn.hutool.core.util.ObjectUtil;
import com.github.mybatis.searcher.Searchable;
import com.github.mybatis.searcher.holder.MappingContext;
import com.github.mybatis.searcher.holder.MybatisMappingContextHolder;
import com.github.mybatis.searcher.solver.SqlSearchSolver;
import com.github.mybatis.searcher.tools.PluginUtils;
import cz.jirutka.rsql.parser.ast.Node;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author WangChen
 * @since 2021-10-15 11:14
 **/
@SuppressWarnings({"rawtypes", "unchecked"})
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class SearchConditionsInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.SELECT == mappedStatement.getSqlCommandType()) {
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            Object paramObj = boundSql.getParameterObject();
            Searchable<?> searchable = null;
            if (paramObj instanceof Searchable) {
                searchable = (Searchable) paramObj;
            } else if (paramObj instanceof Map) {
                for (Object arg : ((Map) paramObj).values()) {
                    if (arg instanceof Searchable) {
                        searchable = (Searchable) arg;
                        break;
                    }
                }
            }
            if (ObjectUtil.isNull(searchable)) {
                return invocation.proceed();
            }
            Class<?> target = searchable.target();
            String searchString = searchable.getSearchString();
            //预尝试解析查询字符串, 失败不去解析SQL
            Node node = SqlSearchSolver.tryToSolve(searchString);
            if (ObjectUtil.isNull(node)){
                return invocation.proceed();
            }
            Select selectStatement = (Select) CCJSqlParserUtil.parse(boundSql.getSql());
            if (selectStatement.getSelectBody() instanceof PlainSelect) {
                try {
                    PlainSelect plainSelect = (PlainSelect) selectStatement.getSelectBody();
                    List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
                    Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("delegate.boundSql.additionalParameters");
                    Configuration configuration = mappedStatement.getConfiguration();
                    MybatisMappingContextHolder.constructMappingContext(new MappingContext(configuration, new ArrayList(parameterMappings), additionalParameters));
                    SqlSearchSolver.solve(node, plainSelect, target, searchString);
                    metaObject.setValue("delegate.boundSql.sql", plainSelect.toString());
                    metaObject.setValue("delegate.boundSql.parameterMappings", MybatisMappingContextHolder.getMappingContext().getMappings());
                    return invocation.proceed();
                } finally {
                    MybatisMappingContextHolder.clear();
                }

            }
            return invocation.proceed();
        } else {
            return invocation.proceed();
        }

    }

}
