package com.github.mybatis.searcher;

import com.github.mybatis.searcher.solver.SolverContext;

import java.util.List;

/**
 * @author WangChen
 * @since 2022-01-05 14:27
 **/
public class SolverContextWrapper {

    private final SolverContext solverContext;

    private final List<String> arguments;

    public SolverContextWrapper(SolverContext solverContext, List<String> arguments) {
        this.solverContext = solverContext;
        this.arguments = arguments;
    }

    public SolverContext getSolverContext() {
        return solverContext;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
