package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;


public class GeneticAlgorithm {

    // Parameters
    static final int POPULATION_SIZE = 100;
    static final int MAX_GENERATIONS = 1000;
    static final int TOURNAMENT_SIZE = 10;

    Random rand = new Random();

    private ArrayList<Solution> initialPopulation;
    private BinPackingProblem problem;

    public GeneticAlgorithm(BinPackingProblem problem) {
        this.problem = problem;

        // Initialising initial population
        Initialise initialiser = new Initialise(this.problem);
        this.initialPopulation = initialiser.getInitialPopulation(POPULATION_SIZE);
    }

    public Solution tournamentSelection() {
        ArrayList<Solution> tournament = new ArrayList<>();
        // Randomly picking candidates for the tournament
        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int randomIndex = rand.nextInt(initialPopulation.size());
            tournament.add(initialPopulation.get(randomIndex));
        }
    
        // Selecting the best solution from the tournament
        return tournament.stream()
                         .max(Comparator.comparingDouble(Solution::getObjectiveFunctionValue))
                         .orElse(null);
    }

    // Perform crossover between two parent solutions
    public Solution crossover(Solution parent1, Solution parent2) {
        // Randomly choose the crossover point based on the number of items
        int crossoverPoint = rand.nextInt(parent1.getAllBinItems().size());
        
        // Create a new offspring solution
        Solution offspring = new Solution();
        offspring.setProblemName(parent1.getProblemName());  // Assuming the problem name is the same for both parents

        // First, take bins and their items from parent1 up to the crossover point
        ArrayList<Item> allItems = new ArrayList<>();  // To keep track of all items and ensure all are assigned
        for (int i = 0; i < crossoverPoint; i++) {
            Item item = new Item(parent1.getAllBinItems().get(i).getWeight());
            item.setBinId(parent1.getAllBinItems().get(i).getBinId());  // Maintain the original bin assignment
            item.setItemId(parent1.getAllBinItems().get(i).getItemId()); //Maintain the original item id
            allItems.add(item);
        }

        // Then attempt to assign items from parent2 starting from the crossover point
        for (int i = crossoverPoint; i < parent2.getAllBinItems().size(); i++) {
            Item item = new Item(parent2.getAllBinItems().get(i).getWeight());
            item.setBinId(parent2.getAllBinItems().get(i).getBinId());  // Start with original bin assignment
            item.setItemId(parent2.getAllBinItems().get(i).getItemId()); //Maintain the original item id
            allItems.add(item);
        }

        // Now place all items into bins, trying to respect their bin IDs if possible
        ArrayList<Bin> bins = new ArrayList<>();

        int binId = 1;

        for (Item item : allItems) {
            boolean placed = false;
            for (Bin bin : bins) {
                if (bin.getId() == item.getBinId() && bin.getRemainingCapacity() >= item.getWeight()) {
                    bin.getItems().add(item);
                    item.setBinId(bins.size());
                    bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                    placed = true;
                    break;
                }
            }
            if (!placed) {
                // If item was not placed, use First Fit to find a new bin
                placed = firstFit(bins, item);
            }
            if (!placed) {
                // If still not placed, create a new bin
                Bin newBin = new Bin(binId++, parent1.getBins().getFirst().getCapacity(), parent1.getBins().getFirst().getCapacity() - item.getWeight());
                newBin.getItems().add(item);
                bins.add(newBin);
            }
        }

        offspring.setBins(bins);
        offspring.setBinCount(bins.size());
        offspring.setObjectiveFunctionValue(Heuristics.objectiveFunction(bins));

        return offspring;
    }

    // Utility method to place an item using the First Fit heuristic
    private boolean firstFit(ArrayList<Bin> bins, Item item) {
        for (Bin bin : bins) {
            if (bin.getRemainingCapacity() >= item.getWeight()) {
                bin.getItems().add(item);
                bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                return true;
            }
        }
        return false;
    }
    
    private Solution mutate(Solution offspring) {

        int binCount = offspring.getBinCount();
        ArrayList<Bin> bins = offspring.getBins();

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
                        
                        if (Heuristics.objectiveFunction(offspring) >= 0) {
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

                        if (Heuristics.objectiveFunction(offspring) >= 0) {
                            break;
                        }
                    }
                }
            }
        }

        offspring.setObjectiveFunctionValue(Heuristics.objectiveFunction(offspring));
      
        return offspring;
    }

    // Calculate improvement after bin swap (Objective function for improvement of bins)
    private double calculateDeltaF(Bin binI, Bin binJ, Item itemI, Item itemJ) {
        int lI = binI.getCapacity() - binI.getRemainingCapacity();
        int lJ = binJ.getCapacity() - binJ.getRemainingCapacity();
        int tI = itemI != null ? itemI.getWeight() : 0;
        int tJ = itemJ != null ? itemJ.getWeight() : 0;

        return Math.pow((lI - tI + tJ), 2) + Math.pow((lJ + tI - tJ), 2) - Math.pow(lI, 2) - Math.pow(lJ, 2);
    }


    public FinalSolution applyGeneticAlgorithm() {
        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();

        for (int generation_id = 1; generation_id <= MAX_GENERATIONS; generation_id++) {
            // Get parents
            Solution parent1 = tournamentSelection();
            Solution parent2 = tournamentSelection();
            
            // Get two offsprings from parents
            Solution offspring1 = crossover(parent1, parent2);
            Solution offspring2 = crossover(parent1, parent2);

            // Get better offspring
            Solution betterOffspring = Heuristics.objectiveFunction(offspring1) > Heuristics.objectiveFunction(offspring2) ? offspring1 : offspring2;
            
            // Mutate the better offspring
            Solution mutatedOffspring = mutate(betterOffspring);

            // Identify the worst solution in the initial population
            Solution worstSolution = initialPopulation.stream()
                                                      .min(Comparator.comparingDouble(Solution::getObjectiveFunctionValue))
                                                      .orElse(null);

            // Compare and replace if the mutated offspring is better
            if (worstSolution != null && Heuristics.objectiveFunction(mutatedOffspring) > Heuristics.objectiveFunction(worstSolution)) {
                // Replace the worst solution with the mutated offspring
                int worstIndex = initialPopulation.indexOf(worstSolution);
                if (worstIndex != -1) {
                    initialPopulation.set(worstIndex, mutatedOffspring);
                }
            }

            // Optionally store generation results
            Generation generation = new Generation();
            generation.setGenerationId(generation_id);
            generation.setCandidateSolutions(new ArrayList<>(initialPopulation));
            generation.setBestSolution(initialPopulation.stream()
                                                        .max(Comparator.comparingDouble(Solution::getObjectiveFunctionValue))
                                                        .orElse(null));
            generation_results.add(generation);
        }

        // Set the best solution from the last generation
        final_solution.setBestSolution(generation_results.get(generation_results.size() - 1).getBestSolution());
        final_solution.setGenerations(generation_results);
        return final_solution;
    }
}