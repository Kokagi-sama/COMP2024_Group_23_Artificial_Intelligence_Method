package com.aimframeworkgrp23;

public class Item {

    private int binId;
    private int itemId;
    private int weight;

    public Item(int weight) {
        this.weight = weight;
    }

    public int getBinId() {
        return binId;
    }

    public void setBinId(int binId) {
        this.binId = binId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getWeight() {
        return weight;
    }
}