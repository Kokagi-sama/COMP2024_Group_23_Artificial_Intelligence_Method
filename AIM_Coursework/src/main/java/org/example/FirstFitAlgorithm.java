package org.example;

import java.util.ArrayList;
import java.util.List;
import com.aimframeworkgrp23.*;
public class FirstFitAlgorithm {

    public static List<Bin> firstFit(List<BinPackingProblem> problems) {
        List<Bin> bins = new ArrayList<>();

        for (BinPackingProblem problem : problems) {
            bins.clear(); // Start with a fresh set of bins for each problem

            for (Item item : problem.getItems()) {
                boolean placed = false;

                // Try to fit the item in an existing bin
                for (Bin bin : bins) {
                    if (bin.remainingCapacity >= item.getWeight()) {
                        bin.items.add(item);
                        bin.remainingCapacity -= item.getWeight();
                        placed = true;
                        break; // Item placed, exit the loop
                    }
                }

                // If the item was not placed in any bin, create a new bin
                if (!placed) {
                    Bin newBin = new Bin(problem.getBinCapacity());
                    newBin.items.add(item);
                    newBin.remainingCapacity -= item.getWeight();
                    bins.add(newBin);
                }
            }


        }

        return bins; // This list will be overwritten for each problem in the loop
    }

    public static void printResults(List<BinPackingProblem> problems, List<Bin> bins){
        // Output for debugging
        for (BinPackingProblem problem: problems) {
            System.out.println("Problem: " + problem.getProblemName() + ", Bins used: " + bins.size());

            int binNumber = 1;
            for (Bin bin : bins) {
                System.out.println("Bin " + binNumber + " (Remaining capacity: " + bin.remainingCapacity + "):");
                for (Item item : bin.items) {
                    System.out.println("  Item with weight " + item.getWeight());
                }
                binNumber++;
            }
        }
    }


}
