package com.aimframeworkgrp23;

import java.util.ArrayList;

public class FinalSolution {
    Solution best_solution;
    ArrayList<Generation> generations;

    public Solution getBestSolution() {
        return best_solution;
    }


    public void setBestSolution(Solution best_solution) {
        this.best_solution = best_solution;
    }


    public ArrayList<Generation> getGenerations() {
        return generations;
    }


    public void setGenerations(ArrayList<Generation> generations) {
        this.generations = generations;
    }

}
