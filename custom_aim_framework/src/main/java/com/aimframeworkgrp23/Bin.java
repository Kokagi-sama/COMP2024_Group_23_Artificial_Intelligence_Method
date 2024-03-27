package com.aimframeworkgrp23;

import java.util.ArrayList;

// Inner class to represent a Bin
public class Bin {
    int id;
    ArrayList<Item> items;
    int capacity;
    int remainingCapacity;

    public Bin(int id, int capacity, int remainingCapacity) {
        this.id = id;
        this.capacity = capacity;
        this.remainingCapacity = remainingCapacity;
        this.items = new ArrayList<>();
    }

    public int getId() {
        return this.id;
    }

    public ArrayList<Item> getItems() {
        return this.items;
    }public Solution copySolution(Solution solution) {
        return solution;
      }

    public int getCapacity() {
        return this.capacity;
    }

    public int getRemainingCapacity() {
        return this.remainingCapacity;
    }

    public void setRemainingCapacity(int remainingCapacity) {
        this.remainingCapacity = remainingCapacity;
    }
}