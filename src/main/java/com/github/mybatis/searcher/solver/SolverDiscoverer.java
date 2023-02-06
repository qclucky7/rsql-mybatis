package com.github.mybatis.searcher.solver;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.util.ServiceLoaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author WangChen
 * @since 2021-10-14 14:56
 **/
public final class SolverDiscoverer {

    private static final Logger logger = LoggerFactory.getLogger(SolverDiscoverer.class);
    private static final Map<String, Solver> SOLVERS = new ConcurrentHashMap<>(SolverType.values().length);

    public static Class<SolverDiscoverer> load(){
        return SolverDiscoverer.class;
    }

    public static Solver lookup(String symbol) {
        return SOLVERS.get(symbol);
    }

    static {
        ServiceLoader<Solver> loader = ServiceLoaderUtil.load(Solver.class);
        for (Solver conditionSolver : loader) {
            final Symbol annotation = AnnotationUtil.getAnnotation(conditionSolver.getClass(), Symbol.class);
            for (String symbol : annotation.value().getOperator().getSymbols()) {
                logger.info("[mybatis searcher] query sql solver loading {} , class: {}", symbol, conditionSolver.toString());
                SOLVERS.put(symbol, conditionSolver);
            }
        }
        Set<String> symbols = SOLVERS.keySet();
        logger.info("[mybatis searcher] query sql solver loaded, size:{} symbol: {},", symbols.size(), symbols.toString());
    }

}
