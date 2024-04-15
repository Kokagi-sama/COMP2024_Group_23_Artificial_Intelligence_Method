package com.aimframeworkgrp23;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

public class ChartUtilities {

    public static void buildAndDisplayXYCharts(String algorithmName, FinalSolution finalSolution, int width, int height) {
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

        String title = finalSolution.getBestSolution().getProblemName();
        displayXYChart(algorithmName, title, " - Objective Value", objectiveDataset, true, width, height);
        displayXYChart(algorithmName, title, " - Bin Count", binCountDataset, false, width, height);
    }

    private static void displayXYChart(String algorithmName, String title, String chartType, XYSeriesCollection dataset, boolean isObjectiveChart, int width, int height) {
        JFreeChart xyChart = ChartFactory.createXYLineChart(
            title + chartType,
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
        plot.setRenderer(renderer);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (!isObjectiveChart) {
            rangeAxis.setNumberFormatOverride(NumberFormat.getIntegerInstance());
        }
        rangeAxis.setAutoRangeIncludesZero(false);

        String saveFilePath = "./src/resources/" 
        + title.trim().replace(" ", "_") + "/" 
        + algorithmName.trim().replace(" ", "_") + "/charts/" 
        + (isObjectiveChart ? "Objective_Value" : "Bin_Count") + ".png";
        saveChartAsPNG(xyChart, saveFilePath, width, height);

        showChartInFrame(title, xyChart, width, height);
    }

    private static void showChartInFrame(String title, JFreeChart chart, int width, int height) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
}