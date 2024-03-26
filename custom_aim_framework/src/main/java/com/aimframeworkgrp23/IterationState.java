package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.List;

public class IterationState {
    int iteration;
    int binCount;
    List<Bin> bins;

    public IterationState(int iteration, int binCount, List<Bin> bins) {
        this.iteration = iteration;
        this.binCount = binCount;
        this.bins = new ArrayList<>(bins);
    }

    public int getIteration() {
        return iteration;
    }

    public int getBinCount() {
        return binCount;
    }

    public List<Bin> getBins() {
        return bins;
    }
}