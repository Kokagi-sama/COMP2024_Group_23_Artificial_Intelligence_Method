package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.Random;

public class Heuristics {

    // Objective function
    public static double objectiveFunction(Solution solution, int z) {
        double fitness = 0;
    
        for (Bin bin : solution.getBins()) {
            double load = (double) (bin.getCapacity() - bin.getRemainingCapacity());
            double load_square = Math.pow(load, 2);
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
            neighbourhood.add(currentSolution);
        }
    
        return neighbourhood;
    }

    public static ArrayList<Solution> swapAndEvaluate(ArrayList<Solution> neighbourhood) {
        Random rand = new Random();

        for (Solution neighbour : neighbourhood) {
            int binCount = neighbour.getBinCount();
            ArrayList<Bin> bins = neighbour.getBins();

            if (binCount > 1) {
                int i = rand.nextInt(binCount);
                int j = i;
                while (j == i) {
                    j = rand.nextInt(binCount);
                }

                Bin binI = bins.get(i);
                Bin binJ = bins.get(j);

                // Swap (1,0) - Moving one item from bin I to bin J
                if (!binI.getItems().isEmpty()) {
                    int itemIndex = rand.nextInt(binI.getItems().size());
                    Item itemI = binI.getItems().get(itemIndex);

                    // Assuming calculateDeltaF and objectiveFunction methods are defined elsewhere
                    double deltaF = calculateDeltaF(binI, binJ, itemI, null);
                    if (deltaF >= 0 && binJ.getRemainingCapacity() >= itemI.getWeight()) {
                        binI.getItems().remove(itemI);
                        binJ.getItems().add(itemI);
                        binI.setRemainingCapacity(binI.getRemainingCapacity() + itemI.getWeight());
                        binJ.setRemainingCapacity(binJ.getRemainingCapacity() - itemI.getWeight());
                    }
                }

                // Swap (1,1) - Swapping items between bin I and bin J
                if (!binI.getItems().isEmpty() && !binJ.getItems().isEmpty()) {
                    int itemIndexI = rand.nextInt(binI.getItems().size());
                    int itemIndexJ = rand.nextInt(binJ.getItems().size());
                    Item itemI = binI.getItems().get(itemIndexI);
                    Item itemJ = binJ.getItems().get(itemIndexJ);

                    double deltaF = calculateDeltaF(binI, binJ, itemI, itemJ);
                    // System.out.println(deltaF);
                    if (deltaF >= 0 && 
                        binI.getRemainingCapacity() + itemJ.getWeight() - itemI.getWeight() >= 0 &&
                        binJ.getRemainingCapacity() + itemI.getWeight() - itemJ.getWeight() >= 0) {
                        binI.getItems().set(itemIndexI, itemJ);
                        binJ.getItems().set(itemIndexJ, itemI);
                        binI.setRemainingCapacity(binI.getRemainingCapacity() + itemJ.getWeight() - itemI.getWeight());
                        binJ.setRemainingCapacity(binJ.getRemainingCapacity() + itemI.getWeight() - itemJ.getWeight());
                    }
                }
            }
        }
        return neighbourhood;
    }

    // Change in fitness
    private static double calculateDeltaF(Bin binI, Bin binJ, Item itemI, Item itemJ) {
        int lI = binI.getCapacity() - binI.getRemainingCapacity();
        int lJ = binJ.getCapacity() - binJ.getRemainingCapacity();
        int tI = itemI != null ? itemI.getWeight() : 0;
        int tJ = itemJ != null ? itemJ.getWeight() : 0;

        return Math.pow((lI - tI + tJ), 2) + Math.pow((lJ + tI - tJ), 2) - Math.pow(lI, 2) - Math.pow(lJ, 2);
    }

    // public ArrayList<Solution> generateNeighbor(ArrayList<Solution> currentSolutions) {
    //     Random random = new Random();

    //     for(Solution currentSolution: currentSolutions) {
    //         // Deep copy all current solutions
    //         Solution newSolution = copySolution(currentSolution);
    //         List<Bin> bins = newSolution.getBins();

    //         if (!bins.isEmpty()) {
    //             int binIndexFrom = random.nextInt(bins.size());
    //             Bin binFrom = bins.get(binIndexFrom);

    //             if (!binFrom.getItems().isEmpty()) {
    //                 int itemIndex = random.nextInt(binFrom.getItems().size());
    //                 Item itemToMove = binFrom.getItems().remove(itemIndex);

    //                 // Try to place the item in a different bin
    //                 int binIndexTo = random.nextInt(bins.size());
    //                 // Ensure we select a different bin
    //                 while (binIndexTo == binIndexFrom) {
    //                     binIndexTo = random.nextInt(bins.size());
    //                 }

    //                 Bin binTo = bins.get(binIndexTo);
    //                 if (binTo.getRemainingCapacity() >= itemToMove.getWeight()) {
    //                     binTo.getItems().add(itemToMove);
    //                     binTo.setRemainingCapacity(binTo.getRemainingCapacity() - itemToMove.getWeight());
    //                 } else {
    //                     // If it doesn't fit, put it back and consider this a failed attempt to find a neighbor
    //                     binFrom.getItems().add(itemToMove);
    //                 }
    //             }
    //         }
    //     }

        
   
    //     return newSolutions;
    // }
    
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

        copySolution.setBinCount(copySolution.getBins().size());

        return copySolution;
    }
}