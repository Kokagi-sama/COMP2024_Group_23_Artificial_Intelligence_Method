package com.aim.coursework.AFDO;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class AdaptiveFitnessDependentOptimizer {

    // Parameters
    static final int POPULATION_SIZE = 10;
    static final int T_MAX = 100;

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

            for(Solution X_it : initial_population) {

                Solution X_it_plus_one = Heuristics.copySolution(X_it);

                double f_X_it = X_it.getObjectiveFunctionValue();

                double wf = 0;

                double fw = Math.abs((f_X_star_it / f_X_it)) * wf;

                if (fw == 1 || fw == 0 || f_X_it == 0) {
                    double r = rand.nextDouble();
                    
                    ArrayList<Item> allSolutionItems = X_it.getAllBinItems();

                    System.out.println("Total No. of Bins: " + X_it_plus_one.getBinCount());

                    for (Item item: allSolutionItems) {
                        System.out.println("Item ID: " + item.getItemId());
                        System.out.println("Bin ID: " + item.getBinId());
                        System.out.println();
                    }

                    int number_of_items_in_solution = allSolutionItems.size();
                    //System.out.println(number_of_items_in_solution);
                    
                    int pace = (int) Math.round(number_of_items_in_solution * r);
                    //System.out.println(r);
                    //System.out.println(pace);

                    ArrayList<Integer> selectedItemIndexes = new ArrayList<Integer>();

                    while (selectedItemIndexes.size() < pace) {
                        int randomIndex = rand.nextInt(allSolutionItems.size());
                        if (!selectedItemIndexes.contains(randomIndex)) {
                            selectedItemIndexes.add(randomIndex);
                        }
                    }

                    for (Integer index : selectedItemIndexes) {
                        //System.out.println("Item Index: " + index);
                        Item item = allSolutionItems.get(index);
                        int current_bin_id = item.getBinId();
                        //System.out.println("Item ID: " + item.getItemId());
                        //System.out.println("Bin ID: " + current_bin_id);
                        
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
                            currentBin.getItems().remove(item);
                            currentBin.setRemainingCapacity(currentBin.getRemainingCapacity() + item.getWeight());
                            item.setBinId(new_random_bin_id);
                            newBin.getItems().add(item);
                            newBin.setRemainingCapacity(newBin.getRemainingCapacity() - item.getWeight());
                        }
                        
                    }

                    double newObjectiveValue = Heuristics.objectiveFunction(X_it_plus_one);
                    if (newObjectiveValue > f_X_it) {
                        X_it_plus_one.setObjectiveFunctionValue(newObjectiveValue);

                        // System.out.println("Bin of: " + X_it_plus_one.getProblemName());
                        
                        // for (Bin bin : X_it_plus_one.getBins()) {
                        //     for (Item item : bin.getItems()) {
                        //         System.out.println("Item ID: " + item.getItemId());
                        //         System.out.println("Bin ID: " + item.getBinId());
                        //         System.out.println();
                        //     }
                        // }
                        
                        initial_population.set(initial_population.indexOf(X_it), X_it_plus_one);
                    }
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

        }

        // Set the best solution from the last generation
        final_solution.setBestSolution(generation_results.get(generation_results.size() - 1).getBestSolution());
        final_solution.setGenerations(generation_results);

        return final_solution;
    }
}