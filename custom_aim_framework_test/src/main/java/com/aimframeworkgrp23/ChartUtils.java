package com.aimframeworkgrp23;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JFrame;

public class ChartUtils {

    public static void displayLineChart(String title, DefaultCategoryDataset dataset) {
        JFreeChart lineChart = createLineChart(title, dataset);
        showChartInFrame(title, lineChart);
    }

    private static DefaultCategoryDataset createDataset() {
        // This could be modified to accept data parameters
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "Series1", "Category1");
        dataset.addValue(4.0, "Series1", "Category2");
        dataset.addValue(3.0, "Series1", "Category3");
        dataset.addValue(5.0, "Series1", "Category4");
        return dataset;
    }

    private static JFreeChart createLineChart(String title, DefaultCategoryDataset dataset) {
        return ChartFactory.createLineChart(
            title,
            "Category",
            "Value",
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
    }

    private static void showChartInFrame(String title, JFreeChart chart) {
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setLocationRelativeTo(null); // Center the frame
        frame.setVisible(true);
    }

    // External applications can use this method to create and display a line chart
    public static void buildAndDisplayChart(String title) {
        DefaultCategoryDataset dataset = createDataset(); // Or pass the dataset as a parameter
        displayLineChart(title, dataset);
    }
}