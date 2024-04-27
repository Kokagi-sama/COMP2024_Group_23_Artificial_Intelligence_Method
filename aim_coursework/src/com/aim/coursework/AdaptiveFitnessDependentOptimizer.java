package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class AdaptiveFitnessDependentOptimizer {

    // Parameters
    private static final int POPULATION_SIZE = 10;
    private static final int T_MAX = 100;

    Random rand = new Random();

    private ArrayList<Solution> initial_population;
    private BinPackingProblem problem;

    public AdaptiveFitnessDependentOptimizer(BinPackingProblem problem) {
        this.problem = problem;

        // Initialising initial population
        Initialise initialiser = new Initialise(this.problem);
        this.initial_population = initialiser.getInitialPopulation(POPULATION_SIZE);
    }

    public FinalSolution applyAdaptiveFitnessDependentOptimizerAlgorithm() {
        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();

        // Global best no. of bins
        int b_star = -1;

        for (int generation_id = 1; generation_id < T_MAX; generation_id++) {

            // Initialize best solution with the first solution
            Solution X_star_it = initial_population.getFirst();
            double f_X_star_it = X_star_it.getObjectiveFunctionValue();

            for (Solution X_it : initial_population) {
                double f_X_it = X_it.getObjectiveFunctionValue();

                if (f_X_it > f_X_star_it) {
                    X_star_it = X_it;
                    f_X_star_it = f_X_it;
                }
            }

            b_star = X_star_it.getBinCount();

            for(Solution X_it : initial_population) {

                double r = rand.nextDouble();

                Solution X_it_plus_one = Heuristics.copySolution(X_it);

                double f_X_it = X_it.getObjectiveFunctionValue();

                double wf = 1;

                double fw = Math.abs((1-(f_X_star_it)) / (1-(f_X_it))) * wf;

                ArrayList<Item> allSolutionItems = X_it.getAllBinItems();

                ArrayList<Item> copyAllSolutionItems = Heuristics.copyItems(allSolutionItems);

                ArrayList<Item> bestSolutionItems = X_star_it.getAllBinItems();

                int pace = 0;

                boolean stable_walk = (fw == 1 || fw == 0 || f_X_it == 0);

                // AFDO Walk
                ArrayList<Integer> selectedItemIndexes = new ArrayList<Integer>();


                if (stable_walk) {
                    int number_of_items_in_solution = allSolutionItems.size();
                    pace = (int) Math.round(number_of_items_in_solution * r);
                }

                else {

                    // Iterate over one list and check if the other list contains each item
                    for (Item item : bestSolutionItems) {
                        // Use removeIf to remove items based on item ID and bin ID
                        copyAllSolutionItems.removeIf(copyItem -> 
                            copyItem.getItemId() == item.getItemId() && copyItem.getBinId() == item.getBinId());
                    }

                    int number_of_items_in_copy_solution = copyAllSolutionItems.size();
                    
                    pace = (int) Math.round(number_of_items_in_copy_solution * fw);
                }
                
                while (selectedItemIndexes.size() < pace) {
                    int randomIndex = rand.nextInt(stable_walk ? allSolutionItems.size() : copyAllSolutionItems.size());
                    if (!selectedItemIndexes.contains(randomIndex)) {
                        selectedItemIndexes.add(randomIndex);
                    }
                }

                for (Integer index : selectedItemIndexes) {
                    Item item = stable_walk ? allSolutionItems.get(index) : copyAllSolutionItems.get(index);
                    int current_bin_id = item.getBinId();
                    
                    Bin currentBin = X_it_plus_one.getBins().get(current_bin_id - 1);
                    int new_random_bin_id = current_bin_id;

                    while (new_random_bin_id == current_bin_id) {
                        // new_random_bin_id is always between 1 and the number of bins (inclusive)
                        new_random_bin_id = rand.nextInt(X_it.getBinCount()) + 1;
                    }

                    Bin newBin = X_it_plus_one.getBins().get(new_random_bin_id - 1);

                    // Check capacity before moving item
                    if (newBin.getRemainingCapacity() >= item.getWeight()) {
                        // Move item
                        if (currentBin.getItems().removeIf(i -> i.getItemId() == item.getItemId())) {
                            currentBin.setRemainingCapacity(currentBin.getRemainingCapacity() + item.getWeight());
                            item.setBinId(new_random_bin_id);
                            newBin.getItems().add(item);
                            newBin.setRemainingCapacity(newBin.getRemainingCapacity() - item.getWeight());
                        }
                    }
                    
                }

                // Calculate objective function value for new solution and replace with current solution if required
                double newObjectiveValue = Heuristics.objectiveFunction(X_it_plus_one);

                if (newObjectiveValue > f_X_it) {
                    X_it_plus_one.setObjectiveFunctionValue(newObjectiveValue);
                    initial_population.set(initial_population.indexOf(X_it), X_it_plus_one);
                }
            }

            // Store generation results
            Generation generation = new Generation();
            generation.setGenerationId(generation_id);
            generation.setCandidateSolutions(new ArrayList<>(initial_population));
            generation.setBestSolution(initial_population.stream()
                                                        .max(Comparator.comparingDouble(Solution::getObjectiveFunctionValue))
                                                        .orElse(null));
            generation_results.add(generation);

            // Terminate if lower bound has been achieved
            if (b_star <= Heuristics.calculateLowerBound(generation.getBestSolution().getBins(), generation.getBestSolution().getBins().getFirst().getCapacity())) {
                break;
            }

        }

        // Set the best solution from the last generation
        final_solution.setBestSolution(generation_results.get(generation_results.size() - 1).getBestSolution());
        final_solution.setGenerations(generation_results);

        return final_solution;
    }
}