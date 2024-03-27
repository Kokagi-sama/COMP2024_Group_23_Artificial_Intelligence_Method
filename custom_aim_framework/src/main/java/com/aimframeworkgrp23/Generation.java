package com.aimframeworkgrp23;

import java.util.ArrayList;

public class Generation {
    int generation_id;
    int population_size;
    ArrayList<Solution> candidate_solutions;
    Solution best_solution;

    public Generation() {
        this.generation_id = -1;
        this.population_size = -1;
        this.candidate_solutions = new ArrayList<Solution>();
        this.best_solution = new Solution();
    }

    public int getGenerationId() {
        return generation_id;
    }
    public void setGenerationId(int generation_id) {
        this.generation_id = generation_id;
    }
    public ArrayList<Solution> getCandidateSolutions() {
        return candidate_solutions;
    }
    public void setCandidateSolutions(ArrayList<Solution> candidate_solutions) {
        this.candidate_solutions = candidate_solutions;
    }
    public Solution getBestSolution() {
        return best_solution;
    }
    public void setBestSolution(Solution best_solution) {
        this.best_solution = best_solution;
    }
}