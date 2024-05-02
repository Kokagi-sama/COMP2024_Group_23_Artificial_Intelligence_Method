package com.aim.coursework;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.aimframeworkgrp23.*;

public class Main {
    static String dataset_path = "./src/resources/BPP.txt";
    static String output_directory = "./src/resources";
    static final int chartWidth = 560;
    static final int chartHeight = 367;


    public static void main(String[] args) {

        try {

            // Clearing the output directory
            FileUtils.clearOutputDirectory(output_directory);

            // Reading Problems
            LinkedHashSet<BinPackingProblem> problems = TextFileReader.readProblems(dataset_path);

            // Initialising initial solution ArrayList
            ArrayList<Solution> initial_solutions = new ArrayList<Solution>();

            // Creating and saving initial solution (using First Fit Algorithm from the framework)
            for (BinPackingProblem problem: problems) {
                Initialise initialiser = new Initialise(problem);
                Solution initial_solution = initialiser.getInitialSolution();
                initial_solutions.add(initial_solution);
                PrintSolutionToFile.saveInitialResult(initial_solution, output_directory, "FFD_Initialisation");
            }

            // // Creating and saving Simulated Annealing solution
            // for (Solution solution: initial_solutions) {
            //     SimulatedAnnealing sa = new SimulatedAnnealing(solution);
            //     FinalSolution simulatedAnnealing_solution = sa.applySimulatedAnnealing();
            //     PrintSolutionToFile.saveResult(simulatedAnnealing_solution, output_directory, "Simmulated_Annealing_Algorithm");
            //     ChartUtilities.buildAndDisplayXYCharts("Simmulated_Annealing_Algorithm", simulatedAnnealing_solution, chartWidth, chartHeight);
            // }

            // // Creating and saving Genetic Algorithm solution
            // for (BinPackingProblem problem: problems) {
            //     GeneticAlgorithm ga = new GeneticAlgorithm(problem);
            //     FinalSolution genetic_algorithm_solution = ga.applyGeneticAlgorithm();
            //     PrintSolutionToFile.saveResult(genetic_algorithm_solution, output_directory, "Genetic_Algorithm");
            //     ChartUtilities.buildAndDisplayXYCharts("Genetic_Algorithm", genetic_algorithm_solution, chartWidth, chartHeight);
            // }

            // // Creating and saving Adaptive Fitness Dependent Optimizer solution
            // for (BinPackingProblem problem: problems) {
            //     AdaptiveFitnessDependentOptimizer AFDO = new AdaptiveFitnessDependentOptimizer(problem);
            //     FinalSolution ADFO_algorithm_solution = AFDO.applyAdaptiveFitnessDependentOptimizerAlgorithm();
            //     PrintSolutionToFile.saveResult(ADFO_algorithm_solution, output_directory, "AFDO_Algorithm");
            //     ChartUtilities.buildAndDisplayXYCharts("AFDO_Algorithm", ADFO_algorithm_solution, chartWidth, chartHeight);
            // }

            // Creating and saving Perturbation MBS solution
            for (Solution solution: initial_solutions) {
                PerturbationMBS pmbs = new PerturbationMBS(solution);
                FinalSolution perturbationmbs_solution = pmbs.applyPerturbationMBS();
                PrintSolutionToFile.saveResult(perturbationmbs_solution, output_directory, "PerturbationMBS_Algorithm");
                ChartUtilities.buildAndDisplayXYCharts("PerturbationMBS_Algorithm", perturbationmbs_solution, chartWidth, chartHeight);
            }
            
            // //  Creating and saving Hybrid Ant Colony Optimization solution
            // for (BinPackingProblem problem: problems) {
            //     HybridAntColonyOptimization HACO = new HybridAntColonyOptimization(problem);
            //     FinalSolution HACO_algorithm_solution = HACO.applyHybridAntColonyOptimization();
            //     PrintSolutionToFile.saveResult(HACO_algorithm_solution, output_directory, "HACO_Algorithm");
            //     ChartUtilities.buildAndDisplayXYCharts("HACO_Algorithm", HACO_algorithm_solution, chartWidth, chartHeight);
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}