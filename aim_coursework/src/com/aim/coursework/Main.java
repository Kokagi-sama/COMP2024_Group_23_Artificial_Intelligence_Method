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

    // Main Entry Point for the program
    public static void main(String[] args) {

        try {

            System.out.println("Clearing Results Directory!");

            System.out.println();

            // Clearing the output directory
            FileUtils.clearOutputDirectory(result_output_directory);

            System.out.println("Reading Problems....");

            // Reading Problems
            LinkedHashSet<BinPackingProblem> problems = TextFileReader.readProblems(dataset_path);

            System.out.println("Problems Read Successfully!");

            System.out.println();

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

            System.out.println();



            // Simulated Annealing

            System.out.println("Generating Simulated Annealing Solutions for " + algorithm_iterations + " iterations....");

                // Creating and saving Simulated Annealing solution
                for (Solution solution: initial_solutions) {

                    System.out.println("\tGenerating Solutions for problem: " + solution.getProblemName() + "....");

                    // Creating Array List of generated final solutions for each problem across all iterations
                    ArrayList<Solution> best_final_solutions_per_problem_simulated_annealing_algorithm = new ArrayList<Solution>();

                    for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                        SimulatedAnnealing sa = new SimulatedAnnealing(solution);
                        FinalSolution simulated_annealing_solution = sa.applySimulatedAnnealing();
                        PrintSolutionToFile.saveResult(iteration, simulated_annealing_solution, result_output_directory, "Simulated_Annealing");
                        ChartUtilities.buildAndSaveXYCharts(iteration, "Simulated_Annealing", simulated_annealing_solution, result_output_directory, chart_width, chart_height);

                        // Store Best Final Solution for each problem for each all iterations of the algorithm
                        best_final_solutions_per_problem_simulated_annealing_algorithm.add(simulated_annealing_solution.getBestSolution());
                    }

                    System.out.println("\tSolutions generated for problem: " + solution.getProblemName());
                    System.out.println();

                    // Creating and Saving Boxplot for each problem across all iterations
                    ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_simulated_annealing_algorithm, "Simulated_Annealing", result_output_directory, chart_width, chart_height);
                
                }

            System.out.println("Simulated Annealing Solutions Generated Successfully!");
            
            System.out.println();



            // Genetic Algorithm

            System.out.println("Generating Genetic Algorithm Solutions for " + algorithm_iterations + "iterations ....");

            // Creating and saving Genetic Algorithm solution
            for (BinPackingProblem problem: problems) {

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_genetic_algorithm = new ArrayList<Solution>();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                    GeneticAlgorithm ga = new GeneticAlgorithm(problem);
                    FinalSolution genetic_algorithm_solution = ga.applyGeneticAlgorithm();
                    PrintSolutionToFile.saveResult(iteration, genetic_algorithm_solution, result_output_directory, "Genetic_Algorithm");
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Genetic_Algorithm", genetic_algorithm_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_genetic_algorithm.add(genetic_algorithm_solution.getBestSolution());
                }

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName());
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_genetic_algorithm, "Genetic_Algorithm", result_output_directory, chart_width, chart_height);

            }

            System.out.println("Genetic Algorithm Solutions Generated Successfully!");

            System.out.println();



            // Adaptive Fitness Dependent Optimizer

            System.out.println("Generating Adaptive Fitness Dependent Optimizer Solutions for " + algorithm_iterations + " iterations....");

            // Creating and saving Adaptive Fitness Dependent Optimizer solution
            for (BinPackingProblem problem: problems) {

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_adaptive_fitness_dependent_optimizer = new ArrayList<Solution>();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                    AdaptiveFitnessDependentOptimizer AFDO = new AdaptiveFitnessDependentOptimizer(problem);
                    FinalSolution adaptive_fitness_dependent_optimizer_solution = AFDO.applyAdaptiveFitnessDependentOptimizerAlgorithm();
                    PrintSolutionToFile.saveResult(iteration, adaptive_fitness_dependent_optimizer_solution, result_output_directory, "Adaptive_Fitness_Dependent_Optimizer");
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Adaptive_Fitness_Dependent_Optimizer", adaptive_fitness_dependent_optimizer_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_adaptive_fitness_dependent_optimizer.add(adaptive_fitness_dependent_optimizer_solution.getBestSolution());
                }

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName());
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_adaptive_fitness_dependent_optimizer, "Adaptive_Fitness_Dependent_Optimizer", 
                    result_output_directory, chart_width, chart_height);

            }

            System.out.println("Adaptive Fitness Dependent Optimizer Solutions Generated Successfully!");

            System.out.println();



            // Perturbation Minimum Bin Slack

            System.out.println("Generating Perturbation Minimum Bin Slack Solutions for " + algorithm_iterations + " iterations....");

            // Creating and saving Perturbation Minimum Bin Slack solution
            for (Solution solution: initial_solutions) {

                System.out.println("\tGenerating Solutions for problem: " + solution.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_perturbation_minimum_bin_slack_solution = new ArrayList<Solution>();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                    PerturbationMinimumBinSlack pmbs = new PerturbationMinimumBinSlack(solution);
                    FinalSolution perturbation_minimum_bin_slack_solution = pmbs.applyPerturbationMBS();
                    PrintSolutionToFile.saveResult(iteration, perturbation_minimum_bin_slack_solution, result_output_directory, "Perturbation_Minimum_Bin_Slack");
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Perturbation_Minimum_Bin_Slack", perturbation_minimum_bin_slack_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_perturbation_minimum_bin_slack_solution.add(perturbation_minimum_bin_slack_solution.getBestSolution());
                }

                System.out.println("\tSolutions generated for problem: " + solution.getProblemName());
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_perturbation_minimum_bin_slack_solution, "Perturbation_Minimum_Bin_Slack",
                    result_output_directory, chart_width, chart_height);

            }

            System.out.println("Perturbation Minimum Bin Slack Solutions Generated Successfully!");

            System.out.println();



            // Ant Colony Optimization

            System.out.println("Generating Ant Colony Optimization Solutions for " + algorithm_iterations + " iterations....");

            
        
            //  Creating and saving Hybrid Ant Colony Optimization solution
            for (BinPackingProblem problem: problems) {

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_ant_colony_optimization = new ArrayList<Solution>();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                    AntColonyOptimization ACO = new AntColonyOptimization(problem);
                    FinalSolution ant_colony_optimization_solution = ACO.applyHybridAntColonyOptimization();
                    PrintSolutionToFile.saveResult(iteration, ant_colony_optimization_solution, result_output_directory, "Ant_Colony_Optimization");
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Ant_Colony_Optimization", ant_colony_optimization_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_ant_colony_optimization.add(ant_colony_optimization_solution.getBestSolution());
                }

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName());
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_ant_colony_optimization, "Ant_Colony_Optimization",
                    result_output_directory, chart_width, chart_height);

            }

            System.out.println("Ant Colony Optimization Solutions Generated Successfully!");

            System.out.println();
            
            System.out.println("Program Ended Successfully! Results can be found in the '../resources/results/' directory!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}