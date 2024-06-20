package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.Random;

public class WeightedRandomSelection {
    private Random random;

    public WeightedRandomSelection() {
        this.random = new Random();
    }

    public Item chooseItemWithWeightedProbability(ArrayList<Item> items, ArrayList<Double> weights) {
        if (items.size() != weights.size()) {
            throw new IllegalArgumentException("Items and weights must be of the same size");
        }

        // Calculate the total weight
        double totalWeight = weights.stream().mapToDouble(Double::doubleValue).sum();
        // Generate a random number between 0 and totalWeight
        double randomValue = random.nextDouble() * totalWeight;
        double cumulativeWeight = 0.0;

        // Iterate over the weights to find where the random value falls
        for (int i = 0; i < items.size(); i++) {
            cumulativeWeight += weights.get(i);
            if (cumulativeWeight >= randomValue) {
                return items.get(i);
            }
        }

        // Fallback, should not happen
        return items.get(items.size() - 1);
    }
}
