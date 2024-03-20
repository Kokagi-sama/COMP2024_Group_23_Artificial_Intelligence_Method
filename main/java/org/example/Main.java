package org.example;

import java.util.Map;
import java.util.LinkedHashSet;
import com.aimframeworkgrp23.*;

public class Main {
    static String datasetPath = "./src/main/resources/BPP.txt";
    static String initialSolutionPath = "./src/main/resources/initialSolution.txt";
    static String firstFitSolutionPath = "./src/main/resources/firstFitSolution.txt";
    static String simulatedAnnealingSolutionPath = "./src/main/resources/simulatedAnnealingSolution.txt";

    public static void main(String[] args) {

        try {
            // Create heuristic object
            Heuristics h1 = new Heuristics();

            // Reading Problems
            LinkedHashSet<BinPackingProblem> problems = TextFileReader.readProblems(datasetPath);

            // Creating and saving initial solution
            Map<String, Solution> initialSolution = InitialiseSolution.initSolution(problems);
            PrintSolutionsToFile.saveResults(initialSolution, initialSolutionPath);

            // Creating and saving First Fit Algorithm solution
            FirstFitAlgorithm ff = new FirstFitAlgorithm(initialSolution);
            Map<String, Solution> firstFitSolution = ff.applyFirstFit();
            PrintSolutionsToFile.saveResults(firstFitSolution, firstFitSolutionPath);

            // Creating and saving Simulated Annealing solution
             SimulatedAnnealing sa = new SimulatedAnnealing(h1, initialSolution);
             Map<String, Solution> simulatedAnnealing_solution = sa.applySimulatedAnnealing();
             PrintSolutionsToFile.saveResults(simulatedAnnealing_solution, simulatedAnnealingSolutionPath);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}