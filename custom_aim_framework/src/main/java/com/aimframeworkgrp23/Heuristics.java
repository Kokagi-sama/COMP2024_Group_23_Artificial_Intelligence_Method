package com.aimframeworkgrp23;

import java.util.ArrayList;

public class Heuristics {

    // Objective function
    public static double objectiveFunction(Solution solution, int z) {
        double fitness = 0;
    
        for (Bin bin : solution.getBins()) {
            double load = (double) (bin.getCapacity() - bin.getRemainingCapacity());
            double load_square = Math.pow(load, z);
            double filledRatio = load_square / (double) bin.getCapacity();
            fitness += filledRatio;
        }
    
        return fitness / solution.getBinCount();
    }

    // Overloaded objective function with default z value = 2
    public static double objectiveFunction(Solution solution) {
        return objectiveFunction(solution, 2);
    }

    // Generate a neighbor for each solution in the map
    public static ArrayList<Solution> generateNeighbour(Solution currentSolution, int population_size) {
        
        ArrayList<Solution> neighbourhood = new ArrayList<Solution>();

        for (int i = 0; i < population_size; i++) {
            neighbourhood.add(copySolution(currentSolution));
        }
    
        return neighbourhood;
    }    
    
    // Method to deep copy a Solution
    public static Solution copySolution(Solution original) {
        Solution copySolution = new Solution();
        copySolution.setProblemName(original.getProblemName());
        

        for (Bin originalBin : original.getBins()) {
            Bin copiedBin = new Bin(originalBin.getId(), originalBin.getCapacity(), originalBin.getRemainingCapacity());

            for (Item originalItem : originalBin.getItems()) {
                Item copiedItem = new Item(originalItem.getWeight());
                copiedBin.getItems().add(copiedItem);
            }

            copySolution.getBins().add(copiedBin);
        }

        copySolution.setObjectiveFunctionValue(original.getObjectiveFunctionValue());
        copySolution.setBinCount(copySolution.getBins().size());

        return copySolution;
    }
}

