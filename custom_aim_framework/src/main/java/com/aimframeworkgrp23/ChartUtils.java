package com.aimframeworkgrp23;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;
import java.text.NumberFormat;

public class ChartUtils {

    public static void buildAndDisplayXYCharts(FinalSolution finalSolution) {
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
        displayXYChart(title + " - Objective Value", objectiveDataset, true);
        displayXYChart(title + " - Bin Count", binCountDataset, false);
    }

    private static void displayXYChart(String title, XYSeriesCollection dataset, boolean isObjectiveChart) {
        JFreeChart xyChart = ChartFactory.createXYLineChart(
            title,
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

        showChartInFrame(title, xyChart);
    }

    private static void showChartInFrame(String title, JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}