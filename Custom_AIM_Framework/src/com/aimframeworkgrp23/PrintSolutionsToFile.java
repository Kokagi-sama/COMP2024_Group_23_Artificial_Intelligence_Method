package com.aimframeworkgrp23;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class PrintSolutionsToFile {
    public static void saveResults(Map<String, Solution> solutions, String saveFilePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(saveFilePath))) {
            for (Map.Entry<String, Solution> entry : solutions.entrySet()) {
                String problemName = entry.getKey();
                Solution solution = entry.getValue();
                IterationState finalState = solution.getFinalState();

                writer.write("==== Final State for Problem: " + problemName + " ====\n");
                writer.write("Iteration: " + finalState.getIteration() + "\n");
                writer.write("Bins used: " + finalState.getBinCount() + "\n");

                for (Bin bin : finalState.getBins()) {
                    writer.write("Bin " + bin.getId() + " (Remaining capacity: " + bin.getRemainingCapacity() + "):\n");
                    for (Item item : bin.getItems()) {
                        writer.write("  Item with weight " + item.getWeight() + "\n");
                    }
                }
                writer.write("==== End of Final State ====\n\n");

                // Then, write the full history of states
                writer.write("==== Detailed Iteration History for Problem: " + problemName + " ====\n");
                Map<Integer, IterationState> iterationStates = solution.getIterationStates();
                for (Map.Entry<Integer, IterationState> entryIteration : iterationStates.entrySet()) {
                    IterationState iterationState = entryIteration.getValue();
                    writer.write("Iteration: " + entryIteration.getKey() + "\n");
                    writer.write("Bins used: " + iterationState.getBinCount() + "\n");

                    for (Bin bin : iterationState.getBins()) {
                        writer.write(
                                "Bin " + bin.getId() + " (Remaining capacity: " + bin.getRemainingCapacity() + "):\n");

                        for (Item item : bin.getItems()) {
                            writer.write("  Item with weight " + item.getWeight() + "\n");
                        }
                    }
                    writer.write("\n"); // Add a newline for better readability
                }
                writer.write("==== End Detailed Iteration History ====\n\n");

            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}