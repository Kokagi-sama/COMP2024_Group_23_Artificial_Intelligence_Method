package com.aimframeworkgrp23;

import java.util.ArrayList;

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

    public ArrayList<Solution> getInitialPopulation(int population_size) {
        ArrayList<Solution> initialPopulation = new ArrayList<>();
        for (int i = 0; i < population_size; i++) {
            // Create a new instance of the problem for each solution in the population
            BinPackingProblem problemInstance = new BinPackingProblem(
                this.problem.getProblemName(),
                this.problem.getUniqueWeightCount(),
                this.problem.getBinCapacity()
            );
    
            // Add all original items to the problem instance
            for (Item item : this.problem.getItems()) { // This assumes getItems returns the original set with IDs
                problemInstance.addItem(item.getWeight());
            }
    
            // Randomize the items within this problem instance
            ArrayList<Item> randomizedItems = problemInstance.randomizeItems();

            problemInstance.setItems(randomizedItems);
    
            // Create a new solution instance using the randomized items
            FirstFitAlgorithm ff = new FirstFitAlgorithm(problemInstance);
            Solution randomizedSolution = ff.applyFirstFit();
            initialPopulation.add(randomizedSolution);
        }
        return initialPopulation;
    }
}