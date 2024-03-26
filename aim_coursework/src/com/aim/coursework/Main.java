package com.aim.coursework;

import java.util.Map;
import java.util.LinkedHashSet;
import com.aimframeworkgrp23.*;

public class Main {
    static String datasetPath = "./src/resources/BPP.txt";
    static String initialSolutionPath = "./src/resources/initialSolution.txt";
    static String simulatedAnnealingSolutionPath = "./src/resources/simulatedAnnealingSolution.txt";

    public static void main(String[] args) {

        try {
            // Create heuristic object
            Heuristics h1 = new Heuristics();

            // Reading Problems
            LinkedHashSet<BinPackingProblem> problems = TextFileReader.readProblems(datasetPath);

            // Creating and saving initial solution (using First Fit Algorithm from the framework)
            Map<String, Solution> initialSolution = InitialSolution.getInitialSolution(problems);
            PrintSolutionsToFile.saveResults(initialSolution, initialSolutionPath);

            // Creating and saving Simulated Annealing solution
//             SimulatedAnnealing sa = new SimulatedAnnealing(h1, initialSolution);
//             Map<String, Solution> simulatedAnnealing_solution = sa.applySimulatedAnnealing();
//             PrintSolutionsToFile.saveResults(simulatedAnnealing_solution, simulatedAnnealingSolutionPath);

            ChartUtils.buildAndDisplayChart("My Line Chart");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}