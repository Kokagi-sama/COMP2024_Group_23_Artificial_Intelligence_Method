package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.Random;
import java.util.ArrayList;

public class SimulatedAnnealingReheating {
    private static final double START_TEMPERATURE = 100.0;
    private static final double COOLING_RATE = 0.95;
    private static final int ITERATIONS_PER_TEMPERATURE = 10;
    private static final int POPULATION_SIZE = 20;
    private static final double BOLTZMANN_CONSTANT = 0.2;
    private static final double MAX_STAGNANT_GENERATIONS = 100;
    private static final double MIN_TEMPERATURE_FOR_REHEAT = START_TEMPERATURE * 0.05;
    private static final double REHEAT_FACTOR = 2.0;
    private static int maxGenerations = 50;

    // Defining common Random object
    Random rand = new Random();

    private final Solution initialSolution;

    public SimulatedAnnealingReheating(Solution initialSolution) {
        this.initialSolution = initialSolution;
    }

    public FinalSolution applySimulatedAnnealing() {
        double temperature = START_TEMPERATURE;
        Solution currentBestSolution = Heuristics.copySolution(initialSolution);
        Solution overallBestSolution = Heuristics.copySolution(initialSolution);
        int generation_id = 0;
    
        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<>();
    
        // To store final solution
        FinalSolution final_solution = new FinalSolution();
    
        double lastImprovementTemperature = temperature; // Track the temperature at the last improvement
        int stagnantGenerations = 0; // Counter for generations without improvement
    
        while (temperature > 1 && generation_id < maxGenerations) {
            boolean improvedInThisGeneration = false;
    
            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++) {
                ArrayList<Solution> neighbourhood_solutions = Heuristics.generateNeighbour(currentBestSolution, POPULATION_SIZE);
                ArrayList<Solution> swapped_neighbourhood_solutions = swapAndEvaluate(neighbourhood_solutions);
    
                for (Solution neighbourhood_solution : swapped_neighbourhood_solutions) {
                    if (neighbourhood_solution.getObjectiveFunctionValue() > currentBestSolution.getObjectiveFunctionValue() ||
                        acceptWorseSolution(currentBestSolution.getProblemName(), currentBestSolution.getObjectiveFunctionValue(), neighbourhood_solution.getObjectiveFunctionValue(), temperature)) {
                        currentBestSolution = Heuristics.copySolution(neighbourhood_solution);
                        improvedInThisGeneration = true;
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
    
                generation_id++;
            }
    
            if (improvedInThisGeneration) {
                lastImprovementTemperature = temperature;
                stagnantGenerations = 0;
            } else {
                stagnantGenerations++;
            }
    
            // Reheat if no improvement for a number of generations or temperature falls too low
            if (stagnantGenerations > MAX_STAGNANT_GENERATIONS || temperature < MIN_TEMPERATURE_FOR_REHEAT) {
                temperature = lastImprovementTemperature * REHEAT_FACTOR; // Reheat to a fraction of the temperature at last improvement
                stagnantGenerations = 0; // Reset the counter
            } else {
                temperature *= COOLING_RATE;
            }
        }
    
        final_solution.setBestSolution(overallBestSolution);
        final_solution.setGenerations(generation_results);
        return final_solution;
    }

    private boolean acceptWorseSolution(String problemName, double currentFitness, double newFitness, double temperature) {
        if (newFitness > currentFitness) {
            return true;
        }

        double acceptance = (newFitness - currentFitness) / BOLTZMANN_CONSTANT * temperature;
        double acceptanceProbability = Math.exp(acceptance);
        // System.out.println("Problem Name: "+problemName+"\nNewFitness: "+newFitness+"\nCurrent Fitness:"+currentFitness+"\nTemperature: "+temperature);
        // System.out.println(acceptance);
        // System.out.println(acceptanceProbability);
       
        // Getting a random probabillity of accepting worse solutions
        return acceptanceProbability > rand.nextDouble(1.0);
    }

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
}