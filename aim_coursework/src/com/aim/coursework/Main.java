package com.aim.coursework;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.aimframeworkgrp23.*;

public class Main {
    static String dataset_path = "./src/resources/BPP.txt";
    static String result_output_directory = "./src/resources/results/";
    static final int chart_width = 560;
    static final int chart_height = 367;
    static final int algorithm_iterations = 30;


    public static void main(String[] args) {

        try {

            System.out.println("Clearing Results Directory!");

            // Clearing the output directory
            FileUtils.clearOutputDirectory(result_output_directory);

            System.out.println("Reading Problems....");

            // Reading Problems
            LinkedHashSet<BinPackingProblem> problems = TextFileReader.readProblems(dataset_path);

            System.out.println("Problems Read Successfully!");

            // Initialising initial solution ArrayList
            ArrayList<Solution> initial_solutions = new ArrayList<Solution>();

            System.out.println("Generating Initial FFD Solution....");

            // Creating and saving initial solution (using First Fit Algorithm from the framework)
            for (BinPackingProblem problem: problems) {
                Initialise initialiser = new Initialise(problem);
                Solution initial_solution = initialiser.getInitialSolution();
                initial_solutions.add(initial_solution);
                PrintSolutionToFile.saveInitialResult(initial_solution, result_output_directory, "FFD_Initialisation");
            }

            System.out.println("Initial FFD Solution Generated Successfully!");



            // Simulated Annealing

            System.out.println("Generating Simulated Annealing Solutions....");

            ArrayList<ArrayList<Solution>> best_final_solutions_per_algorithm_iteration = new ArrayList<ArrayList<Solution>>();

            for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                // Creating Array List of generated final solutions
                ArrayList<Solution> best_final_solutions_simulated_annealing_algorithm = new ArrayList<Solution>();

                // Creating and saving Simulated Annealing solution
                for (Solution solution: initial_solutions) {
                    SimulatedAnnealing sa = new SimulatedAnnealing(solution);
                    FinalSolution simulated_annealing_solution = sa.applySimulatedAnnealing();
                    PrintSolutionToFile.saveResult(iteration, simulated_annealing_solution, result_output_directory, "Simulated_Annealing");
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Simulated_Annealing", simulated_annealing_solution, result_output_directory, chart_width, chart_height);

                    // Store Best Final Solution for every problem for each iteration of the algorithm
                    best_final_solutions_simulated_annealing_algorithm.add(simulated_annealing_solution.getBestSolution());
                }

                best_final_solutions_per_algorithm_iteration.add(best_final_solutions_simulated_annealing_algorithm);
            }

            ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_algorithm_iteration, "Simulated_Annealing", result_output_directory, chart_width, chart_height);
            

            System.out.println("Simulated Annealing Solutions Generated Successfully!");

            // // Genetic Algorithm

            // System.out.println("Generating Genetic Algorithm Solutions....");

            // // Creating and saving Genetic Algorithm solution
            // for (BinPackingProblem problem: problems) {
            //     GeneticAlgorithm ga = new GeneticAlgorithm(problem);
            //     FinalSolution genetic_algorithm_solution = ga.applyGeneticAlgorithm();
            //     PrintSolutionToFile.saveResult(genetic_algorithm_solution, result_output_directory, "Genetic_Algorithm");
            //     ChartUtilities.buildAndSaveXYCharts("Genetic_Algorithm", genetic_algorithm_solution, result_output_directory, chart_width, chart_height);
            // }

            // System.out.println("Genetic Algorithm Solutions Generated Successfully!");



            // // Genetic Adaptive Fitness Dependent Optimizer

            // System.out.println("Generating Adaptive Fitness Dependent Optimizer Solutions....");

            // // Creating and saving Adaptive Fitness Dependent Optimizer solution
            // for (BinPackingProblem problem: problems) {
            //     AdaptiveFitnessDependentOptimizer AFDO = new AdaptiveFitnessDependentOptimizer(problem);
            //     FinalSolution ADFO_solution = AFDO.applyAdaptiveFitnessDependentOptimizerAlgorithm();
            //     PrintSolutionToFile.saveResult(ADFO_solution, result_output_directory, "Adaptive_Fitness_Dependent_Optimizer");
            //     ChartUtilities.buildAndSaveXYCharts("Adaptive_Fitness_Dependent_Optimizer", ADFO_solution, result_output_directory, chart_width, chart_height);
            // }

            // System.out.println("Adaptive Fitness Dependent Optimizer Solutions Generated Successfully!");



            // // Perturbation Minimum Bin Slack

            // System.out.println("Generating Perturbation Minimum Bin Slack Solutions....");

            // // Creating and saving Perturbation Minimum Bin Slack solution
            // for (Solution solution: initial_solutions) {
            //     PerturbationMinimumBinSlack pmbs = new PerturbationMinimumBinSlack(solution);
            //     FinalSolution perturbation_minimum_bin_slack_solution = pmbs.applyPerturbationMBS();
            //     PrintSolutionToFile.saveResult(perturbation_minimum_bin_slack_solution, result_output_directory, "Perturbation_Minimum_Bin_Slack");
            //     ChartUtilities.buildAndSaveXYCharts("Perturbation_Minimum_Bin_Slack", perturbation_minimum_bin_slack_solution, result_output_directory, chart_width, chart_height);
            // }

            // System.out.println("Perturbation Minimum Bin Slack Solutions Generated Successfully!");



            // Hybrid Any Colony Optimization

            
            // //  Creating and saving Hybrid Ant Colony Optimization solution
            // for (BinPackingProblem problem: problems) {
            //     HybridAntColonyOptimization HACO = new HybridAntColonyOptimization(problem);
            //     FinalSolution HACO_algorithm_solution = HACO.applyHybridAntColonyOptimization();
            //     PrintSolutionToFile.saveResult(HACO_algorithm_solution, result_output_directory, "HACO_Algorithm");
            //     ChartUtilities.buildAndSaveXYCharts("HACO_Algorithm", HACO_algorithm_solution, result_output_directory, chart_width, chart_height);
            // }

            
            System.out.println("Program Ended Successfully! Results can be found in the '../resources/results/' directory!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}