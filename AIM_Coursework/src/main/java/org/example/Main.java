package org.example;

import java.io.IOException;
import java.util.List;

import com.aimframeworkgrp23.*;

public class Main {
    public static void main(String[] args) {
        try {
            List<BinPackingProblem> problems = TextFileReader.readProblems("./src/main/resources/BPP.txt");
//            for (BinPackingProblem problem : problems) {
//                System.out.println("Problem Name: " + problem.getProblemName());
//                System.out.println("Unique Weight Count: " + problem.getUniqueWeightCount());
//                System.out.println("Bin Capacity: " + problem.getBinCapacity());
//                for (Item item : problem.getItems()) {
//                    System.out.println("Weight: " + item.getWeight());
//                }
//            }

            List<Bin> bins = FirstFitAlgorithm.firstFit(problems);
            FirstFitAlgorithm.printResults(problems, bins);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}