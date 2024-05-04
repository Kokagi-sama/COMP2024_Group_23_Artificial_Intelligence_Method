package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.Random;
import java.util.ArrayList;

public class SimulatedAnnealing {

    // Parameters
    private static final double START_TEMPERATURE = 100.0;
    private static final double COOLING_RATE = 0.95;
    private static final int ITERATIONS_PER_TEMPERATURE = 50;
    private static final int POPULATION_SIZE = 20;
    private static final double BOLTZMANN_CONSTANT = 0.2;

    // Random object
    Random rand = new Random();

    // Defining Initial solution object
    private final Solution initialSolution;

    // Public Constructor
    public SimulatedAnnealing(Solution initialSolution) {
        this.initialSolution = initialSolution;
    }

    // Generate a neighbor for each solution in the map
    public static ArrayList<Solution> generateNeighbour(Solution currentSolution, int population_size) {
        
        ArrayList<Solution> neighbourhood = new ArrayList<Solution>();

        for (int i = 0; i < population_size; i++) {
            neighbourhood.add(Heuristics.copySolution(currentSolution));
        }
    
        return neighbourhood;
    }

    // Swap and evaluate items in bins from neighbourhood
    private ArrayList<Solution> swapAndEvaluate(ArrayList<Solution> neighbourhood) {

        for (Solution neighbour : neighbourhood) {
            int binCount = neighbour.getBinCount();
            ArrayList<Bin> bins = neighbour.getBins();

            for (int i = 0; i < binCount - 1; i++) {
                for (int j = i + 1; j < binCount; j++) {
                    Bin binI = bins.get(i);
                    Bin binJ = bins.get(j);

                    // Swap (1,0) - Moving one item from bin I to bin J
                    if (!binI.getItems().isEmpty()) {
                        int itemIndex = rand.nextInt(binI.getItems().size());
                        Item itemI = binI.getItems().get(itemIndex);

                        double deltaF = calculateDeltaF(binI, binJ, itemI, null);
                        if (deltaF >= 0 && binJ.getRemainingCapacity() >= itemI.getWeight()) {
                            binI.getItems().remove(itemI);
                            binJ.getItems().add(itemI);
                            binI.setRemainingCapacity(binI.getRemainingCapacity() + itemI.getWeight());
                            binJ.setRemainingCapacity(binJ.getRemainingCapacity() - itemI.getWeight());
                            
                            if (Heuristics.objectiveFunction(neighbour) >= 0) {
                                break;
                            }
                        }
                    }

                    // Swap (1,1) - Swapping items between bin I and bin J
                    if (!binI.getItems().isEmpty() && !binJ.getItems().isEmpty()) {
                        int itemIndexI = rand.nextInt(binI.getItems().size());
                        int itemIndexJ = rand.nextInt(binJ.getItems().size());
                        Item itemI = binI.getItems().get(itemIndexI);
                        Item itemJ = binJ.getItems().get(itemIndexJ);

                        double deltaF = calculateDeltaF(binI, binJ, itemI, itemJ);
                        if (deltaF >= 0 && 
                            binI.getRemainingCapacity() + itemJ.getWeight() - itemI.getWeight() >= 0 &&
                            binJ.getRemainingCapacity() + itemI.getWeight() - itemJ.getWeight() >= 0) {
                            binI.getItems().set(itemIndexI, itemJ);
                            binJ.getItems().set(itemIndexJ, itemI);
                            binI.setRemainingCapacity(binI.getRemainingCapacity() + itemJ.getWeight() - itemI.getWeight());
                            binJ.setRemainingCapacity(binJ.getRemainingCapacity() + itemI.getWeight() - itemJ.getWeight());

                            if (Heuristics.objectiveFunction(neighbour) >= 0) {
                                break;
                            }
                        }
                    }
                }
            }

            neighbour.setObjectiveFunctionValue(Heuristics.objectiveFunction(neighbour));
        }
        return neighbourhood;
    }

    // Calculate improvement after bin swap (Objective function for improvement of bins)
    private double calculateDeltaF(Bin binI, Bin binJ, Item itemI, Item itemJ) {
        int lI = binI.getCapacity() - binI.getRemainingCapacity();
        int lJ = binJ.getCapacity() - binJ.getRemainingCapacity();
        int tI = itemI != null ? itemI.getWeight() : 0;
        int tJ = itemJ != null ? itemJ.getWeight() : 0;

        return Math.pow((lI - tI + tJ), 2) + Math.pow((lJ + tI - tJ), 2) - Math.pow(lI, 2) - Math.pow(lJ, 2);
    }

    // Solution acceptance with random probability of accepting worse solution to escape local optima
    private boolean acceptWorseSolution(String problemName, double currentFitness, double newFitness, double temperature) {
        if (newFitness > currentFitness) {
            return true;
        }

        double acceptance = (newFitness - currentFitness) / BOLTZMANN_CONSTANT * temperature;
        double acceptanceProbability = Math.exp(acceptance);

        // Getting a random probabillity of accepting worse solutions
        return acceptanceProbability > rand.nextDouble(1.0);
    }

    // Main Simulated Annealing Function
    public FinalSolution applySimulatedAnnealing() {
        double temperature = START_TEMPERATURE;
        Solution currentBestSolution = Heuristics.copySolution(initialSolution);
        Solution overallBestSolution = Heuristics.copySolution(initialSolution);
        int generation_id = 1;

        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<Generation>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();

        // Global best no. of bins
        int b_star = -1;

        // Flag to end Simulated Annealing if lower bound has been achieved
        boolean end = false;

        while (temperature > 1) {
            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++, generation_id++) {
                ArrayList<Solution> neighbourhood_solutions = generateNeighbour(currentBestSolution, POPULATION_SIZE);
                ArrayList<Solution> swapped_neighbourhood_solutions = swapAndEvaluate(neighbourhood_solutions);

                for (Solution neighbourhood_solution: swapped_neighbourhood_solutions) {
                    if (neighbourhood_solution.getObjectiveFunctionValue() > currentBestSolution.getObjectiveFunctionValue() ||
                        acceptWorseSolution(currentBestSolution.getProblemName(), currentBestSolution.getObjectiveFunctionValue(), neighbourhood_solution.getObjectiveFunctionValue(), temperature)) {
                            currentBestSolution = Heuristics.copySolution(neighbourhood_solution);
                    }

                    if (currentBestSolution.getObjectiveFunctionValue() > overallBestSolution.getObjectiveFunctionValue()) {
                        overallBestSolution = Heuristics.copySolution(currentBestSolution);
                    }

                }

                Generation generation = new Generation();
                generation.setGenerationId(generation_id);
                generation.setCandidateSolutions(swapped_neighbourhood_solutions);
                generation.setBestSolution(currentBestSolution);
                generation_results.add(generation);

                b_star = generation.getBestSolution().getBinCount();

                // Terminate if lower bound has been achieved
                if (b_star <= Heuristics.calculateLowerBound(generation.getBestSolution().getBins(), generation.getBestSolution().getBins().getFirst().getCapacity())) {
                    end = true;
                    break;
                }

            }
            
            temperature *= COOLING_RATE;
            
            // Terminate if lower bound has been achieved
            if (end == true) {
                System.out.println(String.format("\tReached lower bound of %d, terminating the algorithm.", b_star));
                break;
            }
        }

        // Store final solution
        final_solution.setBestSolution(overallBestSolution);
        final_solution.setGenerations(generation_results);
        return final_solution;
    }
}