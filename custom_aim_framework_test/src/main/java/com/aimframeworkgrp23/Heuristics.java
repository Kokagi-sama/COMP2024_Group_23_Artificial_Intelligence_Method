package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Heuristics {

    private final Random random;

    public Heuristics() {
        this.random = new Random();
    }

    public Random getRandom() {
        return this.random;
    }

    // Assume we need to calculate the objective function for each solution in the map
    public double objectiveFunction(Map<String, Solution> solutions) {
        double fitness = 0;
        for (Solution solution : solutions.values()) {
            IterationState finalState = solution.getFinalState();
            for (Bin bin : finalState.getBins()) {
                fitness += Math.pow((bin.getCapacity() - bin.getRemainingCapacity()), 2);
            }
            
        }
        return fitness;
    }

    // Generate a neighbor for each solution in the map
    public Map<String, Solution> generateNeighbor(Map<String, Solution> currentSolutions) {
        // Deep copy all current solutions
        Map<String, Solution> newSolutions = copySolution(currentSolutions);
    
        for (Map.Entry<String, Solution> entry : newSolutions.entrySet()) {
            //String problemName = entry.getKey();
            Solution newSolution = entry.getValue();
            
            List<Bin> bins = newSolution.getFinalState().getBins();
            int iteration = newSolution.getFinalState().getIteration();
    
            if (!bins.isEmpty()) {
                int binIndexFrom = random.nextInt(bins.size());
                Bin binFrom = bins.get(binIndexFrom);
    
                if (!binFrom.getItems().isEmpty()) {
                    int itemIndex = random.nextInt(binFrom.getItems().size());
                    Item itemToMove = binFrom.getItems().remove(itemIndex);
    
                    // Try to place the item in a different bin
                    int binIndexTo = random.nextInt(bins.size());
                    // Ensure we select a different bin
                    while (binIndexTo == binIndexFrom) {
                        binIndexTo = random.nextInt(bins.size());
                    }
    
                    Bin binTo = bins.get(binIndexTo);
                    if (binTo.getRemainingCapacity() >= itemToMove.getWeight()) {
                        binTo.getItems().add(itemToMove);
                        binTo.setRemainingCapacity(binTo.getRemainingCapacity() - itemToMove.getWeight());
                    } else {
                        // If it doesn't fit, put it back and consider this a failed attempt to find a neighbor
                        binFrom.getItems().add(itemToMove);
                    }
                }
            }
            IterationState newIterationState = createIterationState(bins, iteration);
            newSolution.updateIterationState(iteration, newIterationState);
            iteration++;
        }
    
        return newSolutions;
    }
    
    private IterationState createIterationState(List<Bin> bins, int iteration) {
        int usedBinCount = 0;
        for (Bin bin : bins) {
            if (bin.getCapacity() - bin.getRemainingCapacity() > 0) { // Bin is used
                usedBinCount++;
            }
        }
        return new IterationState(iteration, usedBinCount, bins);
    }    

    // Copy the Solution object deeply
    public Map<String, Solution> copySolution(Map<String, Solution> solutions) {
        Map<String, Solution> newSolutions = new LinkedHashMap<>();
        for (Map.Entry<String, Solution> entry : solutions.entrySet()) {
            String problemName = entry.getKey();
            Solution solution = entry.getValue();
    
            Solution newSolution = new Solution(problemName);
            for (Map.Entry<Integer, IterationState> stateEntry : solution.getIterationStates().entrySet()) {
                int iteration = stateEntry.getKey();
                IterationState state = stateEntry.getValue();
                IterationState newState = createIterationState(copyBins(state.getBins()), iteration);
                newSolution.addIterationState(iteration, newState);
            }
    
            newSolutions.put(problemName, newSolution);
        }
        return newSolutions;
    }
    
    
    private List<Bin> copyBins(List<Bin> bins) {
        List<Bin> newBins = new ArrayList<>();
        for (Bin bin : bins) {
            // Start with the full capacity for the new bin as the remaining capacity will be calculated based on added items.
            Bin newBin = new Bin(bin.getId(), bin.getCapacity(), bin.getCapacity());
            
            // Copy items to the new bin and adjust the remaining capacity accordingly.
            for (Item item : bin.getItems()) {
                newBin.getItems().add(new Item(item.getWeight()));
                // Reduce the remaining capacity only by the weight of the added items.
                newBin.setRemainingCapacity(newBin.getRemainingCapacity() - item.getWeight());
            }
            newBins.add(newBin);
        }
        return newBins;
    }
    
}