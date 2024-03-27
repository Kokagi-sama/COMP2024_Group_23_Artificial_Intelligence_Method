package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.Random;
import java.util.ArrayList;

public class SimulatedAnnealing {
    private static final double START_TEMPERATURE = 1000.0;
    private static final double COOLING_RATE = 0.95;
    private static final int ITERATIONS_PER_TEMPERATURE = 100;
    private static final int POPULATION_SIZE = 50;
    // private static final double BOLTZMANN_CONSTANT = 0.2;

    private final Solution initialSolution;

    public SimulatedAnnealing(Solution initialSolution) {
        this.initialSolution = initialSolution;
    }

    public FinalSolution applySimulatedAnnealing() {
        double temperature = START_TEMPERATURE;
        Solution currentBestSolution = Heuristics.copySolution(initialSolution);
        Solution overallBestSolution = Heuristics.copySolution(initialSolution);
        int generation_id = 0;

        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<Generation>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();

        while (temperature > 1) {
            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++) {
                ArrayList<Solution> neighbourhood_solutions = Heuristics.generateNeighbour(currentBestSolution, POPULATION_SIZE);

                ArrayList<Solution> swapped_neighbourhood_solutions = Heuristics.swapAndEvaluate(neighbourhood_solutions);

                for (Solution neighbourhood_solution: swapped_neighbourhood_solutions) {
                    if (Heuristics.objectiveFunction(neighbourhood_solution) > Heuristics.objectiveFunction(currentBestSolution) ||
                        acceptWorseSolution(Heuristics.objectiveFunction(currentBestSolution), Heuristics.objectiveFunction(neighbourhood_solution), temperature)) {
                            currentBestSolution = Heuristics.copySolution(neighbourhood_solution);
                    }

                    if (Heuristics.objectiveFunction(currentBestSolution) > Heuristics.objectiveFunction(overallBestSolution)) {
                        overallBestSolution = Heuristics.copySolution(currentBestSolution);
                    }

                }

                Generation generation = new Generation();
                generation.setGenerationId(generation_id);
                generation.setCandidateSolutions(swapped_neighbourhood_solutions);
                generation.setBestSolution(currentBestSolution);
                generation_results.add(generation);

                generation_id++;
            }
            
            temperature *= COOLING_RATE;
            
        }

        final_solution.setBestSolution(overallBestSolution);
        final_solution.setGenerations(generation_results);
        return final_solution;
    }

    private boolean acceptWorseSolution(double currentFitness, double newFitness, double temperature) {
        if (newFitness > currentFitness) {
            return true;
        }
        double acceptanceProbability = Math.exp((newFitness - currentFitness) / temperature);
        //System.out.println((newFitness - currentFitness) / BOLTZMANN_CONSTANT * temperature);
       
        // Getting a random probabillity of accepting worse solutions
        Random rand = new Random();
        return acceptanceProbability > rand.nextDouble(1.0);
    }
}