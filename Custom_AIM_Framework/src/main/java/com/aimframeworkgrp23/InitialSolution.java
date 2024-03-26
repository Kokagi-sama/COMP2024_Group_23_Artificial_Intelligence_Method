package com.aimframeworkgrp23;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.LinkedHashSet;

public class InitialSolution {

    public static Map<String, Solution> getInitialSolution(LinkedHashSet<BinPackingProblem> problems) {
        return initialiseSolution(problems);
    }

    private static Map<String, Solution> initialiseSolution(LinkedHashSet<BinPackingProblem> problems) {
        Map<String, Solution> solutionsMap = new LinkedHashMap<>();
        
        FirstFitAlgorithm ff = new FirstFitAlgorithm(problems);
        solutionsMap = ff.applyFirstFit();
    
        return solutionsMap;
    }
}