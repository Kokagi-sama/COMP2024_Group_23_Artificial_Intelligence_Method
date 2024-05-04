package com.aimframeworkgrp23;

import org.jfree.chart.ChartFactory;
//import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
//import javax.swing.JFrame;
import java.awt.Color;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    public static void buildAndSaveBoxPlot(ArrayList<Solution> bestFinalSolutionsPerProblemPerAlgorithmIteration, String algorithm_name, String output_directory, int width, int height) {

        // Using LinkedHashMap to preserve the order of problem names and associate each with a list of values
        ArrayList<Double> problemObjectiveValues = new ArrayList<Double>();
        ArrayList<Integer> problemBinCountValues = new ArrayList<Integer>();
        String problem_name = bestFinalSolutionsPerProblemPerAlgorithmIteration.getFirst().getProblemName();

        // Collect values for each problem across all iterations
        
        for (Solution solution : bestFinalSolutionsPerProblemPerAlgorithmIteration) {

            // Objective function value map for a problem across all iterations
            problemObjectiveValues.add(solution.getObjectiveFunctionValue());

            // Bin count value map for a problem across all iterations
            problemBinCountValues.add(solution.getBinCount());
           
        }
        
        buildSaveBoxPlot(problemObjectiveValues, "Objective Function Values", problem_name, algorithm_name, output_directory, width, height, false);
        buildSaveBoxPlot(problemBinCountValues, "Bin Count Values", problem_name, algorithm_name, output_directory, width, height, true);
    }

    private static <T extends Number> void buildSaveBoxPlot(ArrayList<T> problem_values, String title, String problem_name, String algorithm_name, String output_directory, 
        int width, int height, boolean adjustNumberAxis) {

        // Check if the list contains Double instances

        double[] mean_and_std_dev = null;

        if (!problem_values.isEmpty() && problem_values.get(0) instanceof Double) {
            @SuppressWarnings("unchecked") // Safe cast because of the instance check
            ArrayList<Double> doubleValues = (ArrayList<Double>) (ArrayList<?>) problem_values;
            
            // Calculate Mean and Standard Deviation
            mean_and_std_dev = StatisticsCalculator.calculateMeanAndStdDev(doubleValues);
        }

        // Generate and save a box plot for given values of each problem across all iterations
        DefaultBoxAndWhiskerCategoryDataset datasetObjectiveValues = new DefaultBoxAndWhiskerCategoryDataset();
        String label = (mean_and_std_dev != null) ? "x̄= " + mean_and_std_dev[0] + "    σ= " + mean_and_std_dev[1] : "";
        datasetObjectiveValues.add(problem_values, problem_name, label);

        JFreeChart boxPlot = ChartFactory.createBoxAndWhiskerChart(
            title + " for " + problem_name,
            "Algorithm Iterations",
            title,
            datasetObjectiveValues,
            true
        );

        boxPlot.removeLegend();

        // Get the plot from the chart and adjust the renderer
        CategoryPlot plot = (CategoryPlot) boxPlot.getPlot();
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();

        renderer.setFillBox(false); // Controls whether the box is filled or just an outline.
        renderer.setMeanVisible(true);
        renderer.setUseOutlinePaintForWhiskers(true);
        renderer.setMaximumBarWidth(0.15); // Set the maximum bar width to control box width.

        // Setting the outliers to be visible
        renderer.setMaxOutlierVisible(true);
        renderer.setMinOutlierVisible(true);

        if (adjustNumberAxis) {
            // Adjusting the y-axis to better fit the range
            NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
            Comparator<Number> numberComparator = Comparator.comparingInt(Number::intValue);
            Number minValue = Collections.min(problem_values, numberComparator);
            Number maxValue = Collections.max(problem_values, numberComparator);
            yAxis.setRange(minValue.intValue() - 4, maxValue.intValue() + 4);
            yAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance()); // Display axis labels as integers
        }

        plot.setRenderer(renderer); // Set the renderer on the correct plot type.

        String saveFilePath = output_directory + problem_name + "/" + algorithm_name + "/boxplot/" + title.trim().replace(" ", "") + "Boxplot.png";
        saveChartAsPNG(boxPlot, saveFilePath, width, height);

        //showChartInFrame(-1, algorithm_name, problem_name, "-" + title + " Across Problems", boxplot, width, height, true);
    }

    public static void buildAndSaveTimeBarChart(LinkedHashMap<String, ArrayList<LinkedHashMap<String, Long>>> final_times_map, String output_directory, int width, int height) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Iterate over each problem and their respective list of algorithm times
        for (Map.Entry<String, ArrayList<LinkedHashMap<String, Long>>> problemEntry : final_times_map.entrySet()) {
            String problemName = problemEntry.getKey();
            List<LinkedHashMap<String, Long>> algorithmList = problemEntry.getValue();

            for (LinkedHashMap<String, Long> algorithmTimes : algorithmList) {
                for (Map.Entry<String, Long> algorithmEntry : algorithmTimes.entrySet()) {
                    String algorithmName = algorithmEntry.getKey();
                    Long time = algorithmEntry.getValue();
                    dataset.addValue(time, algorithmName, problemName);
                }
            }
        }

        // Create the bar chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Algorithm Performance Comparison",
                "Problem Name",
                "Time (ms)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer barRenderer = (BarRenderer) plot.getRenderer();

        Color[] colors = new Color[]{Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.ORANGE, Color.CYAN}; // Define more colors as needed
        for (int i = 0; i < dataset.getRowCount(); i++) {
            barRenderer.setSeriesPaint(i, colors[i % colors.length]);
        }

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // Save the chart as a PNG
        String saveFilePath = output_directory + "time_comparision_chart.png";
        saveChartAsPNG(chart, saveFilePath, width, height);
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