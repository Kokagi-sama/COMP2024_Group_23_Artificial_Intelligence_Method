package com.aimframeworkgrp23;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PrintSolutionToFile {

    public static void saveInitialResult(int iteration, Solution initial_solution, String output_directory, String algorithm_name, long elapsed_time_initialisation) {
        
        String problem_name = initial_solution.getProblemName();
        String save_directory = output_directory + "/" + problem_name + "/" + algorithm_name + "/Iteration_" + iteration + "/";
        String save_file_path = save_directory + "/" + problem_name + "_" + algorithm_name + "_" + "Iteration_" + iteration + ".txt";

        try {

            // Ensure directory exists
            new File(save_directory).mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file_path));

            writer.write("==== Initial Solution for Iteration: " + iteration + " of Problem: " + problem_name + " ====\n");
            writer.write("Obejctive function value: " + initial_solution.getObjectiveFunctionValue() + "\n");
            writer.write("Bins used: " + initial_solution.getBinCount() + "\n");
            writer.write("Time taken to generate initial solution: " + ((double) elapsed_time_initialisation / 1000000) + " milliseconds\n");
           
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void saveResult(int iteration, FinalSolution final_solution, String output_directory, String algorithm_name, boolean nanoSecondFlag, long elapsed_time_initialisation) {

        ArrayList<Generation> generations = final_solution.getGenerations();
        Solution overall_best_solution = final_solution.getBestSolution();
        String problem_name = generations.getFirst().getBestSolution().getProblemName();
        String save_directory = output_directory + "/" + problem_name + "/" + algorithm_name + "/Iteration_" + iteration + "/";
        String save_file_path = save_directory + "/" + problem_name + "_" + algorithm_name + "_" + "Iteration_" + iteration + ".txt";

        try {

            // Ensure directory exists
            new File(save_directory).mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file_path));

            writer.write("==== Overall Best Solution for iteration: " + iteration + "of Problem: " + problem_name + " ====\n");
            writer.write("Obejctive function value: " + overall_best_solution.getObjectiveFunctionValue() + "\n");
            writer.write("Bins used: " + overall_best_solution.getBinCount() + "\n\n");
            writer.write("Time taken to generate initial solution: " + (nanoSecondFlag ? ((double) elapsed_time_initialisation / 1000000) : elapsed_time_initialisation) + " milliseconds\n");

            // // Printing bins and contents for debugging
            // for (Bin bin : overall_best_solution.getBins()) {
            //     writer.write("Bin " + bin.getId() + " (Capacity: " + bin.getCapacity() + ", Remaining: " + bin.getRemainingCapacity() + "):\n");
            //     for (Item item : bin.getItems()) {
            //         writer.write("    Item ID: " + item.getItemId() + ", Weight: " + item.getWeight() + "\n");
            //     }
            //     writer.write("\n");
            // }

            for (Generation generation : generations) {
                
                int generation_id = generation.getGenerationId();
                Solution solution = generation.getBestSolution();

                writer.write("==== Best Solution for Generation: " + generation_id + " ====\n");
                writer.write("Obejctive function value: " + solution.getObjectiveFunctionValue() + "\n");
                writer.write("Bins used: " + solution.getBinCount() + "\n");
        
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }    
}