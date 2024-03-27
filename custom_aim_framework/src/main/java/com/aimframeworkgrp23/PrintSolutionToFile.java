package com.aimframeworkgrp23;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class PrintSolutionToFile {

    public static void checkAndClearDirectory(String directoryPath) throws IOException {
        Path directory = Paths.get(directoryPath);

        // Create the directory if it doesn't exist, then return without clearing
        if (Files.notExists(directory)) {
            Files.createDirectories(directory);
            return;
        }

        // If the directory exists, clear the contents
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory)) {
            for (Path file : stream) {
                Files.delete(file);  // Delete each file/directory in the directory
            }
        }
    }

    public static void saveInitialResult(Solution initial_solution, String output_directory, String algorithm_name) {
        
        String problem_name = initial_solution.getProblemName();
        String save_directory = output_directory + "/" + problem_name + "/" + algorithm_name;
        String save_file_path = save_directory + "/" + problem_name + "_" + algorithm_name + ".txt";

        try {

            checkAndClearDirectory(save_directory);
        
            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file_path));

            writer.write("==== Initial Solution for problem: " + problem_name + " ====\n");
            writer.write("Obejctive function value: " + Heuristics.objectiveFunction(initial_solution) + "\n");
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
            checkAndClearDirectory(save_directory);

            BufferedWriter writer = new BufferedWriter(new FileWriter(save_file_path));

            writer.write("==== Overall Best Solution for Problem: " + problem_name + " ====\n");
            writer.write("Obejctive function value: " + Heuristics.objectiveFunction(overall_best_solution) + "\n");
            writer.write("Bins used: " + overall_best_solution.getBinCount() + "\n\n");

            for (Generation generation : generations) {
                
                int generation_id = generation.getGenerationId();
                Solution solution = generation.getBestSolution();

                writer.write("==== Best Solution for Generation: " + generation_id + " ====\n");
                writer.write("Obejctive function value: " + Heuristics.objectiveFunction(solution) + "\n");
                writer.write("Bins used: " + solution.getBinCount() + "\n");
        
            }

            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }    
}