package com.aimframeworkgrp23;

import java.util.ArrayList;

public class Solution {
    String problem_name;
    private int binCount;
    private ArrayList<Bin> bins;

    public Solution() {
        this.problem_name = "";
        this.binCount = -1;
        this.bins = new ArrayList<Bin>();
    }

    public String getProblemName() {
        return problem_name;
    }

    public void setProblemName(String problem_name) {
        this.problem_name = problem_name;
    }

    public int getBinCount() {
        return binCount;
    }

    public void setBinCount(int binCount) {
        this.binCount = binCount;
    }

    public ArrayList<Bin> getBins() {
        return bins;
    }

    public void setBins(ArrayList<Bin> bins) {
        this.bins = bins;
    }
}