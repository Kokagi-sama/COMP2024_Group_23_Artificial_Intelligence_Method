package org.example;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirstFitAlgorithm {

    private final Map<String, Solution> initialSolutions;
    private final Map<String, Solution> solutions;

    public FirstFitAlgorithm(Map<String, Solution> initialSolutions) {
        this.initialSolutions = initialSolutions;
        this.solutions = new LinkedHashMap<>();
    }

    public Map<String, Solution> applyFirstFit() {
        for (String problemName : initialSolutions.keySet()) {
            Solution initialSolution = initialSolutions.get(problemName);
            Solution solution = new Solution(problemName);
            IterationState lastState = initialSolution.getFinalState();

            List<Item> allItems = new ArrayList<>();
            for (Bin bin : lastState.getBins()) {
                allItems.addAll(bin.getItems());
            }

            List<Bin> bins = new ArrayList<>();
            int binId = 1;
            int iteration = 0;

            for (Item item : allItems) {
                boolean placed = false;

                for (Bin bin : bins) {
                    if (bin.getRemainingCapacity() >= item.getWeight()) {
                        bin.getItems().add(item);
                        bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                        placed = true;
                        break;
                    }
                }

                if (!placed) {
                    Bin newBin = new Bin(binId++, lastState.getBins().getFirst().getCapacity(), lastState.getBins().getFirst().getCapacity() - item.getWeight());
                    newBin.getItems().add(item);
                    bins.add(newBin);
                }

                iteration++;
                solution.addIterationState(iteration, new IterationState(iteration, bins.size(), new ArrayList<>(bins)));

            }

            solutions.put(problemName, solution);
        }

        return solutions;
    }
}