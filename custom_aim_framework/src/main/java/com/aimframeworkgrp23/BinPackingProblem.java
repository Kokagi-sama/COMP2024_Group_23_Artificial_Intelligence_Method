package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.Collections;

public class BinPackingProblem {
    private String problemName;
    private int uniqueWeightCount;
    private int binCapacity;
    private ArrayList<Item> items;
    private int nextItemId = 1;

    public BinPackingProblem(String problemName, int uniqueWeightCount, int binCapacity) {
        this.problemName = problemName;
        this.uniqueWeightCount = uniqueWeightCount;
        this.binCapacity = binCapacity;
        this.items = new ArrayList<>();
    }

    public void addItem(int weight) {
        Item newItem = new Item(weight);
        newItem.setItemId(nextItemId++);  // Set the item ID upon creation
        this.items.add(newItem);
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

    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    public ArrayList<Item> randomizeItems() {
        ArrayList<Item> shuffledItems = new ArrayList<>(items);
        Collections.shuffle(shuffledItems);
        // for (Item item : shuffledItems) {
        //     System.out.print(item.getWeight() + " ");
        // }
        // System.out.println();
        return shuffledItems;
    }
}