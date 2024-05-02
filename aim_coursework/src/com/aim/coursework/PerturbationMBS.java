package com.aim.coursework;


import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

// Sufficient Average Weight Minimum Bin Slack Algorithm
public class PerturbationMBS {

    // Parameters
    private static final int MAX_ITERATIONS = 6900;

    private Solution initial_solution;

    // To store multiple generations' results
    ArrayList<Generation> generation_results = new ArrayList<>();

    // To store final solution
    FinalSolution final_solution = new FinalSolution();

    public PerturbationMBS(Solution solution) {
        this.initial_solution = solution;
    }

    public ArrayList<Bin> firstFit(ArrayList<Item> items, String problem_name, int binCapacity) {
        ArrayList<Bin> bins = new ArrayList<>();

        int binId = 0;

        for (Item item : items) {
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
                Bin newBin = new Bin(binId, binCapacity, binCapacity - item.getWeight());
                newBin.getItems().add(item);
                bins.add(newBin);
                item.setBinId(binId);
            }
        }
        return bins;
    }


    public Solution perturbSolution(Solution current_solution, int maxIterations, int lowerBound) {
        int generation_id = 1;
        boolean improvement = true;

        ArrayList<Bin> bins = new ArrayList<>(current_solution.getBins());
        Solution bestSolution = new Solution();
        bestSolution.setObjectiveFunctionValue(0);
        Solution bestGenerationSolution = null;

        while (improvement && generation_id <= maxIterations) {

            Random rand2 = new Random(); // Create the first Random object
            int seed = rand2.nextInt(lowerBound); // Generate a random integer to use as a seed
            Random rand = new Random(seed); // Create the second Random object using the generated seed

            // Check lower bound
            if (bins.size() <= lowerBound) {
                System.out.println(String.format("Reached lower bound of %d, terminating the algorithm.", lowerBound));
                break;
            }

            bestGenerationSolution = new Solution();

            improvement = false;
            generation_id++;

            // Flatten all items and shuffle
            ArrayList<Item> allItems = new ArrayList<>();
            for (Bin bin : bins) {
                allItems.addAll(bin.getItems());
            }

            Collections.shuffle(allItems, rand);

            // Repack items using First Fit
            ArrayList<Bin> newBins = firstFit(allItems, current_solution.getProblemName(), bins.getFirst().getCapacity());

            if (newBins.size() < bins.size()) {
                bins = Heuristics.copyBins(newBins);
                improvement = true;
            }

            // Try to reduce bins further by moving items
            for (int i = 0; i < bins.size(); i++) {
                //System.out.println("In first for loop");
                Bin bin = bins.get(i);

                // Check for empty bin
                if (bin.getItems().isEmpty()) {
                    continue;
                }

                int j = 0;

                while (j < bin.getItems().size()) {

                    Item item = bin.getItems().get(j);

                    for (Bin otherBin : bins) {
                        if (otherBin.getId() != i && otherBin.getRemainingCapacity() >= item.getWeight()) {

                            if (bin.getItems().removeIf(copyItem -> 
                            copyItem.getItemId() == item.getItemId() && copyItem.getBinId() == item.getBinId())) {

                                bin.setRemainingCapacity(bin.getRemainingCapacity() + item.getWeight());

                                otherBin.getItems().add(item);
                                otherBin.setRemainingCapacity(otherBin.getRemainingCapacity() - item.getWeight());
                                item.setBinId(otherBin.getId());

                                improvement = true;
                                break;
                            }
                        }
                    }
                    j++;
                }
            }
            
            // Clean up empty bins
            for (int i = 0; i < bins.size(); i++) {
                Bin bin = bins.get(i);

                if (bin.getItems().isEmpty()) {
                    bins.remove(i);
                }
            }

            // Re-number bin ids and item ids
            for (int binId = 0; binId < bins.size(); binId++) {
                Bin bin = bins.get(binId);

                bin.setId(binId);

                for (Item item: bin.getItems()) {
                    item.setBinId(binId);
                }
            }

            // Attempt to consolidate bins by trying to empty bins if possible
            for (int i = 0; i < bins.size(); i++) {

                Bin bin = bins.get(i);

                for (int j = 0; j < bin.getItems().size(); j++) {

                    Item item = bin.getItems().get(j);

                    int best_bin_index = -1;
                    int minimum_slack_after_move = 10001;

                    for (Bin otherBin : bins) {

                        if (otherBin.getId() != i && otherBin.getRemainingCapacity() >= item.getWeight()) {

                            Bin tempBin = Heuristics.copyBin(otherBin);

                            tempBin.getItems().add(item);
                            tempBin.setRemainingCapacity(tempBin.getRemainingCapacity() - item.getWeight());

                            int slack_after_move = tempBin.getRemainingCapacity();

                            if (slack_after_move < minimum_slack_after_move) {
                                minimum_slack_after_move = slack_after_move;
                                best_bin_index = otherBin.getId();
                            }
                        }

                    }

                    if (best_bin_index != -1 && bin.getRemainingCapacity() - item.getWeight() >= minimum_slack_after_move) {

                        if (bin.getItems().removeIf(copyItem -> 
                        copyItem.getItemId() == item.getItemId() && copyItem.getBinId() == item.getBinId())) {

                            bin.setRemainingCapacity(bin.getRemainingCapacity() + item.getWeight());

                            bins.get(best_bin_index).getItems().add(item);
                            bins.get(best_bin_index).setRemainingCapacity(bins.get(best_bin_index).getRemainingCapacity() - item.getWeight());
                            item.setBinId(bins.get(best_bin_index).getId());

                            improvement = true;
                            break;
                        }

                    }
                    
                }
            }
            
            bestGenerationSolution.setProblemName(current_solution.getProblemName());
            bestGenerationSolution.setBinCount(bins.size());
            bestGenerationSolution.setBins(Heuristics.copyBins(bins));
            bestGenerationSolution.setObjectiveFunctionValue(Heuristics.objectiveFunction(bins));

            Generation generation = new Generation();
            generation.setGenerationId(generation_id);
            generation.setBestSolution(bestGenerationSolution);
            generation_results.add(generation);
        }

        for(Generation generation : generation_results) {
            Solution currentSolution = generation.getBestSolution();
            if (currentSolution != null && currentSolution.getObjectiveFunctionValue() > bestSolution.getObjectiveFunctionValue()) {
                bestSolution = currentSolution;
            }
        }
        
        return bestSolution;
    }

    
    public FinalSolution applyPerturbationMBS() {
        Solution current_solution = Heuristics.copySolution(initial_solution);

        int lower_bound = Heuristics.calculateLowerBound(current_solution.getBins(), current_solution.getBins().getFirst().getCapacity());

        Solution bestSolution = perturbSolution(current_solution, MAX_ITERATIONS, lower_bound);

        final_solution.setBestSolution(bestSolution);
        final_solution.setGenerations(generation_results);
        
        return final_solution;
    }
}