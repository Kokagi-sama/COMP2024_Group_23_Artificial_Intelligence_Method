// package com.aim.coursework;

// import java.util.List;
// import com.aimframeworkgrp23.*;

// public class SimulatedAnnealingOld {
//    private static final double START_TEMPERATURE = 100.0;
//    private static final double COOLING_RATE = 0.95;
//    private static final int ITERATIONS_PER_TEMPERATURE = 100;

//    private final Heuristics heuristic;
//    private final List<Bin> initialSolution;

//    public SimulatedAnnealingOld(Heuristics heuristic, List<Bin> initialSolution) {
//        this.heuristic = heuristic;
//        this.initialSolution = initialSolution;
//    }

//    public List<Bin> applySimulatedAnnealing() {
//        double temperature = START_TEMPERATURE;
//        List<Bin> currentSolution = heuristic.copySolution(initialSolution);
//        List<Bin> bestSolution = heuristic.copySolution(initialSolution);

//        while (temperature > 1) {
//            for (int i = 0; i < ITERATIONS_PER_TEMPERATURE; i++) {
//                List<Bin> newSolution = heuristic.generateNeighbor(currentSolution);

//                if (heuristic.objectiveFunction(newSolution) > heuristic.objectiveFunction(currentSolution) ||
//                        acceptWorseSolution(heuristic.objectiveFunction(currentSolution),
//                                heuristic.objectiveFunction(newSolution), temperature)) {
//                    currentSolution = heuristic.copySolution(newSolution);
//                }

//                if (heuristic.objectiveFunction(currentSolution) > heuristic.objectiveFunction(bestSolution)) {
//                    bestSolution = heuristic.copySolution(currentSolution);
//                }
//            }

//            temperature *= COOLING_RATE;
//        }

//        return bestSolution;
//    }

//    private boolean acceptWorseSolution(double currentFitness, double newFitness, double temperature) {
//        if (newFitness < currentFitness) {
//            return true;
//        }
//        double acceptanceProbability = Math.exp((currentFitness - newFitness) / temperature);
//        return acceptanceProbability > heuristic.getRandom().nextDouble();
//    }
// }