package com.aimframeworkgrp23;

import java.util.LinkedHashMap;
import java.util.Map;

public class Solution {
    String problemName;
    Map<Integer, IterationState> iterationStates;
    IterationState finalState;

    public Solution(String problemName) {
        this.problemName = problemName;
        this.iterationStates = new LinkedHashMap<>();
    }

    public void addIterationState(int iteration, IterationState iterationState) {
        this.iterationStates.put(iteration, iterationState);
    }

    public void updateIterationState(int iteration, IterationState iterationState) {
        if (!this.iterationStates.isEmpty()) {
            this.iterationStates.put(iteration, iterationState);
        } else {
            addIterationState(iteration, iterationState); // Add if no state exists
        }
    }

    public void setFinalState(IterationState iterationState) {
        this.finalState = iterationState;
    }

    Map<Integer, IterationState> getIterationStates() {
        return iterationStates;
    }

    public IterationState getFinalState() {
        return this.finalState;
    }
}