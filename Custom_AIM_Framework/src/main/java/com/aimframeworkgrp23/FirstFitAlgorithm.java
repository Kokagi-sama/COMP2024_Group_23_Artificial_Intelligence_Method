package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

public class FirstFitAlgorithm {

    private LinkedHashSet<BinPackingProblem> problems;

    public FirstFitAlgorithm(LinkedHashSet<BinPackingProblem> problems) {
        this.problems = problems;
    }

    public Map<String, Solution> applyFirstFit() {
        Map<String, Solution> solutions = new LinkedHashMap<>();
        int iteration = 0;
    
        for (BinPackingProblem problem : problems) {
            List<Item> allItems = problem.getItems();
    
            List<Bin> bins = new ArrayList<>();
            int binId = 1;
            Solution solution = new Solution(problem.getProblemName());
    
            for (Item item : allItems) {
                boolean placed = false;
    
                for (Bin bin : bins) {
                    if (bin.getRemainingCapacity() >= item.getWeight()) {
                        bin.getItems().add(item);
                        bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                        placed = true;
                        break;
                    }
                }
    
                if (!placed) {
                    Bin newBin = new Bin(binId++, problem.getBinCapacity(), problem.getBinCapacity() - item.getWeight());
                    newBin.getItems().add(item);
                    bins.add(newBin);
                }

                iteration++;
            }
    
            // After all items have been processed, set the final state of the solution
            IterationState finalState = new IterationState(iteration, bins.size(), new ArrayList<>(bins));
            solution.setFinalState(finalState);
    
            solutions.put(problem.getProblemName(), solution);
        }
    
        return solutions;
    }
    
}