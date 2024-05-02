package com.aim.coursework;
import java.util.ArrayList;
import com.aimframeworkgrp23.*;

public class ModifiedBin {
    ArrayList<Item> freeItems;
    Bin modifiedBin;

    public void setFreeItems(ArrayList<Item> freeItems){
        this.freeItems=freeItems;
    }

    public void setModifiedBin(Bin modifiedBin){
        this.modifiedBin=modifiedBin;
    }

    public ArrayList<Item> getFreeItems(){
        return freeItems;
    }

    public Bin getModifiedBin(){
        return modifiedBin;
    }
}