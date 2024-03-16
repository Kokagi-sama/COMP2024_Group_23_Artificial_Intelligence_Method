package org.example;

import com.aimframeworkgrp23.Item;

import java.util.ArrayList;
import java.util.List;

// Inner class to represent a Bin
public class Bin {
    List<Item> items;
    int remainingCapacity;

    public Bin(int capacity) {
        this.remainingCapacity = capacity;
        this.items = new ArrayList<>();
    }
}