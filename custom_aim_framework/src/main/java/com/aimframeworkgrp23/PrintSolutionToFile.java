package com.aimframeworkgrp23;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class PrintSolutionToFile {

    public static void saveInitialResult(Solution initial_solution, String output_directory, String algorithm_name) {
        
        String problem_name = initial_solution.getProblemName();
        String save_directory = output_directory + "/" + problem_name + "/" + algorithm_name;
        String save_file_path = save_directory + "/" + problem_name + "_" + algorithm_name + ".txt";

        try {

            // Ensure directory exists
            new File(save_directory).mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file_path));

            writer.write("==== Initial Solution for problem: " + problem_name + " ====\n");
            writer.write("Obejctive function value: " + initial_solution.getObjectiveFunctionValue() + "\n");
            writer.write("Bins used: " + initial_solution.getBinCount() + "\n");
           
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    public static void saveResult(FinalSolution final_solution, String output_directory, String algorithm_name) {

        ArrayList<Generation> generations = final_solution.getGenerations();
        Solution overall_best_solution = final_solution.getBestSolution();
        String problem_name = generations.getFirst().getBestSolution().getProblemName();
        String save_directory = output_directory + "/" + problem_name + "/" + algorithm_name;
        String save_file_path = save_directory + "/" + problem_name + "_" + algorithm_name + ".txt";

        

        try {

            // Ensure directory exists
            new File(save_directory).mkdirs();

            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file_path));

            writer.write("==== Overall Best Solution for Problem: " + problem_name + " ====\n");
            writer.write("Obejctive function value: " + overall_best_solution.getObjectiveFunctionValue() + "\n");
            writer.write("Bins used: " + overall_best_solution.getBinCount() + "\n\n");

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