package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HybridAntColonyOptimization {

    // Random Object
    Random rand = new Random();
    
    // Problem
    private BinPackingProblem problem;

    // Problem Items
    private ArrayList<Item> problem_items;

    // Pheromone matrix
    private double[][] pheromone_matrix;

    // Heuristic Information
    private Map<Integer, Integer> heuristic_information = new HashMap<>();
    

    // HACO Paramenters

    // Maximum number of iterations
    private static final int max_iterations = 30;

    // Number of ants
    private int number_of_ants = 10;

    // Pheromone importance coefficient
    private static final int alpha = 1;

    // Heuristic importance coefficient
    private static final double beta = 2;

    // Max pheromone level
    private static final double tau_max = 1;

    // Max pheromone level
    private static final double tau_mim = 0.01;

    // Pheromone evaporation coefficient
    private static final double rho = 0.1;

    // Number of bins to free in local search
    private static final int free_n_bins = 2;

    // Public Constructor
    public HybridAntColonyOptimization(BinPackingProblem problem) {
        this.problem = problem;
        this.problem_items = this.problem.getItems();
        this.pheromone_matrix =  getPheromoneMatrix(this.problem_items.size());
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

    // Method to put free items into solution using first fit decreasing algorithm.
    public Solution firstFitDecreasing(Solution solution, ArrayList<Item> free_items, int binCapacity) {
        // Sort items by weight in decreasing order

        Solution ffdSolution = new Solution();
        ffdSolution = Heuristics.copySolution(solution);

        // System.out.println(ffdSolution.getBins().size());

        free_items.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));

        // for (Item item : items) {
        //     System.out.println(item.getWeight());
        // }

        for (Item item : free_items) {
            boolean itemAssigned = false;
            for (Bin bin : ffdSolution.getBins()) {
                if (bin.getRemainingCapacity() >= item.getWeight()) {
                    bin.getItems().add(item);
                    bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                    itemAssigned = true;
                    break;
                }
            }
            if (!itemAssigned) {
                Bin newBin = new Bin(ffdSolution.getBins().size() + 1, binCapacity, binCapacity - item.getWeight());
                newBin.getItems().add(item);
                ffdSolution.getBins().add(newBin);
                ffdSolution.setBinCount(ffdSolution.getBins().size() + 1);
            }
        }

        ffdSolution.setObjectiveFunctionValue(Heuristics.objectiveFunction(ffdSolution));

        // System.out.println(ffdSolution.getBins().size());
        return ffdSolution;
    }

    public Item choose_item(ArrayList<Item> remaining_items, ArrayList<Item> current_bin_items, double[][] pheromone_matrix, int bin_capacity) {

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
        for (Item item : remaining_items) {
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

    public FinalSolution applyHybridAntColonyOptimization() {
        // To store multiple generations' results
        ArrayList<Generation> generationResults = new ArrayList<Generation>();

        // To store final solution
        FinalSolution finalSolution = new FinalSolution();
        
        // To store global best solution
        Solution globalBestSolution = new Solution();
        globalBestSolution.setObjectiveFunctionValue(0);

        for (int generation_id = 1; generation_id <= max_iterations; generation_id++) {
            
            for (int ant = 0; ant < number_of_ants; ant++) {
                ArrayList<Item> remaining_items = Heuristics.copyItems(this.problem_items);

                int itemIndex = 0;

                for (Item item : remaining_items) {
                    item.setItemId(itemIndex);
                    itemIndex++;
                }

                while (!remaining_items.isEmpty()) {
                    ArrayList<Item> current_bin_items = new ArrayList<Item>();

                    while (!remaining_items.isEmpty()) {
                        Item item = choose_item(remaining_items, current_bin_items, pheromone_matrix, this.problem.getBinCapacity());
                    }
                }
            }

        }

        boolean break_flag = false;






        return finalSolution;
    }
}
