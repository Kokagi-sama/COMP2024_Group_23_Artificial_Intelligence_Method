package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HeuristicsOld {

    private final Random random;

    public HeuristicsOld() {
        this.random = new Random();
    }

    public Random getRandom() {
        return this.random;
    }

    public double objectiveFunction(List<Bin> solution) {
        double fitness = 0;
        for (Bin bin : solution) {
            fitness += Math.pow((bin.getCapacity() - bin.getRemainingCapacity()), 2); // Example: consider remaining capacity to calculate fitness
        }
        return fitness; // Lower fitness is better; adjust as needed
    }

    public List<Bin> generateNeighbor(List<Bin> currentSolution) {
        List<Bin> newSolution = copySolution(currentSolution);

        int binIndexFrom = random.nextInt(newSolution.size());
        Bin binFrom = newSolution.get(binIndexFrom);

        if (!binFrom.getItems().isEmpty()) {
            int itemIndex = random.nextInt(binFrom.getItems().size());
            Item itemToMove = binFrom.getItems().remove(itemIndex);

            // Try to place the item in a different bin
            int binIndexTo = random.nextInt(newSolution.size());
            // Ensure we select a different bin
            while (binIndexTo == binIndexFrom) {
                binIndexTo = random.nextInt(newSolution.size());
            }

            Bin binTo = newSolution.get(binIndexTo);
            if (binTo.getRemainingCapacity() >= itemToMove.getWeight()) {
                binTo.getItems().add(itemToMove);
                binTo.setRemainingCapacity(binTo.getRemainingCapacity() - itemToMove.getWeight());
            } else {
                // If it doesn't fit, put it back and consider this a failed attempt to find a neighbor
                binFrom.getItems().add(itemToMove);
            }
        }

        return newSolution;
    }

    public List<Bin> copySolution(List<Bin> solution) {
        List<Bin> newSolution = new ArrayList<>();
        for (Bin bin : solution) {
            Bin newBin = new Bin(bin.getId(), bin.getCapacity(), bin.getRemainingCapacity() + totalItemsWeight(bin.getItems()));
            for (Item item : bin.getItems()) {
                newBin.getItems().add(new Item(item.getWeight()));
                newBin.setRemainingCapacity(newBin.getRemainingCapacity() - item.getWeight());  // Adjust capacity as items are added
            }
            newSolution.add(newBin);
        }
        return newSolution;
    }

    public int totalItemsWeight(List<Item> items) {
        int totalWeight = 0;
        for (Item item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }
}