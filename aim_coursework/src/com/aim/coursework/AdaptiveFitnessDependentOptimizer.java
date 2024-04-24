package com.aim.coursework;

import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

public class AdaptiveFitnessDependentOptimizer {

    // Parameters
    static final int POPULATION_SIZE = 10;
    static final int T_MAX = 100;

    Random rand = new Random();

    private ArrayList<Solution> initial_population;
    private BinPackingProblem problem;

    public AdaptiveFitnessDependentOptimizer(BinPackingProblem problem) {
        this.problem = problem;

        // Initialising initial population
        Initialise initialiser = new Initialise(this.problem);
        this.initial_population = initialiser.getInitialPopulation(POPULATION_SIZE);
    }

    public FinalSolution applyAdaptiveFitnessDependentOptimizerAlgorithm() {
        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();

        for (int generation_id = 1; generation_id < T_MAX; generation_id++) {

            // Initialize best solution with the first solution
            Solution X_star = initial_population.getFirst();
            double X_star_it = X_star.getObjectiveFunctionValue();

            for (Solution X : initial_population) {
                double X_it = X.getObjectiveFunctionValue();

                if (X_it < X_star_it) {
                    X_star = X;
                    X_star_it = X_it;
                }
            }

            for(Solution X : initial_population) {

                double X_it = X.getObjectiveFunctionValue();

                double wf = 0;

                double fw = Math.abs((X_star_it / X_it)) * wf;

                if (fw == 1 || fw == 0 || X_it == 0) {
                    double r = rand.nextDouble();
                    
                }
            }

        }


        return final_solution;
    }
}