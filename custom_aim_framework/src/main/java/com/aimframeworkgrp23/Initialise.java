package com.aimframeworkgrp23;

public class Initialise {
    BinPackingProblem problem;
    Solution solution;

    public Initialise(BinPackingProblem problem) {
        this.problem = problem;
        this.solution = new Solution();
    }

    public Solution getInitialSolution() {
        FirstFitAlgorithm ff = new FirstFitAlgorithm(this.problem);
        solution = ff.applyFirstFit();
        return solution;
    }

    // Make getInitialPopulation() for GA.
}