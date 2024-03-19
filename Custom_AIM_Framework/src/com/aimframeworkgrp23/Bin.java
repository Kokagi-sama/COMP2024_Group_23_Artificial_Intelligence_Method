package com.aimframeworkgrp23;

import java.util.ArrayList;
import java.util.List;

// Inner class to represent a Bin
public class Bin {
    int id;
    List<Item> items;
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

    public List<Item> getItems() {
        return this.items;
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