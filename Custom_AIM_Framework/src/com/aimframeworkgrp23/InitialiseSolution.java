package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;

public class InitialiseSolution {

    public static Map<String, Solution> initSolution(LinkedHashSet<BinPackingProblem> problems) {
        return initialiseSolution(problems);
    }

    public static Map<String, Solution> initialiseSolution(LinkedHashSet<BinPackingProblem> problems) {
        Map<String, Solution> solutionsMap = new LinkedHashMap<>();
        int iteration = 0;
    
        for (BinPackingProblem problem : problems) {
            Solution solution = new Solution(problem.getProblemName());
            List<Bin> bins = new ArrayList<>();
    
            for (Item item : problem.getItems()) {
                Bin newBin = new Bin(bins.size() + 1, problem.getBinCapacity(), problem.getBinCapacity());
                newBin.getItems().add(item);
                newBin.setRemainingCapacity(newBin.getRemainingCapacity() - item.getWeight());
                bins.add(newBin);
                
                iteration++;
                solution.addIterationState(iteration, new IterationState(iteration, bins.size(), new ArrayList<>(bins)));
            }
            solutionsMap.put(problem.getProblemName(), solution);
            
            iteration = 0; // Reset iteration for each problem
        }
    
        return solutionsMap;
    }
}