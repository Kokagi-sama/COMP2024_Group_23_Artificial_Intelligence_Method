package com.aimframeworkgrp23;

import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
//import javax.swing.JFrame;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChartUtilities {

    public static void buildAndSaveXYCharts(int iteration, String algorithmName, FinalSolution finalSolution, String output_directory, int width, int height) {
        XYSeriesCollection objectiveDataset = new XYSeriesCollection();
        XYSeriesCollection binCountDataset = new XYSeriesCollection();

        XYSeries objectiveSeries = new XYSeries("Objective Value");
        XYSeries binCountSeries = new XYSeries("Bin Count");

        for (Generation generation : finalSolution.getGenerations()) {
            int generationId = generation.getGenerationId();
            double objectiveValue = generation.getBestSolution().getObjectiveFunctionValue();
            int binCount = generation.getBestSolution().getBinCount();

            objectiveSeries.add(generationId, objectiveValue);
            binCountSeries.add(generationId, binCount);
        }

        objectiveDataset.addSeries(objectiveSeries);
        binCountDataset.addSeries(binCountSeries);

        String problemName = finalSolution.getBestSolution().getProblemName();
        saveXYChart(iteration, algorithmName, problemName, output_directory, " - Objective Value", objectiveDataset, true, width, height);
        saveXYChart(iteration, algorithmName, problemName, output_directory, " - Bin Count", binCountDataset, false, width, height);
    }

    private static void saveXYChart(int iteration, String algorithmName, String problemName, String output_directory, String chartType, XYSeriesCollection dataset, 
        boolean isObjectiveChart, int width, int height) {
        JFreeChart xyChart = ChartFactory.createXYLineChart(
            "Iteration_" + iteration + "-" + algorithmName + "-" + problemName + chartType,
            "Generation ID",
            isObjectiveChart ? "Objective Value" : "Bin Count",
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        XYPlot plot = xyChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        
        // Adjusting the y-axis to show non-scientific numbers
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(NumberFormat.getNumberInstance());

        plot.setRenderer(renderer);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (!isObjectiveChart) {
            rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        }
        rangeAxis.setAutoRangeIncludesZero(false);

        String saveFilePath = output_directory
        + problemName.trim().replace(" ", "_") + "/" 
        + algorithmName.trim().replace(" ", "_") + "/Iteration_" + iteration 
        + "/charts/Iteration_" + iteration + (isObjectiveChart ?  "-Objective_Value" : "-Bin_Count") + ".png";

        // Save Chart
        saveChartAsPNG(xyChart, saveFilePath, width, height);

        // Display Chart
        //showChartInFrame(iteration, algorithmName, problemName, chartType, xyChart, width, height, false);
    }

    public static void buildAndSaveBoxPlot(ArrayList<ArrayList<Solution>> bestFinalSolutionsPerAlgorithmIteration, String algorithm_name, String output_directory, int width, int height) {

        // Using LinkedHashMap to preserve the order of problem names and associate each with a list of values
        LinkedHashMap<String, List<Double>> problemObjectiveValuesMap = new LinkedHashMap<>();
        LinkedHashMap<String, List<Integer>> problemBinValuesMap = new LinkedHashMap<>();

        // Collect values for each problem across all iterations
        for (List<Solution> solutions : bestFinalSolutionsPerAlgorithmIteration) {
            for (Solution solution : solutions) {
                String problemName = solution.getProblemName();

                // Objective function value map per problem across all iterations
                problemObjectiveValuesMap.putIfAbsent(problemName, new ArrayList<>());
                problemObjectiveValuesMap.get(problemName).add(solution.getObjectiveFunctionValue());

                // Bin count value map per problem per problem across all iterations
                problemBinValuesMap.putIfAbsent(problemName, new ArrayList<>());
                problemBinValuesMap.get(problemName).add(solution.getBinCount());
            }
        }
     
        // Generate and save a box plot for objective values of each problem
        for (Map.Entry<String, List<Double>> entry : problemObjectiveValuesMap.entrySet()) {
            DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
            dataset.add(entry.getValue(), entry.getKey(), "");

            JFreeChart boxplot = ChartFactory.createBoxAndWhiskerChart(
                "Objective Function Values for " + entry.getKey(),
                "Algorithm Iterations",
                "Objective Function Values",
                dataset,
                true
            );

            // Get the plot from the chart and adjust the renderer
            CategoryPlot plot = (CategoryPlot) boxplot.getPlot();
            BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();

            renderer.setFillBox(false); // Controls whether the box is filled or just an outline.
            renderer.setMeanVisible(true);
            renderer.setUseOutlinePaintForWhiskers(true);
            renderer.setMaximumBarWidth(0.15); // Set the maximum bar width to control box width.

            // Setting the outliers to be visible
            renderer.setMaxOutlierVisible(true);
            renderer.setMinOutlierVisible(true);

            plot.setRenderer(renderer); // Set the renderer on the correct plot type.

            String saveFilePath = output_directory + entry.getKey().trim().replace(" ", "_") + "/" + algorithm_name + "/boxplot/" + "ObjectiveFunctionValuesBoxplot.png";
            saveChartAsPNG(boxplot, saveFilePath, width, height);

            //showChartInFrame(-1, algorithm_name, entry.getKey().trim().replace(" ", "_"), "-Objective Function Values Across Problems", boxplot, width, height, true);
        }

        // Generate and save a box plot for bin count of each problem
        for (Map.Entry<String, List<Integer>> entry : problemBinValuesMap.entrySet()) {
            DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
            dataset.add(entry.getValue(), entry.getKey(), "");

            JFreeChart boxplot = ChartFactory.createBoxAndWhiskerChart(
                "Bin Count Values for " + entry.getKey(),
                "Algorithm Iterations",
                "Bin Count Values",
                dataset,
                true
            );

            // Get the plot from the chart and adjust the renderer
            CategoryPlot plot = (CategoryPlot) boxplot.getPlot();
            BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();

            renderer.setFillBox(true); // Controls whether the box is filled or just an outline.
            renderer.setMeanVisible(true); // Hide the mean marker if desired.
            renderer.setUseOutlinePaintForWhiskers(true);
            renderer.setMaximumBarWidth(0.15); // Set the maximum bar width to control box width.

            // Setting the outliers to be visible
            renderer.setMaxOutlierVisible(true);
            renderer.setMinOutlierVisible(true);

            // Adjusting the y-axis to better fit the range of your data
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            int binCount = Collections.min(entry.getValue());
            yAxis.setRange(binCount - 2, binCount + 2); // Replace minBinCount and maxBinCount with your data's range
            yAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance()); // Display axis labels as integers


            plot.setRenderer(renderer); // Set the renderer on the correct plot type.

            String saveFilePath = output_directory + entry.getKey().trim().replace(" ", "_") + "/" + algorithm_name + "/boxplot/" + "BinCountValuesBoxplot.png";
            saveChartAsPNG(boxplot, saveFilePath, width, height);

            //showChartInFrame(-1, algorithm_name, entry.getKey().trim().replace(" ", "_"), "-Bin Count Values Across Problems", boxplot, width, height, true);
        }
    }

    private static void saveChartAsPNG(JFreeChart chart, String saveFilePath, int width, int height) {
        try {
            File outputFile = new File(saveFilePath);
            outputFile.getParentFile().mkdirs(); // This will create the directory path if it doesn't exist
            ChartUtils.saveChartAsPNG(outputFile, chart, width, height);
        } catch (IOException e) {
            System.err.println("Problem occurred creating chart.");
            e.printStackTrace();
        }
    }

    // private static void showChartInFrame(int iteration, String algorithmName, String problemName, String chartType, JFreeChart chart, int width, int height, boolean boxplot) {
    //     ChartPanel chartPanel = new ChartPanel(chart);
    //     chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
    //     JFrame frame = new JFrame(boxplot ? problemName + " Boxplot" : "Iteration_" + iteration + "-" + algorithmName);
    //     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    //     frame.setContentPane(chartPanel);
    //     frame.pack();
    //     frame.setLocationRelativeTo(null);
    //     frame.setVisible(true);
    // }
}