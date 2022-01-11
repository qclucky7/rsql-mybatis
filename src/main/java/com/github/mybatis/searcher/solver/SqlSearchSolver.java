package com.github.mybatis.searcher.solver;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.mybatis.searcher.*;
import com.github.mybatis.searcher.solver.operators.ExtensionRSQLOperators;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.RSQLParserException;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author WangChen
 * @since 2021-10-14 08:49
 **/
public final class SqlSearchSolver {

    private static final Logger logger = LoggerFactory.getLogger(SqlSearchSolver.class);
    private static RSQLParser rsqlParser;

    static {
        rsqlParser = new RSQLParser(ExtensionRSQLOperators.getAllOperators());
    }

    /**
     * 查询转换对象
     *
     * @param target 转换目标类
     * @param search search
     * @return SearchBodyAccessor searchBodyAccessor
     */
    public static SearchBodyAccessor solve(Class<?> target, String search){
        if (StrUtil.isBlank(search)){
            return SearchBodyAccessor.empty();
        }
        try {
            return rsqlParser.parse(search).accept(new QueryBodyVisitor(target));
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("[SqlSearchSolver] parsing search string fail！ target:{}, search:{}", target, search);
            }
            return SearchBodyAccessor.empty();
        }
    }

    /**
     * 尝试解析查询字符串, 为解析成功直接跳过 不去解析sql语句。
     * @param search search
     * @return Node node
     */
    public static Node tryToSolve(String search){
        if (StrUtil.isBlank(search)){
            return null;
        }
        try {
            return rsqlParser.parse(search);
        } catch (RSQLParserException e) {
            return null;
        }
    }

    /**
     * 查询对象解析sql
     *
     * @param node node
     * @param plainSelect plainSelect
     * @param target      target
     * @param search      search
     */
    public static void solve(Node node, PlainSelect plainSelect, Class<?> target, String search) {
        if (StrUtil.isBlank(search) || ObjectUtil.isNull(target)) {
            return;
        }
        RSQLVisitor<PlainSelect, Void> visitor;
        if (CollUtil.isEmpty(plainSelect.getJoins())) {
            visitor = new SingleSearchVisitor(plainSelect, target);
        } else {
            visitor = new MultiSearchVisitor(plainSelect, target);
        }
        try {
            node.accept(visitor);
        } catch (Exception ex) {
            if (logger.isDebugEnabled()) {
                logger.debug("[SqlSearchSolver] parsing search string fail！ target:{}, search:{}, error: {}", target, search, ExceptionUtil.stacktraceToString(ex));
            }
        }
    }

}
