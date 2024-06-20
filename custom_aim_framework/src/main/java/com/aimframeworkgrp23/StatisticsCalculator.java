package com.aimframeworkgrp23;

import java.util.ArrayList;

public class StatisticsCalculator {

    /**
     * Calculates the mean and standard deviation of a list of doubles.
     * @param values The list of doubles.
     * @return An array where the first element is the mean and the second element is the standard deviation.
     */
    public static double[] calculateMeanAndStdDev(ArrayList<Double> values) {
        if (values == null || values.size() == 0) {
            throw new IllegalArgumentException("List must not be null or empty");
        }

        // Calculate mean
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        double mean = sum / values.size();

        // Calculate standard deviation
        double sumOfSquares = 0.0;
        for (double value : values) {
            sumOfSquares += Math.pow(value - mean, 2);
        }
        double standardDeviation = Math.sqrt(sumOfSquares / values.size());

        return new double[]{mean, standardDeviation};
    }
}