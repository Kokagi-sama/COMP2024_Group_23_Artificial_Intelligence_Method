package com.aimframeworkgrp23;

import java.util.ArrayList;

public class FirstFitAlgorithm {

    private BinPackingProblem problem;
    Solution solution;

    public FirstFitAlgorithm(BinPackingProblem problem) {
        this.problem = problem;
        this.solution = new Solution();
    }

    public Solution applyFirstFit() {

        String problem_name = problem.getProblemName();
        ArrayList<Item> allItems = problem.getItems();
        ArrayList<Bin> bins = new ArrayList<>();

        int binId = 0;

        for (Item item : allItems) {
            boolean placed = false;

            for (Bin bin : bins) {
                if (bin.getRemainingCapacity() >= item.getWeight()) {
                    bin.getItems().add(item);
                    item.setBinId(binId);
                    bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                binId++;
                Bin newBin = new Bin(binId, problem.getBinCapacity(), problem.getBinCapacity() - item.getWeight());
                newBin.getItems().add(item);
                bins.add(newBin);
                item.setBinId(binId);
            }
        }
        
        solution.setProblemName(problem_name);
        solution.setBins(bins);
        solution.setBinCount(bins.size());
        solution.setObjectiveFunctionValue(Heuristics.objectiveFunction(solution));

        return solution;
    }
}