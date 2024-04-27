package com.aim.coursework;


import com.aimframeworkgrp23.*;
import java.util.ArrayList;
import java.util.Random;

// Sufficient Average Weight Minimum Bin Slack Algorithm
public class PerturbationMBS {

    // Parameters
    private static final int MAX_ITERATIONS = 2000;

    Random rand = new Random();

    private Solution initial_solution;

    public PerturbationMBS(Solution solution) {
        this.initial_solution = solution;
    }

    
    public FinalSolution applyPerturbationMBS() {
        Solution currentBestSolution = Heuristics.copySolution(initial_solution);
        Solution overallBestSolution = Heuristics.copySolution(initial_solution);

        // To store multiple generations' results
        ArrayList<Generation> generation_results = new ArrayList<>();

        // To store final solution
        FinalSolution final_solution = new FinalSolution();

        for (int generation_id = 1; generation_id <= MAX_ITERATIONS; generation_id++) {
            
        }
        

        return final_solution;
    }
}
