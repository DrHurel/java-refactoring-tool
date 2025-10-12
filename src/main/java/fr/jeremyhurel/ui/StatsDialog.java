package fr.jeremyhurel.ui;

import fr.jeremyhurel.utils.Dialog;
import fr.jeremyhurel.processors.StatisticsProcessor;
import fr.jeremyhurel.models.ProjectStatistics;
import fr.jeremyhurel.utils.StatisticsExporter;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import java.io.IOException;

public class StatsDialog implements Dialog {

    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private String projectPath;
    private int methodThreshold = 10;

    public StatsDialog(MultiWindowTextGUI gui) {
        this.gui = gui;
    }

    public void show() {
        askForProjectPath();
    }

    public void close() {
        if (window != null) {
            window.close();
        }
    }

    public void confirm() {
        generateStatistics();
    }

    private void askForProjectPath() {
        String inputPath = new TextInputDialogBuilder()
                .setTitle("Statistics - Project Path")
                .setDescription("Enter the path to your Java project:")
                .setInitialContent("./src/main/java")
                .build()
                .showDialog(gui);

        if (inputPath != null && !inputPath.trim().isEmpty()) {
            this.projectPath = inputPath.trim();
            showStatsOptions();
        }
    }

    private void showStatsOptions() {
        new ActionListDialogBuilder()
                .setTitle("Statistics - Analysis Type")
                .setDescription("Select the type of statistics to generate:")
                .addAction("Full Analysis", this::generateStatistics)
                .addAction("Custom Threshold", this::askForMethodThreshold)
                .addAction("Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void askForMethodThreshold() {
        String thresholdStr = new TextInputDialogBuilder()
                .setTitle("Method Threshold")
                .setDescription("Enter the minimum number of methods for class filtering:")
                .setInitialContent(String.valueOf(methodThreshold))
                .build()
                .showDialog(gui);

        if (thresholdStr != null && !thresholdStr.trim().isEmpty()) {
            try {
                this.methodThreshold = Integer.parseInt(thresholdStr.trim());
                generateStatistics();
            } catch (NumberFormatException e) {
                new MessageDialogBuilder()
                        .setTitle("Invalid Input")
                        .setText("Please enter a valid number for the method threshold.")
                        .build()
                        .showDialog(gui);
            }
        }
    }

    private void generateStatistics() {
        try {
            // Show progress dialog
            new MessageDialogBuilder()
                    .setTitle("Generating Statistics")
                    .setText("Analyzing project... Please wait.")
                    .build()
                    .showDialog(gui);

            // Create processor and generate statistics
            StatisticsProcessor processor = new StatisticsProcessor(projectPath);
            ProjectStatistics stats = processor.generateStatistics(methodThreshold);

            // Display statistics in terminal UI
            showStatisticsInUI(stats);

            // Ask for export
            askForExportOptions(stats);

        } catch (Exception e) {
            new MessageDialogBuilder()
                    .setTitle("Error")
                    .setText("Failed to generate statistics:\n" + e.getMessage())
                    .build()
                    .showDialog(gui);
        }
    }

    private void showStatisticsInUI(ProjectStatistics stats) {
        // Create a window to display statistics
        BasicWindow statsWindow = new BasicWindow("Project Statistics");
        statsWindow.setHints(java.util.Arrays.asList(Window.Hint.CENTERED));

        Panel mainPanel = new Panel(new GridLayout(1));

        // Create scrollable text area for statistics
        String statisticsText = StatisticsExporter.formatStatisticsForDisplay(stats);

        // Create a multi-line label to display the statistics
        Panel textPanel = new Panel(new GridLayout(1));
        String[] lines = statisticsText.split("\n");
        for (String line : lines) {
            textPanel.addComponent(new Label(line));
        }

        // Wrap in a scrollable panel
        mainPanel.addComponent(textPanel.withBorder(Borders.singleLine("Statistics")));

        // Add close button
        Button closeButton = new Button("Close", statsWindow::close);
        mainPanel.addComponent(closeButton);

        statsWindow.setComponent(mainPanel);
        gui.addWindow(statsWindow);
    }

    private void askForExportOptions(ProjectStatistics stats) {
        new ActionListDialogBuilder()
                .setTitle("Export Statistics")
                .setDescription("Do you want to export the statistics to a file?")
                .addAction("Export to TXT file", () -> exportStatistics(stats))
                .addAction("Skip Export", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void exportStatistics(ProjectStatistics stats) {
        String filePath = new TextInputDialogBuilder()
                .setTitle("Export Statistics - Save Path")
                .setDescription("Enter the file path to save the statistics:")
                .setInitialContent("./project-statistics.txt")
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                StatisticsExporter.exportToText(stats, filePath.trim());

                new MessageDialogBuilder()
                        .setTitle("Export Successful")
                        .setText("Statistics exported to: " + filePath.trim())
                        .build()
                        .showDialog(gui);

            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Export Error")
                        .setText("Failed to export statistics:\n" + e.getMessage())
                        .build()
                        .showDialog(gui);
            }
        }
    }
}
