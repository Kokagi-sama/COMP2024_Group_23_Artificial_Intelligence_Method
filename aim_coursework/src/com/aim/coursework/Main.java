package com.aim.coursework;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import com.aimframeworkgrp23.*;

public class Main {
    static String dataset_path = "./src/resources/BPP.txt";
    static String output_directory = "./src/resources";

    public static void main(String[] args) {

        try {
            // Reading Problems
            LinkedHashSet<BinPackingProblem> problems = TextFileReader.readProblems(dataset_path);

            // Initialising initial solution ArrayList
            ArrayList<Solution> initial_solutions = new ArrayList<Solution>();

            // Creating and saving initial solution (using First Fit Algorithm from the framework)
            for (BinPackingProblem problem: problems) {
                Initialise i = new Initialise(problem);
                Solution initial_solution = i.getInitialSolution();
                initial_solutions.add(initial_solution);
                PrintSolutionToFile.saveInitialResult(initial_solution, output_directory, "FFD Initialisation");
            }

            // Creating and saving Simulated Annealing solution
            for (Solution solution: initial_solutions) {
                SimulatedAnnealing sa = new SimulatedAnnealing(solution);
                FinalSolution simulatedAnnealing_solution = sa.applySimulatedAnnealing();
                PrintSolutionToFile.saveResult(simulatedAnnealing_solution, output_directory, "Simmulated Annealing");
                ChartUtils.buildAndDisplayXYCharts(simulatedAnnealing_solution);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}