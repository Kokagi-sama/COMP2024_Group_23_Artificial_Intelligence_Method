package com.aimframeworkgrp23;

import java.util.ArrayList;

public class Heuristics {

    // Objective function
    public static double objectiveFunction(Solution solution, int z) {
        double fitness = 0;
    
        for (Bin bin : solution.getBins()) {
            double load = (bin.getCapacity() - bin.getRemainingCapacity()) == 10000 ? 0 : (double) (bin.getCapacity() - bin.getRemainingCapacity());
            double filledRatio = load / (double) bin.getCapacity();
            double load_square = Math.pow(filledRatio, z);
            fitness += load_square;
        }
    
        return fitness / solution.getBinCount();
    }

    // Overloaded objective function with default z value = 2
    public static double objectiveFunction(Solution solution) {
        return objectiveFunction(solution, 2);
    }
    
    // Objective function for Arraylist of Bins passed directly
    public static double objectiveFunction (ArrayList<Bin> bins, int z) {
        double fitness = 0;

        for (Bin bin : bins) {
            double load = (bin.getCapacity() - bin.getRemainingCapacity()) == 10000 ? 0 : (double) (bin.getCapacity() - bin.getRemainingCapacity());
            double filledRatio = load / (double) bin.getCapacity();
            double load_square = Math.pow(filledRatio, z);
            fitness += load_square;
        }

        return fitness / bins.size();
    }

    // Overloaded objective function with default z value = 2 for ArrayList of Bins passed directly
    public static double objectiveFunction(ArrayList<Bin> bins) {
        return objectiveFunction(bins, 2);
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
                copiedItem.setBinId(originalItem.getBinId());
                copiedItem.setItemId(originalItem.getItemId());
                copiedBin.getItems().add(copiedItem);
            }

            copySolution.getBins().add(copiedBin);
        }

        copySolution.setObjectiveFunctionValue(original.getObjectiveFunctionValue());
        copySolution.setBinCount(copySolution.getBins().size());

        return copySolution;
    }

    public static ArrayList<Item> copyItems(ArrayList<Item> items) {
        ArrayList<Item> copyItems = new ArrayList<Item>();
    
        for (Item original : items) {
            // Create a new Item object with the same weight
            Item newItem = new Item(original.getWeight());
            
            // Set the same binId and itemId as the original item
            newItem.setBinId(original.getBinId());
            newItem.setItemId(original.getItemId());
    
            // Add the newly created item to the copied list
            copyItems.add(newItem);
        }
    
        return copyItems;
    }

    public static int calculateLowerBound(ArrayList<Bin> bins, int bin_capacity) {
        int lower_bound = -1;
        double total_bin_weight = 0.0;

        for (Bin bin: bins) {
            for (Item item: bin.getItems()) {
                total_bin_weight += item.getWeight();
            }
        }

        lower_bound = (int) Math.ceil(total_bin_weight / bin_capacity);

        return lower_bound;
    }
}

