package com.aim.coursework.AFDO;

public class V {
    private int item_id;
    private int current_bin_id;
    private int new_random_bin_id;

    public V(int item_id, int current_bin_id, int new_random_bin_id) {
        this.item_id = item_id;
        this.current_bin_id = current_bin_id;
        this.new_random_bin_id = new_random_bin_id;
    }

    public int getItemId() {
        return item_id;
    }
    public void setItemId(int item_id) {
        this.item_id = item_id;
    }

    public int getCurrentBinId() {
        return current_bin_id;
    }

    public void setCurrentBinId(int current_bin_id) {
        this.current_bin_id = current_bin_id;
    }

    public int getNewRandomBinId() {
        return new_random_bin_id;
    }

    public void setNewRandomBinId(int new_random_bin_id) {
        this.new_random_bin_id = new_random_bin_id;
    }
   
}