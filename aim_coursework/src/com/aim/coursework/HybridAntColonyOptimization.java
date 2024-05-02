package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HybridAntColonyOptimization {
    
    // Parameters
    // item_sizes = [item for weight, count in data0014 for item in [weight] * count]
    // bin_capacity = 10000  # Your bin capacity
    // lower_bound = math.ceil(sum(item_sizes) / bin_capacity)  # Calculate lower bound
    // number_of_ants = 10  # Number of ants
    private static final int max_iterations = 100;
    // Pheromone evaporation coefficient
    private static final double rho = 0.75;
    // Heuristic importance coefficient
    private static final double beta = 2;
    // Pheromone importance coefficient
    // private static final int alpha = 1;
    // Max pheromone level
    private static final double tau_max = 4;
    // Min pheromone level
    // private static final double tau_min = 0.00;
    // Number of bins to free in local search
    private static final int free_n_bins = 4;

    private static double longScalarCoeff=1;

    int noAnts;

    int binCapacity;
    Random rand = new Random();

    ArrayList<Item> allItems = new ArrayList<>();

    // Pheromone matrix as a 2D array
    double[][] pheromone_matrix;
    Map<Integer, Integer> heuristicInfo = new HashMap<>();


    private void setPheromoneMatrix(ArrayList<Item> allItems){
        int n = allItems.size();
        double[][] pheromoneMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                pheromoneMatrix[i][j] = tau_max;
            }
        }
    }

    public void evaporatePheromones() {
        for (int i = 0; i < pheromone_matrix.length; i++) {
            for (int j = 0; j < pheromone_matrix[i].length; j++) {
                pheromone_matrix[i][j] *= rho;
            }
        }
    }

    public void updatePheromones(Solution bestAnt) {
        for (Bin bin : bestAnt.getBins()) {
            double bestFitness = bestAnt.getObjectiveFunctionValue();
            List<Item> items = bin.getItems();
            for (int i = 0; i < items.size(); i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    int itemIId = items.get(i).getItemId();
                    int itemJId = items.get(j).getItemId();
                    pheromone_matrix[itemIId][itemJId] += bestFitness;
                    pheromone_matrix[itemJId][itemIId] += bestFitness;
                }
            }
        }
    }

    // Heuristic info
    private void setHeuristicInfo(ArrayList<Item> allItems){
        for (Item item : allItems) {
            heuristicInfo.put(item.getItemId(), item.getWeight());
        }
    }


    public static double fitnessFunc(Solution solution, int binCapacity) {
        double total = 0;
        for (Bin bin : solution.getBins()) {
            int binSum = bin.getItems().stream().mapToInt(Item::getWeight).sum();
            total += Math.pow((double) binSum / binCapacity, 2);
        }
        return total / solution.getBinCount();
    }

    public int findLeastFilledBin(Solution solution) {
        int leastFilledBinIdx = -1;
        int leastWeightDiff = Integer.MAX_VALUE;
        List<Bin> bins = solution.getBins();

        for (int binIdx = 0; binIdx < bins.size(); binIdx++) {
            Bin bin = bins.get(binIdx);
            int remainingCapacity = bin.getRemainingCapacity();

            if (remainingCapacity < leastWeightDiff) {
                leastWeightDiff = remainingCapacity;
                leastFilledBinIdx = binIdx;
            }
        }
        return leastFilledBinIdx;
    }

    public ModifiedSolution modifySolution(Solution solution, int freeNBins) {
        ModifiedSolution finalSolution = new ModifiedSolution();


        ArrayList<Item> freeItems = new ArrayList<>();
        Solution modifiedSolution = solution;
        for (int i = 0; i < freeNBins; i++) {
            int idx = findLeastFilledBin(solution);
            freeItems.addAll(modifiedSolution.getBins().get(idx).getItems());
            ArrayList<Bin> newBins = modifiedSolution.getBins();
            newBins.remove(idx);
            modifiedSolution.setBins(newBins);
        }
        finalSolution.setFreeItems(freeItems);
        finalSolution.setModifiedSolution(modifiedSolution);
        return finalSolution;
    }

    public int getItemsWeight(ArrayList<Item> items) {
        int totalWeight = 0;
        for (Item item : items) {
            totalWeight += item.getWeight();
        }
        return totalWeight;
    }
    
    public ModifiedBin swapTwoByTwo(Bin bin, ArrayList<Item> newItems) {
        ModifiedBin finalSolution = new ModifiedBin();

        Bin originalBin = bin;
        int currentWeightDiff=bin.getRemainingCapacity();
        ArrayList<Item> oldFree= new ArrayList<Item>();

        finalSolution.setFreeItems(oldFree);
        finalSolution.setModifiedBin(bin);

        for (int i = 0; i < bin.getItems().size(); i++) {
            for (int j = 0; j < bin.getItems().size(); j++) {
                if (i != j) {
                    ModifiedBin modifiedSolution = new ModifiedBin();
                    
                    ArrayList<Item> tempBins= originalBin.getItems();
                    ArrayList<Item> newFree= new ArrayList<Item>();
                    newFree.set(0, tempBins.get(i));
                    newFree.set(1, tempBins.get(j));
                    tempBins.set(i, newItems.get(0));
                    tempBins.set(j, newItems.get(1));
                    int newFreeLoad = getItemsWeight(tempBins);
                    int newFreeRemainingWeight = bin.getCapacity() - newFreeLoad;

                    if (currentWeightDiff > newFreeRemainingWeight && newFreeLoad <= bin.getCapacity()) {
                        Bin modifiedBin = new Bin(bin.getId(), bin.getCapacity(), newFreeRemainingWeight);
                        modifiedBin.setItems(tempBins);
                        modifiedSolution.setFreeItems(newFree);
                        modifiedSolution.setModifiedBin(modifiedBin);
                        return modifiedSolution;
                    }
                }
            }
        }
        return finalSolution;
    }

    public ModifiedBin swapTwoByOne(Bin bin, ArrayList<Item> newItems) {
        ModifiedBin finalSolution = new ModifiedBin();

        Bin originalBin = bin;
        int currentWeightDiff=bin.getRemainingCapacity();
        ArrayList<Item> oldFree= new ArrayList<Item>();

        finalSolution.setFreeItems(oldFree);
        finalSolution.setModifiedBin(bin);

        for (int i = 0; i < bin.getItems().size(); i++) {
            for (int j = 0; j < bin.getItems().size(); j++) {
                if (i != j) {
                    ModifiedBin modifiedSolution = new ModifiedBin();
                    
                    ArrayList<Item> tempBins= originalBin.getItems();
                    ArrayList<Item> newFree= new ArrayList<Item>();
                    newFree.set(0, tempBins.get(i));
                    newFree.set(1, tempBins.get(j));
                    tempBins.set(i, newItems.get(0));
                    tempBins.remove(j);
                    int newFreeLoad = getItemsWeight(tempBins);
                    int newFreeRemainingWeight = bin.getCapacity() - newFreeLoad;

                    if (currentWeightDiff > newFreeRemainingWeight && newFreeLoad <= bin.getCapacity()) {
                        Bin modifiedBin = new Bin(bin.getId(), bin.getCapacity(), newFreeRemainingWeight);
                        modifiedBin.setItems(tempBins);
                        modifiedSolution.setFreeItems(newFree);
                        modifiedSolution.setModifiedBin(modifiedBin);
                        return modifiedSolution;
                    }
                }
            }
        }
        return finalSolution;
    }

    public ModifiedBin swapOneByOne(Bin bin, ArrayList<Item> newItems) {
        ModifiedBin finalSolution = new ModifiedBin();

        Bin originalBin = bin;
        int currentWeightDiff=bin.getRemainingCapacity();
        ArrayList<Item> oldFree= new ArrayList<Item>();

        finalSolution.setFreeItems(oldFree);
        finalSolution.setModifiedBin(bin);

        for (int i = 0; i < bin.getItems().size(); i++) {
            ModifiedBin modifiedSolution = new ModifiedBin();
            
            ArrayList<Item> tempBins= originalBin.getItems();
            ArrayList<Item> newFree= new ArrayList<Item>();
            newFree.set(0, tempBins.get(i));
            tempBins.set(i, newItems.get(0));
            int newFreeLoad = getItemsWeight(tempBins);
            int newFreeRemainingWeight = bin.getCapacity() - newFreeLoad;

            if (currentWeightDiff > newFreeRemainingWeight && newFreeLoad <= bin.getCapacity()) {
                Bin modifiedBin = new Bin(bin.getId(), bin.getCapacity(), newFreeRemainingWeight);
                modifiedBin.setItems(tempBins);
                modifiedSolution.setFreeItems(newFree);
                modifiedSolution.setModifiedBin(modifiedBin);
                return modifiedSolution;

            }
        }
        return finalSolution;
    }

    public Solution firstFitDecreasing(Solution solution, ArrayList<Item> items, int binCapacity) {
        // Sort items by weight in decreasing order
        Collections.sort(items, new Comparator<Item>() {
            @Override
            public int compare(Item item1, Item item2) {
                return Integer.compare(item2.getWeight(), item1.getWeight()); // Reverse order
            }
        });

        for (Item item : items) {
            boolean itemAssigned = false;
            for (Bin bin : solution.getBins()) {
                if (bin.getRemainingCapacity() >= item.getWeight()) {
                    bin.getItems().add(item);
                    bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                    itemAssigned = true;
                    break;
                }
            }
            if (!itemAssigned) {
                Bin newBin = new Bin(solution.getBinCount() + 1, binCapacity, binCapacity - item.getWeight());
                newBin.getItems().add(item);
                solution.getBins().add(newBin);
                solution.setBinCount(solution.getBinCount() + 1);
            }
        }
        return solution;
    }

    public Solution localSearch(Solution solution, int freeNBins) {
        ModifiedSolution modResult = modifySolution(solution, freeNBins);
        ArrayList<Item> freeItems = modResult.getFreeItems();
        Solution modifiedSolution = modResult.getModifiedSolution();
        int binCapacity = solution.getBins().get(0).getCapacity();

        for (int binIdx = 0; binIdx < modifiedSolution.getBinCount(); binIdx++) {
            Bin currentBin = modifiedSolution.getBins().get(binIdx);

            for (int i = 1; i < freeItems.size(); i++) {
                ArrayList<Item> pair = new ArrayList<>();
                pair.add(freeItems.get(i - 1));
                pair.add(freeItems.get(i));

                ModifiedBin swapResult = swapTwoByTwo(currentBin, pair);
                if (swapResult.getFreeItems().size() > 0) {
                    freeItems.set(i - 1, swapResult.getFreeItems().get(0));
                    freeItems.set(i, swapResult.getFreeItems().get(1));
                    modifiedSolution.getBins().set(binIdx, swapResult.getModifiedBin());
                    break;
                }
            }

            for (int i = 1; i < freeItems.size(); i++) {
                ArrayList<Item> pair = new ArrayList<>();
                pair.add(freeItems.get(i - 1));
                pair.add(freeItems.get(i));

                ModifiedBin swapResult = swapTwoByOne(currentBin, pair);
                if (swapResult.getFreeItems().size() > 0) {
                    freeItems.set(i - 1, swapResult.getFreeItems().get(0));
                    freeItems.remove(i);
                    modifiedSolution.getBins().set(binIdx, swapResult.getModifiedBin());
                    break;
                }
            }

            for (int i = 0; i < freeItems.size(); i++) {
                ArrayList<Item> pair = new ArrayList<>();
                pair.add(freeItems.get(i));

                ModifiedBin swapResult = swapOneByOne(currentBin, pair);
                if (swapResult.getFreeItems().size() > 0) {
                    freeItems.set(i, swapResult.getFreeItems().get(0));
                    modifiedSolution.getBins().set(binIdx, swapResult.getModifiedBin());
                    break;
                }
            }
        }

        return firstFitDecreasing(modifiedSolution, freeItems, binCapacity);
    }

    public  Solution findBetter(Solution solution, int binsToOpen) {
        int binCapacity = solution.getBins().get(0).getCapacity();
        double originalFitness = fitnessFunc(solution, binCapacity);
        Solution modifiedSolution = localSearch(solution, binsToOpen);
        double currentFitness = fitnessFunc(modifiedSolution, binCapacity);

        while (originalFitness < currentFitness) {
            Solution tempSolution = Heuristics.copySolution(modifiedSolution); // Clone or deep copy the modified solution
            originalFitness = currentFitness;
            modifiedSolution = localSearch(tempSolution, binsToOpen);
            currentFitness = fitnessFunc(modifiedSolution, binCapacity);
        }

        if (originalFitness >= currentFitness && solution.getBinCount() >= modifiedSolution.getBinCount()) {
            return modifiedSolution;
        } else {
            return solution;
        }
    }

    public boolean packingAllowed(Item item, Bin bin, ArrayList<Item> possibleItems) {
        ArrayList<Item> binItems = bin.getItems();
        int freeSpace = bin.getRemainingCapacity();
        if (item.getWeight() > freeSpace) {
            return false;
        }

        double denominator = 0;
        for (Item i : possibleItems) {
            if (i.getWeight() <= freeSpace) {
                denominator += decisionFormulaTerm(i, binItems);
            }
        }
        double numerator = decisionFormulaTerm(item, binItems);

        double probability = 0;
        if (!possibleItems.isEmpty()) {
            probability = (numerator / denominator);
        }
        double stateUniform = rand.nextDouble() * longScalarCoeff;
        return stateUniform < probability;
    }

    private double decisionFormulaTerm(Item item, ArrayList<Item> binItems) {
        double d = taoCoeff(item, binItems) * Math.pow(item.getWeight(), beta);
        return d * longScalarCoeff;
    }

    private double taoCoeff(Item item, ArrayList<Item> binItems) {
        if (binItems.isEmpty()) {
            return 1;
        }

        double pheromonesSum = 0;
        for (Item binItem : binItems) {
            pheromonesSum += pheromone_matrix[binItem.getItemId()][item.getItemId()];
        }
        return pheromonesSum / binItems.size();
    }


    public ArrayList<Solution> runAnts(ArrayList<Item> items) {
        ArrayList<Solution> solutions = new ArrayList<Solution>();
        for (int i = 0; i < noAnts; i++) {
            Solution solution = new Solution();
            ArrayList<Bin> path = buildSolution(items); // Copy items to avoid modifying the original list
            solution.setBins(path);
            solution = findBetter(solution, free_n_bins);
            solution.setObjectiveFunctionValue(fitnessFunc(solution, binCapacity));
            solutions.add(solution);
        }
        return solutions;
    }


    public ArrayList<Bin> buildSolution(ArrayList<Item> items) {
        ArrayList<Bin> path = new ArrayList<>();
        for(int i = 1; i <= items.size(); i++){
            Bin packedBin = packBin(items);
            packedBin.setId(i);
            if (!packedBin.getItems().isEmpty()) {
                path.add(packedBin);
                for(Item item : packedBin.getItems()){
                    items.remove(item);
                }
            }
            else{
                break;
            }
        }
        return path;
    }

    private Bin packBin(ArrayList<Item> items) {
        Bin bin = new Bin(0, binCapacity, binCapacity);
        ArrayList<Item> binItems =  new ArrayList<>();

        while (!items.isEmpty() && bin.getRemainingCapacity() >= findMinWeight(items)) {
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                if (packingAllowed(item, bin, items)) {
                    binItems.add(item);
                    bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                    items.remove(i);
                    break;  // Break to reassess the best next item after adding one
                }
            }
        }
        bin.setItems(binItems);
        return bin;
    }

    private int findMinWeight(List<Item> items) {
        return items.stream().mapToInt(Item::getWeight).min().orElse(0);
    }

    // private BinPackingProblem problem;

    public HybridAntColonyOptimization(BinPackingProblem problem) {
        // this.problem = problem;

        allItems = problem.getItems();
        setPheromoneMatrix(allItems);
        setHeuristicInfo(allItems);
        binCapacity=problem.getBinCapacity();
    }



    public FinalSolution applyHybridAntColonyOptimization() {
        FinalSolution finalSolution = new FinalSolution();
        Solution globalBestSolution = new Solution();


        for (int iteration = 0; iteration < max_iterations; iteration++) {
            Solution iterationBestSolution = new Solution();
            ArrayList<Solution> solutions = runAnts(allItems);
            evaporatePheromones();

            for(int index = 0; index < solutions.size(); index++){
                Solution currentSolution = solutions.get(index);
                double currentFitness = currentSolution.getObjectiveFunctionValue();
                
                if (iterationBestSolution.getBins().isEmpty() || currentFitness > iterationBestSolution.getObjectiveFunctionValue()) {
                    iterationBestSolution = currentSolution;
                }
            
            }

            if (iteration % 5 == 0 && iteration > 0) {
                updatePheromones(globalBestSolution);
            } else {
                updatePheromones(iterationBestSolution);
            }

            if (iterationBestSolution.getObjectiveFunctionValue() > globalBestSolution.getObjectiveFunctionValue()) {
                globalBestSolution = iterationBestSolution;
            }
        }

        finalSolution.setBestSolution(globalBestSolution);
        return finalSolution;
    }

}