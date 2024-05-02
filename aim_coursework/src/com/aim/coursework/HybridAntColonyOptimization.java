package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class HybridAntColonyOptimization {

    private BinPackingProblem problem;
    
    // Parameters
    // item_sizes = [item for weight, count in data0014 for item in [weight] * count]
    // bin_capacity = 10000  # Your bin capacity
    // lower_bound = math.ceil(sum(item_sizes) / bin_capacity)  # Calculate lower bound
    // number_of_ants = 10  # Number of ants
    private static final int max_iterations = 1;
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


    private double[][] getPheromoneMatrix(ArrayList<Item> allItems){
        int n = allItems.size();
        double[][] pheromoneMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                pheromoneMatrix[i][j] = tau_max;
            }
        }

        return pheromoneMatrix;
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
            for (int i = 0; i < items.size() - 1; i++) {
                for (int j = i + 1; j < items.size(); j++) {
                    int itemIId = items.get(i).getItemId();
                    int itemJId = items.get(j).getItemId();
                    // pheromone_matrix[itemIId][itemJId] += bestFitness;
                    // pheromone_matrix[itemJId][itemIId] += bestFitness;

                    pheromone_matrix[itemIId - 1][itemJId - 1] += bestFitness;
                    pheromone_matrix[itemJId - 1][itemIId - 1] += bestFitness;
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
        // System.out.println("AMBATUKAMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM");


        ArrayList<Item> freeItems = new ArrayList<>();
        Solution modifiedSolution = Heuristics.copySolution(solution);
        for (int i = 0; i < freeNBins; i++) {
            // System.out.println("New Soln:");
            // System.out.println(modifiedSolution.getBins().size());
            int idx = findLeastFilledBin(modifiedSolution);
            freeItems.addAll(modifiedSolution.getBins().get(idx).getItems());
            ArrayList<Bin> newBins = modifiedSolution.getBins();
            newBins.remove(idx);
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
        //System.out.println("AMBATUKAMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM " + bin.getItems().size());
        ModifiedBin finalSolution = new ModifiedBin();

        Bin originalBin = Heuristics.copyBin(bin);
        // System.out.println("AMBATUKAMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMMM " + originalBin.getItems().size());
        int currentWeightDiff=originalBin.getRemainingCapacity();

        finalSolution.setFreeItems(newItems);
        finalSolution.setModifiedBin(originalBin);

        for (int i = 0; i < originalBin.getItems().size(); i++) {
            for (int j = 0; j < originalBin.getItems().size(); j++) {
                if (i != j) {
                    ModifiedBin modifiedBin = new ModifiedBin();
                    
                    ArrayList<Item> tempBins = Heuristics.copyItems(originalBin.getItems()); 
                    ArrayList<Item> newFree = Heuristics.copyItems(newItems);
                    newFree.add(0, tempBins.get(i));
                    newFree.add(1, tempBins.get(j));
                    tempBins.set(i, newItems.get(0));
                    tempBins.set(j, newItems.get(1));
                    int newFreeLoad = getItemsWeight(tempBins);
                    int newFreeRemainingWeight = originalBin.getCapacity() - newFreeLoad;

                    if (currentWeightDiff > newFreeRemainingWeight && newFreeLoad <= originalBin.getCapacity()) {
                        Bin newBin = new Bin(originalBin.getId(), originalBin.getCapacity(), newFreeRemainingWeight);
                        newBin.setItems(tempBins);
                        modifiedBin.setFreeItems(newFree);
                        modifiedBin.setModifiedBin(newBin);
                        return modifiedBin;
                    }
                }
            }
        }
        return finalSolution;
    }

    public ModifiedBin swapTwoByOne(Bin bin, ArrayList<Item> newItems) {
        ModifiedBin finalSolution = new ModifiedBin();

        Bin originalBin = Heuristics.copyBin(bin);
        int currentWeightDiff=originalBin.getRemainingCapacity();

        finalSolution.setFreeItems(newItems);
        finalSolution.setModifiedBin(originalBin);

        

        for (int i = 0; i < originalBin.getItems().size(); i++) {
            for (int j = 0; j < originalBin.getItems().size(); j++) {
                if (i != j) {
                    ModifiedBin modifiedBin = new ModifiedBin();
                    
                    ArrayList<Item> tempBins = Heuristics.copyItems(originalBin.getItems()); 
                    // System.out.println(tempBins.size());
                    ArrayList<Item> newFree= Heuristics.copyItems(newItems);
                    newFree.add(0, tempBins.get(i));
                    newFree.add(1, tempBins.get(j));
                    tempBins.set(i, newItems.get(0));
                    tempBins.remove(j);
                    int newFreeLoad = getItemsWeight(tempBins);
                    int newFreeRemainingWeight = originalBin.getCapacity() - newFreeLoad;

                    if (currentWeightDiff > newFreeRemainingWeight && newFreeLoad <= originalBin.getCapacity()) {
                        Bin newBin = new Bin(originalBin.getId(), originalBin.getCapacity(), newFreeRemainingWeight);
                        newBin.setItems(tempBins);
                        modifiedBin.setFreeItems(newFree);
                        modifiedBin.setModifiedBin(newBin);
                        return modifiedBin;
                    }
                }
            }
        }
        return finalSolution;
    }

    public ModifiedBin swapOneByOne(Bin bin, ArrayList<Item> newItems) {
        ModifiedBin finalSolution = new ModifiedBin();

        Bin originalBin = Heuristics.copyBin(bin);
        int currentWeightDiff=originalBin.getRemainingCapacity();

        finalSolution.setFreeItems(newItems);
        finalSolution.setModifiedBin(originalBin);

        for (int i = 0; i < originalBin.getItems().size(); i++) {
            ModifiedBin modifiedBin = new ModifiedBin();
            
            ArrayList<Item> tempBins = Heuristics.copyItems(originalBin.getItems()); 
            ArrayList<Item> newFree= Heuristics.copyItems(newItems);
            newFree.add(0, tempBins.get(i));
            tempBins.set(i, newItems.get(0));
            int newFreeLoad = getItemsWeight(tempBins);
            int newFreeRemainingWeight = originalBin.getCapacity() - newFreeLoad;

            if (currentWeightDiff > newFreeRemainingWeight && newFreeLoad <= originalBin.getCapacity()) {
                Bin newBin = new Bin(originalBin.getId(), originalBin.getCapacity(), newFreeRemainingWeight);
                newBin.setItems(tempBins);
                modifiedBin.setFreeItems(newFree);
                modifiedBin.setModifiedBin(newBin);
                return modifiedBin;

            }
        }
        return finalSolution;
    }

    public Solution firstFitDecreasing(Solution solution, ArrayList<Item> items, int binCapacity) {
        // Sort items by weight in decreasing order

        Solution ffdSolution = new Solution();
        ffdSolution = Heuristics.copySolution(solution);

        // System.out.println(ffdSolution.getBins().size());

        items.sort((a, b) -> Integer.compare(b.getWeight(), a.getWeight()));

        // for (Item item : items) {
        //     System.out.println(item.getWeight());
        // }

        for (Item item : items) {
            boolean itemAssigned = false;
            for (Bin bin : ffdSolution.getBins()) {
                if (bin.getRemainingCapacity() >= item.getWeight()) {
                    bin.getItems().add(item);
                    bin.setRemainingCapacity(bin.getRemainingCapacity() - item.getWeight());
                    itemAssigned = true;
                    break;
                }
            }
            if (!itemAssigned) {
                Bin newBin = new Bin(ffdSolution.getBins().size() + 1, binCapacity, binCapacity - item.getWeight());
                newBin.getItems().add(item);
                ffdSolution.getBins().add(newBin);
                ffdSolution.setBinCount(ffdSolution.getBins().size() + 1);
            }
        }

        ffdSolution.setObjectiveFunctionValue(Heuristics.objectiveFunction(ffdSolution));

        // System.out.println(ffdSolution.getBins().size());
        return ffdSolution;
    }

    public Solution localSearch(Solution solution, int freeNBins) {

        ModifiedSolution modResult = modifySolution(solution, freeNBins);
        ArrayList<Item> freeItems = modResult.getFreeItems();
        int freeItemSize = freeItems.size();
        Solution modifiedSolution = Heuristics.copySolution(modResult.getModifiedSolution());
        int binCapacity = solution.getBins().getFirst().getCapacity();

        for (int binIdx = 0; binIdx < modifiedSolution.getBins().size(); binIdx++) {
            Bin currentBin = modifiedSolution.getBins().get(binIdx);
            freeItemSize = freeItems.size();

            for (int i = 1; i < freeItemSize; i++) {
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

                freeItemSize = freeItems.size();
            }

            freeItemSize = freeItems.size();

            for (int i = 1; i < freeItemSize; i++) {
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
                freeItemSize = freeItems.size();
            }

            freeItemSize = freeItems.size();

            for (int i = 0; i < freeItemSize; i++) {
                ArrayList<Item> pair = new ArrayList<>();
                pair.add(freeItems.get(i));

                ModifiedBin swapResult = swapOneByOne(currentBin, pair);
                if (swapResult.getFreeItems().size() > 0) {
                    freeItems.set(i, swapResult.getFreeItems().get(0));
                    modifiedSolution.getBins().set(binIdx, swapResult.getModifiedBin());
                    break;
                }
                freeItemSize = freeItems.size();
            }
        }

        return firstFitDecreasing(modifiedSolution, freeItems, binCapacity);
    }

    public  Solution findBetter(Solution solution, int binsToOpen) {

        Solution originalSolution = Heuristics.copySolution(solution);
        double originalFitness = solution.getObjectiveFunctionValue();

        Solution modifiedSolution = localSearch(originalSolution, binsToOpen);
        double currentFitness = Heuristics.objectiveFunction(modifiedSolution);

        while (originalFitness < currentFitness) {
            Solution tempSolution = Heuristics.copySolution(modifiedSolution); // Clone or deep copy the modified solution
            originalFitness = currentFitness;
            modifiedSolution = localSearch(tempSolution, binsToOpen);
            currentFitness = Heuristics.objectiveFunction(modifiedSolution);
        }

        if (originalFitness >= currentFitness && solution.getBins().size() >= modifiedSolution.getBins().size()) {
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
            ArrayList<Item> antItems = Heuristics.copyItems(items);
            // System.out.println("new ant");

            Solution solution = new Solution();
            solution.setProblemName(this.problem.getProblemName());
            ArrayList<Bin> path = buildSolution(antItems);
            solution.setBins(Heuristics.copyBins(path));
            solution.setBinCount(path.size());
            
            //solution.setObjectiveFunctionValue();

            System.out.println(Heuristics.objectiveFunction(solution));

            int binCnt = 0;
            for (Bin bin : solution.getBins()) {

                System.out.println("Bin ID: " + binCnt);
                System.out.println("Bin Capacity: " + bin.getCapacity());
                System.out.println("Remaining Bin Capacity: " + bin.getRemainingCapacity());
                
                for (Item item : bin.getItems()) {
                    System.out.println("Item Weight: " + item.getWeight());
                }

                System.out.println();

                binCnt ++;
            }

            // System.out.println(Heuristics.objectiveFunction(solution));
            // System.out.println(solution.getObjectiveFunctionValue());

            // Solution antSolution = new Solution();
            // antSolution = Heuristics.copySolution(findBetter(solution, free_n_bins)); 
            // antSolution.setObjectiveFunctionValue(Heuristics.objectiveFunction(antSolution));
            // solutions.add(antSolution);

            solutions.add(solution);
        }
        return solutions;
    }


    public ArrayList<Bin> buildSolution(ArrayList<Item> items) {
        ArrayList<Bin> path = new ArrayList<>();
        for(int i = 0; i < items.size(); i++){
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
        // System.out.println(items.size());
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

    public HybridAntColonyOptimization(BinPackingProblem problem) {
        this.problem = problem;
        allItems = this.problem.getItems();
        noAnts = allItems.size();
        pheromone_matrix =  getPheromoneMatrix(allItems);
        setHeuristicInfo(allItems);
        binCapacity=problem.getBinCapacity();
    }



    public FinalSolution applyHybridAntColonyOptimization() {

        // To store multiple generations' results
        ArrayList<Generation> generationResults = new ArrayList<>();

        // To store final solution
        FinalSolution finalSolution = new FinalSolution();
        
        Solution globalBestSolution = new Solution();
        globalBestSolution.setObjectiveFunctionValue(0);

        // Global best no. of bins
        int b_star = -1;


        for (int generation_id = 1; generation_id <= max_iterations; generation_id++) {
            Solution iterationBestSolution = new Solution();
            iterationBestSolution.setObjectiveFunctionValue(0);
            ArrayList<Solution> solutions = runAnts(allItems);
            evaporatePheromones();

            //System.out.println(solutions.size());

            // int binCnt = 1;
            // for (Bin bin : iterationBestSolution.getBins()) {
            //     System.out.println("Bin ID: " + binCnt);
            //     for (Item item: bin.getItems()) {
            //         System.out.println("Item Weight: " + item.getWeight());
            //     }

            //     System.out.println();
            //     binCnt++;
            // }

            for(int index = 0; index < solutions.size(); index++){
                Solution currentSolution = solutions.get(index);
                double currentFitness = currentSolution.getObjectiveFunctionValue();
                
                if (iterationBestSolution.getBins().isEmpty() || currentFitness > iterationBestSolution.getObjectiveFunctionValue()) {
                    //System.out.println("In IF FOR IBS");
                    iterationBestSolution = Heuristics.copySolution(currentSolution);
                    //System.out.println(currentSolution.getObjectiveFunctionValue());
                }
            }

            if (Heuristics.objectiveFunction(iterationBestSolution) > globalBestSolution.getObjectiveFunctionValue()) {
                globalBestSolution = Heuristics.copySolution(iterationBestSolution);
                // System.out.println("In IF");
                
                // System.out.println(iterationBestSolution.getProblemName());
            }

            if (generation_id % 5 == 0 && generation_id > 0) {
                updatePheromones(globalBestSolution);
            } else {
                updatePheromones(iterationBestSolution);
            }

            b_star = globalBestSolution.getBins().size();

            System.out.println("Iteration Obj Func Value: " + Heuristics.objectiveFunction(iterationBestSolution));

            // Store generation results
            Generation generation = new Generation();
            generation.setGenerationId(generation_id);
            generation.setCandidateSolutions(new ArrayList<>(solutions));
            generation.setBestSolution(iterationBestSolution);
            generationResults.add(generation);

            // Terminate if lower bound has been achieved
            // if (b_star <= Heuristics.calculateLowerBound(generation.getBestSolution().getBins(), generation.getBestSolution().getBins().getFirst().getCapacity())) {
            //     break;
            // }
            
        }

        finalSolution.setBestSolution(globalBestSolution);
        finalSolution.setGenerations(generationResults);

        return finalSolution;
    }

}