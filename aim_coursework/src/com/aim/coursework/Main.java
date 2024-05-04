package com.aim.coursework;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
// import java.util.List;
// import java.util.Map;

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

            // Linked Hash Map to store time taken by each algorith to complete {algorithm_iterations} for each problem
            LinkedHashMap<String, ArrayList<LinkedHashMap<String, Long>>> final_times_map = new LinkedHashMap<>();

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

            System.out.println("Reading All Problems and Generating Initial FFD Solutions for " + algorithm_iterations + " iterations....");

            long start_time_initialisation = System.currentTimeMillis();

            // Creating and saving initial solution per problem (using First Fit Algorithm from the framework)
            for (BinPackingProblem problem: problems) {

                // Retrieve the time list associated with the problem, or create a new list if none exists
                ArrayList<LinkedHashMap<String, Long>> time_list = final_times_map.getOrDefault(problem.getProblemName(), new ArrayList<>());

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                long start_time_initialisation_per_problem = System.currentTimeMillis();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                    Initialise initialiser = new Initialise(problem);

                    long start_time_initialisation_per_iteration = System.nanoTime();

                    Solution initial_solution = initialiser.getInitialSolution();

                    long elapsed_time_initialisation_per_iteration = System.nanoTime() - start_time_initialisation_per_iteration;

                    // Save the result of the first iteration only
                    if (iteration == 1) {
                        initial_solutions.add(initial_solution);
                    }

                    PrintSolutionToFile.saveInitialResult(iteration, initial_solution, result_output_directory, "FFD_Initialisation", elapsed_time_initialisation_per_iteration);

                }

                long elapsed_time_initialisation_per_problem = System.currentTimeMillis() - start_time_initialisation_per_problem;

                // Create a new LinkedHashMap for this specific recording
                LinkedHashMap<String, Long> algorithm_time = new LinkedHashMap<>();
                algorithm_time.put("FFD", elapsed_time_initialisation_per_problem);

                // Add the new map to the list
                time_list.add(algorithm_time);

                // Put the updated list back into the main map
                final_times_map.put(problem.getProblemName(), time_list);

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName() + " in " + elapsed_time_initialisation_per_problem + " milliseconds!");
                System.out.println();
            }

            long elapsed_time_initialisation = System.currentTimeMillis() - start_time_initialisation;

            System.out.println("Read All Problems and Generated Initial FFD Solutions Successfully in " + elapsed_time_initialisation + " milliseconds!");
            System.out.println();



            // Simulated Annealing

            System.out.println("Generating Simulated Annealing Solutions for " + algorithm_iterations + " iterations....");

            long start_time_sa = System.currentTimeMillis();

            // Creating and saving Simulated Annealing solution
            for (Solution solution: initial_solutions) {

                // Retrieve the time list associated with the problem, or create a new list if none exists
                ArrayList<LinkedHashMap<String, Long>> time_list = final_times_map.getOrDefault(solution.getProblemName(), new ArrayList<>());

                System.out.println("\tGenerating Solutions for problem: " + solution.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_simulated_annealing_algorithm = new ArrayList<Solution>();

                long start_time_sa_per_problem = System.currentTimeMillis();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {
                    long start_time_sa_per_iteration = System.currentTimeMillis();

                    SimulatedAnnealing sa = new SimulatedAnnealing(solution);
                    FinalSolution simulated_annealing_solution = sa.applySimulatedAnnealing();

                    long elapsed_time_sa_per_iteration = System.currentTimeMillis() - start_time_sa_per_iteration;

                    PrintSolutionToFile.saveResult(iteration, simulated_annealing_solution, result_output_directory, "Simulated_Annealing", false, elapsed_time_sa_per_iteration);
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Simulated_Annealing", simulated_annealing_solution, result_output_directory, chart_width, chart_height);

                    // Store Best Final Solution for each problem for each all iterations of the algorithm
                    best_final_solutions_per_problem_simulated_annealing_algorithm.add(simulated_annealing_solution.getBestSolution());
                }

                long elapsed_time_sa_per_problem = System.currentTimeMillis() - start_time_sa_per_problem;

                // Create a new LinkedHashMap for this specific recording
                LinkedHashMap<String, Long> algorithm_time = new LinkedHashMap<>();
                algorithm_time.put("SA", elapsed_time_sa_per_problem);

                // Add the new map to the list
                time_list.add(algorithm_time);

                // Put the updated list back into the main map
                final_times_map.put(solution.getProblemName(), time_list);

                System.out.println("\tSolutions generated for problem: " + solution.getProblemName() + " in " + elapsed_time_sa_per_problem + " milliseconds!");
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_simulated_annealing_algorithm, "Simulated_Annealing", result_output_directory, chart_width, chart_height);
            
            }

            long elapsed_time_sa = System.currentTimeMillis() - start_time_sa;

            System.out.println("Simulated Annealing Solutions Generated Successfully for All Problems in " + elapsed_time_sa + " milliseconds!");
            System.out.println();



            // Genetic Algorithm

            System.out.println("Generating Genetic Algorithm Solutions for " + algorithm_iterations + " iterations....");

            long start_time_ga = System.currentTimeMillis();

            // Creating and saving Genetic Algorithm solution
            for (BinPackingProblem problem: problems) {

                // Retrieve the time list associated with the problem, or create a new list if none exists
                ArrayList<LinkedHashMap<String, Long>> time_list = final_times_map.getOrDefault(problem.getProblemName(), new ArrayList<>());

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_genetic_algorithm = new ArrayList<Solution>();

                long start_time_ga_per_problem = System.currentTimeMillis();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {
                    long start_time_ga_per_iteration = System.currentTimeMillis();

                    GeneticAlgorithm ga = new GeneticAlgorithm(problem);
                    FinalSolution genetic_algorithm_solution = ga.applyGeneticAlgorithm();

                    long elapsed_time_ga_per_iteration = System.currentTimeMillis() - start_time_ga_per_iteration;

                    PrintSolutionToFile.saveResult(iteration, genetic_algorithm_solution, result_output_directory, "Genetic_Algorithm", false, elapsed_time_ga_per_iteration);
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Genetic_Algorithm", genetic_algorithm_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_genetic_algorithm.add(genetic_algorithm_solution.getBestSolution());
                }

                long elapsed_time_ga_per_problem = System.currentTimeMillis() - start_time_ga_per_problem;

                // Create a new LinkedHashMap for this specific recording
                LinkedHashMap<String, Long> algorithm_time = new LinkedHashMap<>();
                algorithm_time.put("GA", elapsed_time_ga_per_problem);

                // Add the new map to the list
                time_list.add(algorithm_time);

                // Put the updated list back into the main map
                final_times_map.put(problem.getProblemName(), time_list);

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName() + " in " + elapsed_time_ga_per_problem + " milliseconds!");
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_genetic_algorithm, "Genetic_Algorithm", result_output_directory, chart_width, chart_height);

            }

            long elapsed_time_ga = System.currentTimeMillis() - start_time_ga;

            System.out.println("Genetic Algorithm Solutions Generated Successfully for All Problems in " + elapsed_time_ga + " milliseconds!");
            System.out.println();



            // Adaptive Fitness Dependent Optimizer

            System.out.println("Generating Adaptive Fitness Dependent Optimizer Solutions for " + algorithm_iterations + " iterations....");

            long start_time_afdo = System.currentTimeMillis();

            // Creating and saving Adaptive Fitness Dependent Optimizer solution
            for (BinPackingProblem problem: problems) {

                // Retrieve the time list associated with the problem, or create a new list if none exists
                ArrayList<LinkedHashMap<String, Long>> time_list = final_times_map.getOrDefault(problem.getProblemName(), new ArrayList<>());

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_adaptive_fitness_dependent_optimizer = new ArrayList<Solution>();

                long start_time_afdo_per_problem = System.currentTimeMillis();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {

                    long start_time_afdo_per_iteration = System.nanoTime();

                    AdaptiveFitnessDependentOptimizer AFDO = new AdaptiveFitnessDependentOptimizer(problem);
                    FinalSolution adaptive_fitness_dependent_optimizer_solution = AFDO.applyAdaptiveFitnessDependentOptimizerAlgorithm();

                    long elapsed_time_afdo_per_iteration = System.nanoTime() - start_time_afdo_per_iteration;

                    PrintSolutionToFile.saveResult(iteration, adaptive_fitness_dependent_optimizer_solution, result_output_directory, "Adaptive_Fitness_Dependent_Optimizer",
                        true, elapsed_time_afdo_per_iteration);
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Adaptive_Fitness_Dependent_Optimizer", adaptive_fitness_dependent_optimizer_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_adaptive_fitness_dependent_optimizer.add(adaptive_fitness_dependent_optimizer_solution.getBestSolution());
                }

                long elapsed_time_afdo_per_problem = System.currentTimeMillis() - start_time_afdo_per_problem;

                // Create a new LinkedHashMap for this specific recording
                LinkedHashMap<String, Long> algorithm_time = new LinkedHashMap<>();
                algorithm_time.put("AFDO", elapsed_time_afdo_per_problem);

                // Add the new map to the list
                time_list.add(algorithm_time);

                // Put the updated list back into the main map
                final_times_map.put(problem.getProblemName(), time_list);

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName() + " in " + elapsed_time_afdo_per_problem + " milliseconds!");
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_adaptive_fitness_dependent_optimizer, "Adaptive_Fitness_Dependent_Optimizer", 
                    result_output_directory, chart_width, chart_height);

            }

            long elapsed_time_afdo = System.currentTimeMillis() - start_time_afdo;

            System.out.println("Adaptive Fitness Dependent Optimizer Solutions Generated Successfully for All Problems in " + elapsed_time_afdo + " milliseconds!");
            System.out.println();



            // Perturbation Minimum Bin Slack

            System.out.println("Generating Perturbation Minimum Bin Slack Solutions for " + algorithm_iterations + " iterations....");

            long start_time_pmbs = System.currentTimeMillis();

            // Creating and saving Perturbation Minimum Bin Slack solution
            for (Solution solution: initial_solutions) {

                // Retrieve the time list associated with the problem, or create a new list if none exists
                ArrayList<LinkedHashMap<String, Long>> time_list = final_times_map.getOrDefault(solution.getProblemName(), new ArrayList<>());

                System.out.println("\tGenerating Solutions for problem: " + solution.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_perturbation_minimum_bin_slack_solution = new ArrayList<Solution>();

                long start_time_pmbs_per_problem = System.currentTimeMillis();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {
                    long start_time_pmbs_per_iteration = System.currentTimeMillis();

                    PerturbationMinimumBinSlack pmbs = new PerturbationMinimumBinSlack(solution);
                    FinalSolution perturbation_minimum_bin_slack_solution = pmbs.applyPerturbationMBS();

                    long elapsed_time_pmbs_per_iteration = System.nanoTime() - start_time_pmbs_per_iteration;

                    PrintSolutionToFile.saveResult(iteration, perturbation_minimum_bin_slack_solution, result_output_directory, "Perturbation_Minimum_Bin_Slack",
                        false, elapsed_time_pmbs_per_iteration);
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Perturbation_Minimum_Bin_Slack", perturbation_minimum_bin_slack_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_perturbation_minimum_bin_slack_solution.add(perturbation_minimum_bin_slack_solution.getBestSolution());
                }

                long elapsed_time_pmbs_per_problem = System.currentTimeMillis() - start_time_pmbs_per_problem;

                // Create a new LinkedHashMap for this specific recording
                LinkedHashMap<String, Long> algorithm_time = new LinkedHashMap<>();
                algorithm_time.put("PMBS", elapsed_time_pmbs_per_problem);

                // Add the new map to the list
                time_list.add(algorithm_time);

                // Put the updated list back into the main map
                final_times_map.put(solution.getProblemName(), time_list);

                System.out.println("\tSolutions generated for problem: " + solution.getProblemName() + " in " + elapsed_time_pmbs_per_problem + " milliseconds!");
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_perturbation_minimum_bin_slack_solution, "Perturbation_Minimum_Bin_Slack",
                    result_output_directory, chart_width, chart_height);

            }

            long elapsed_time_pmbs = System.currentTimeMillis() - start_time_pmbs;

            System.out.println("Perturbation Minimum Bin Slack Solutions Generated Successfully for All Problems in " + elapsed_time_pmbs + " milliseconds!");
            System.out.println();



            // Ant Colony Optimization

            System.out.println("Generating Ant Colony Optimization Solutions for " + algorithm_iterations + " iterations....");

            long start_time_aco = System.currentTimeMillis();

            //  Creating and saving Hybrid Ant Colony Optimization solution
            for (BinPackingProblem problem: problems) {

                // Retrieve the time list associated with the problem, or create a new list if none exists
                ArrayList<LinkedHashMap<String, Long>> time_list = final_times_map.getOrDefault(problem.getProblemName(), new ArrayList<>());

                System.out.println("\tGenerating Solutions for problem: " + problem.getProblemName() + "....");

                // Creating Array List of generated final solutions for each problem across all iterations
                ArrayList<Solution> best_final_solutions_per_problem_ant_colony_optimization = new ArrayList<Solution>();

                long start_time_aco_per_problem = System.currentTimeMillis();

                for (int iteration = 1; iteration <= algorithm_iterations; iteration++) {
                    long start_time_aco_per_iteration = System.currentTimeMillis();

                    AntColonyOptimization ACO = new AntColonyOptimization(problem);
                    FinalSolution ant_colony_optimization_solution = ACO.applyHybridAntColonyOptimization();

                    long elapsed_time_aco_per_iteration = System.nanoTime() - start_time_aco_per_iteration;

                    PrintSolutionToFile.saveResult(iteration, ant_colony_optimization_solution, result_output_directory, "Ant_Colony_Optimization",
                        false, elapsed_time_aco_per_iteration);
                    ChartUtilities.buildAndSaveXYCharts(iteration, "Ant_Colony_Optimization", ant_colony_optimization_solution, result_output_directory, chart_width, chart_height);

                    best_final_solutions_per_problem_ant_colony_optimization.add(ant_colony_optimization_solution.getBestSolution());
                }

                long elapsed_time_aco_per_problem = System.currentTimeMillis() - start_time_aco_per_problem;

                // Create a new LinkedHashMap for this specific recording
                LinkedHashMap<String, Long> algorithm_time = new LinkedHashMap<>();
                algorithm_time.put("ACO", elapsed_time_aco_per_problem);

                // Add the new map to the list
                time_list.add(algorithm_time);

                // Put the updated list back into the main map
                final_times_map.put(problem.getProblemName(), time_list);

                System.out.println("\tSolutions generated for problem: " + problem.getProblemName() + " in " + elapsed_time_aco_per_problem + " milliseconds!");
                System.out.println();

                // Creating and Saving Boxplot for each problem across all iterations
                ChartUtilities.buildAndSaveBoxPlot(best_final_solutions_per_problem_ant_colony_optimization, "Ant_Colony_Optimization",
                    result_output_directory, chart_width, chart_height);

            }

            long elapsed_time_aco = System.currentTimeMillis() - start_time_aco;

            System.out.println("Ant Colony Optimization Solutions Generated Successfully for All Problems in " + elapsed_time_aco + " milliseconds!");
            System.out.println();

            // printTimes(final_times_map);

            ChartUtilities.buildAndSaveTimeBarChart(final_times_map, result_output_directory, chart_width, chart_height);
            
            System.out.println("Program Ended Successfully! Results can be found in the '../resources/results/' directory!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // public static void printTimes(LinkedHashMap<String, ArrayList<LinkedHashMap<String, Long>>> final_times_map) {
    //     for (Map.Entry<String, ArrayList<LinkedHashMap<String, Long>>> problemEntry : final_times_map.entrySet()) {
    //         System.out.println("Problem: " + problemEntry.getKey());
    //         List<LinkedHashMap<String, Long>> algorithmList = problemEntry.getValue();

    //         for (int i = 0; i < algorithmList.size(); i++) {
    //             LinkedHashMap<String, Long> algorithmTimes = algorithmList.get(i);
    //             for (Map.Entry<String, Long> algorithmEntry : algorithmTimes.entrySet()) {
    //                 System.out.println("    " + algorithmEntry.getKey() + ": " + algorithmEntry.getValue() + " ms");
    //             }
    //         }
    //         System.out.println();
    //     }
    // }
}