package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AntColonyOptimization {

    // Random Object
    Random rand = new Random();
    
    // Problem
    private BinPackingProblem problem;

    // Problem Items
    private ArrayList<Item> problem_items;

    // Pheromone matrix
    private double[][] pheromone_matrix;

    

    // Ant Colony Optimization Paramenters

    // Maximum number of iterations
    private static final int max_iterations = 100;

    // Number of ants
    private int number_of_ants;

    // Pheromone importance coefficient
    private static final int alpha = 1;

    // Heuristic importance coefficient
    private static final double beta = 2;

    // Max pheromone level
    private static final double tau_max = 20;

    // Pheromone evaporation coefficient
    private static final double rho = 0.95;


    // Public Constructor
    public AntColonyOptimization(BinPackingProblem problem) {
        this.problem = problem;
        this.problem_items = this.problem.getItems();
        this.number_of_ants = this.problem_items.size();
        this.pheromone_matrix =  getPheromoneMatrix(this.number_of_ants);
    }

    // Method to generate pheromone matrix
    private double[][] getPheromoneMatrix(int problem_items_size){
        double[][] new_phenomenon_matrix = new double[problem_items_size][problem_items_size];
        for (int i = 0; i < problem_items_size; i++) {
            for (int j = 0; j < problem_items_size; j++) {
                if (i != j) {
                    new_phenomenon_matrix[i][j] = tau_max;
                }
            }
        }

        return new_phenomenon_matrix;
    }

    // Updates pheromone levels for combination of items based on the generation's best solution
    public void updatePheromones(Solution generation_best_solution) {
        for (Bin bin : generation_best_solution.getBins()) {
            double bestFitness = generation_best_solution.getObjectiveFunctionValue();
            List<Item> items = bin.getItems();
            int m = 0;

            for (int i = 0; i < items.size() - 1; i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    int itemIId = items.get(i).getItemId();
                    int itemJId = items.get(j).getItemId();

                    m = getItemPairOccurrence(generation_best_solution, items.get(i), items.get(j));

                    pheromone_matrix[itemIId - 1][itemJId - 1] = (rho * pheromone_matrix[itemIId - 1][itemJId - 1]) + (m * bestFitness);
                    pheromone_matrix[itemJId - 1][itemIId - 1] = (rho * pheromone_matrix[itemJId - 1][itemIId - 1]) + (m * bestFitness);
                }
            }
        }
    }

    public int getItemPairOccurrence(Solution generation_best_solution, Item item1, Item item2) {
        int m = 0;
        
        // Iterate over each bin in the solution
        for (Bin bin : generation_best_solution.getBins()) {
            List<Item> items = bin.getItems();
            
            // Check each combination of items within the bin
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    Item first = items.get(i);
                    Item second = items.get(j);
                    
                    // Check if the current combination matches the pair of interest
                    if ((first.getWeight() == item1.getWeight() && second.getWeight() == item2.getWeight())) {
                        m++;
                    }
                }
            }
        }
        return m;
    }

    // Returns best possible item to be added into current bin according to item pheromone values calculation to get the best objective function
    public Item choose_item(ArrayList<Item> remaining_items, ArrayList<Item> current_bin_items, double[][] pheromone_matrix, int bin_capacity) {

        ArrayList<Item> remaining_items_copy = Heuristics.copyItems(remaining_items);
        ArrayList<Item> fitting_items = new ArrayList<Item>();
        ArrayList<Double> weighted_probabilities = new ArrayList<Double>();

        double pheromone_sum = 0.0;
        int division_factor = (current_bin_items.size() != 0 ? current_bin_items.size() : 1);
        double pheromone;

        boolean all_weighted_probabilities_zero = true;


        int current_bin_weight = 0;

        for (Item item : current_bin_items) {
            current_bin_weight += item.getWeight();
        }
        
        int itemIndex = 0;
        for (Item item : remaining_items_copy) {
            if (current_bin_weight + item.getWeight() <= bin_capacity) {
                fitting_items.add(item);
                item.setItemIndex(itemIndex);
                itemIndex++;
            }
        }

        for (Item item : fitting_items) {
            if (!current_bin_items.isEmpty()) {
                for (Item other_item : current_bin_items) {
                    if (item.getWeight() != other_item.getWeight()) {

                        int minIndex = Math.min(item.getItemIndex(), other_item.getItemIndex());
                        int maxIndex = Math.max(item.getItemIndex(), other_item.getItemIndex());

                        int pheromoneMatrixMaxIndex = pheromone_matrix.length - 1;

                        if (minIndex >= 0 && minIndex <= pheromoneMatrixMaxIndex && maxIndex >= 0 && maxIndex <= pheromoneMatrixMaxIndex) {
                            pheromone_sum += this.pheromone_matrix[minIndex][maxIndex];
                        }

                        else {
                            pheromone_sum += this.pheromone_matrix[0][0];
                        }
                        
                    }
                }

                pheromone = pheromone_sum / division_factor;
            }

            else {
                pheromone = tau_max;
            }

            int heuristic = item.getWeight();
            double weight_probability = Math.pow(pheromone, alpha) * Math.pow(heuristic, beta);
            weighted_probabilities.add(weight_probability);

        }
        
        // If no items can be placed, return item with -1 weight to indicate the start of a new bin.

        if (weighted_probabilities.isEmpty()) {
            return new Item(-1);
        }

        for (double weight_probability : weighted_probabilities) {
            if (weight_probability != 0) {
                all_weighted_probabilities_zero = false;
                break;
            }

            all_weighted_probabilities_zero = true;
        }

        if (all_weighted_probabilities_zero) {
            return new Item(-1);
        }

        // Get random item with calculated weighted probability
        WeightedRandomSelection wrs = new WeightedRandomSelection();
        Item chosen_item = wrs.chooseItemWithWeightedProbability(fitting_items, weighted_probabilities);

        return chosen_item;
    }

    // Main Ant Colony Optimization Function
    public FinalSolution applyHybridAntColonyOptimization() {
        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<Generation>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();
        
        // To store global best solution
        Solution global_best_solution = new Solution();
        global_best_solution.setObjectiveFunctionValue(0);

        for (int generation_id = 1; generation_id <= max_iterations; generation_id++) {

            Solution generation_best_solution = new Solution();
            generation_best_solution.setObjectiveFunctionValue(0);
            
            for (int ant = 0; ant < number_of_ants; ant++) {
                ArrayList<Item> remaining_items = Heuristics.copyItems(this.problem_items);
                Solution ant_solution = new Solution();
                ant_solution.setProblemName(this.problem.getProblemName());

                int binId = 0;
                int itemIndex = 0;
                int itemId = 1;

                for (Item rem_item : remaining_items) {
                    rem_item.setItemId(itemIndex);
                    itemIndex++;
                }

                while (!remaining_items.isEmpty()) {
                    ArrayList<Item> current_bin_items = new ArrayList<Item>();

                    while (!remaining_items.isEmpty()) {

                        int current_bin_items_sum = 0;

                        for (Item current_bin_item : current_bin_items) {
                            current_bin_items_sum += current_bin_item.getWeight();
                        }


                        Item item = choose_item(remaining_items, current_bin_items, pheromone_matrix, this.problem.getBinCapacity());

                        if (item.getWeight() == -1 || current_bin_items_sum + item.getWeight() > this.problem.getBinCapacity()) {
                            Bin bin = new Bin(binId, this.problem.getBinCapacity(), this.problem.getBinCapacity() - current_bin_items_sum);
                            ArrayList<Item> current_bin_items_copy = Heuristics.copyItems(current_bin_items);
                            bin.setItems(current_bin_items_copy);
                            ant_solution.getBins().add(bin);
                            binId++;

                            if (item.getWeight() == -1) {
                                current_bin_items.clear();
                            }

                            else {
                                current_bin_items.clear();
                                item.setBinId(binId);
                                item.setItemId(itemId);
                                current_bin_items.add(item);
                                itemId++;
                            }
                        }

                        else if (item.getWeight() != -1) {
                            item.setBinId(binId);
                            item.setItemId(itemId);
                            current_bin_items.add(item);
                            itemId++;
                        }

                        for (Item item_iterator : remaining_items) {
                            if (item.getWeight() == item_iterator.getWeight() &&  item.getWeight() != -1) {
                                remaining_items.remove(item_iterator);
                                break;
                            }
                        }
                    }

                    if (!current_bin_items.isEmpty()) {

                        int current_bin_items_sum = 0;
                        
                        for (Item current_bin_item : current_bin_items) {
                            current_bin_items_sum += current_bin_item.getWeight();
                        }

                        Bin bin = new Bin(binId, this.problem.getBinCapacity(), this.problem.getBinCapacity() - current_bin_items_sum);
                        ArrayList<Item> current_bin_items_copy = Heuristics.copyItems(current_bin_items);
                        bin.setItems(current_bin_items_copy);
                        ant_solution.getBins().add(bin);
                        binId++;
                    }
                }

                ant_solution.setBinCount(ant_solution.getBins().size());
                ant_solution.setObjectiveFunctionValue(Heuristics.objectiveFunction(ant_solution));

                if (ant_solution.getObjectiveFunctionValue() > generation_best_solution.getObjectiveFunctionValue()) {
                    generation_best_solution = Heuristics.copySolution(ant_solution);
                }

            }

            if (generation_best_solution.getObjectiveFunctionValue() > global_best_solution.getObjectiveFunctionValue()) {
                global_best_solution = Heuristics.copySolution(generation_best_solution);
            }

            // Store generation results
            Generation generation = new Generation();
            generation.setGenerationId(generation_id);
            generation.setBestSolution(generation_best_solution);
            generation_results.add(generation);

            updatePheromones(generation_best_solution);
        }

        // Store final solution
        final_solution.setBestSolution(global_best_solution);
        final_solution.setGenerations(generation_results);

        return final_solution;
    }
}