package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.List;

public class BinPackingProblem {
    private String problemName;
    private int uniqueWeightCount;
    private int binCapacity;
    private List<Item> items;

    public BinPackingProblem(String problemName, int uniqueWeightCount, int binCapacity) {
        this.problemName = problemName;
        this.uniqueWeightCount = uniqueWeightCount;
        this.binCapacity = binCapacity;
        this.items = new ArrayList<>();
    }

    public void addItem(int weight) {
        this.items.add(new Item(weight));
    }

    public String getProblemName() {
        return problemName;
    }

    public int getUniqueWeightCount() {
        return uniqueWeightCount;
    }

    public int getBinCapacity() {
        return binCapacity;
    }

    public List<Item> getItems() {
        return items;
    }
}