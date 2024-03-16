package com.aimframeworkgrp23;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

public class TextFileReader {

    public static List<BinPackingProblem> readProblems(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<BinPackingProblem> problems = new ArrayList<>();

        BinPackingProblem currentProblem = null;
        String problemName = null;
        int uniqueWeightCount = 0;
        int binCapacity = 0;
        boolean isNameNext = false;

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("'")) {
                if (currentProblem != null) {
                    problems.add(currentProblem);
                }
                problemName = line.replaceAll("'", "");
                isNameNext = true;
                currentProblem = null; // Prepare for the next problem
            } else if (isNameNext && line.matches("\\d+")) {
                uniqueWeightCount = Integer.parseInt(line);
                isNameNext = false; // Next line should be bin capacity
            } else if (!isNameNext && line.matches("\\d+") && currentProblem == null) {
                binCapacity = Integer.parseInt(line);
                currentProblem = new BinPackingProblem(problemName, uniqueWeightCount, binCapacity);
            } else if (currentProblem != null && line.matches("\\d+\\s+\\d+")) {
                String[] parts = line.split("\\s+");
                int weight = Integer.parseInt(parts[0]);
                int count = Integer.parseInt(parts[1]);
                for (int i = 0; i < count; i++) {
                    currentProblem.addItem(weight);
                }
            }
        }

        if (currentProblem != null) { // Add the last problem if it exists
            problems.add(currentProblem);
        }

        return problems;
    }
}