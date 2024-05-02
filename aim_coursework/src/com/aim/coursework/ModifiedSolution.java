package com.aim.coursework;
import java.util.ArrayList;
import com.aimframeworkgrp23.*;

public class ModifiedSolution {
    ArrayList<Item> freeItems;
    Solution modifiedSolution;

    public void setFreeItems(ArrayList<Item> freeItems){
        this.freeItems=freeItems;
    }

    public void setModifiedSolution(Solution modifiedSolution){
        this.modifiedSolution=modifiedSolution;
    }

    public ArrayList<Item> getFreeItems(){
        return freeItems;
    }

    public Solution getModifiedSolution(){
        return modifiedSolution;
    }
}