package fr.jeremyhurel.ui;

import java.io.IOException;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.processors.CouplingGraphProcessor;
import fr.jeremyhurel.utils.ClusterTreeExporter;
import fr.jeremyhurel.utils.CouplingGraphExporter;
import fr.jeremyhurel.utils.Dialog;

public class CouplingGraphDialog implements Dialog {

    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private String projectPath;
    private String rootPackage;

    public CouplingGraphDialog(MultiWindowTextGUI gui) {
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
        generateCouplingGraph();
    }

    private void askForProjectPath() {
        String inputPath = new TextInputDialogBuilder()
                .setTitle("Coupling Graph - Project Path")
                .setDescription("Enter the path to your Java project:")
                .setInitialContent("./src/main/java")
                .build()
                .showDialog(gui);

        if (inputPath != null && !inputPath.trim().isEmpty()) {
            this.projectPath = inputPath.trim();
            askForRootPackageOrNone();
        }
    }

    private void askForRootPackageOrNone() {
        new ActionListDialogBuilder()
                .setTitle("Coupling Graph - Root Package")
                .setDescription("Select a root package or choose 'All Packages':")
                .addAction("All Packages", this::generateCouplingGraph)
                .addAction("Specify Root Package", () -> {
                    String packageName = new TextInputDialogBuilder()
                            .setTitle("Root Package")
                            .setDescription("Enter the root package name (e.g., com.example):")
                            .build()
                            .showDialog(gui);

                    if (packageName != null && !packageName.trim().isEmpty()) {
                        this.rootPackage = packageName.trim();
                        generateCouplingGraph();
                    }
                })
                .addAction("Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void generateCouplingGraph() {
        try {

            new MessageDialogBuilder()
                    .setTitle("Generating Coupling Graph")
                    .setText("Analyzing project coupling... Please wait.")
                    .build()
                    .showDialog(gui);

            CouplingGraphProcessor processor;
            if (rootPackage != null) {
                processor = new CouplingGraphProcessor(projectPath, rootPackage);
            } else {
                processor = new CouplingGraphProcessor(projectPath);
            }

            CouplingGraph couplingGraph = processor.generateCouplingGraph();

            if (couplingGraph.isEmpty()) {
                new MessageDialogBuilder()
                        .setTitle("Coupling Graph Generation")
                        .setText("No coupling relationships found in the specified scope.")
                        .build()
                        .showDialog(gui);
                return;
            }

            new MessageDialogBuilder()
                    .setTitle("Coupling Graph Generated")
                    .setText("Coupling graph generated successfully!\n\n" +
                            "Classes found: " + couplingGraph.getNodeCount() + "\n" +
                            "Coupling relationships: " + couplingGraph.getCouplingCount() + "\n" +
                            "Total method calls: " + couplingGraph.getTotalMethodCalls() + "\n\n" +
                            "Formula: Couplage(A,B) = Method calls A→B / Total calls")
                    .build()
                    .showDialog(gui);

            askForExportOptions(couplingGraph, processor);

        } catch (Exception e) {
            new MessageDialogBuilder()
                    .setTitle("Error")
                    .setText("Failed to generate coupling graph:\n" + e.getMessage())
                    .build()
                    .showDialog(gui);
        }
    }

    private void askForExportOptions(CouplingGraph couplingGraph, CouplingGraphProcessor processor) {
        new ActionListDialogBuilder()
                .setTitle("Export Coupling Graph")
                .setDescription("Choose export format:")
                .addAction("JSON Format", () -> exportCouplingGraph(couplingGraph, "json"))
                .addAction("DOT Format (Graphviz)", () -> exportCouplingGraph(couplingGraph, "dot"))
                .addAction("Both Formats", () -> {
                    exportCouplingGraph(couplingGraph, "json");
                    exportCouplingGraph(couplingGraph, "dot");
                })
                .addAction("Generate Cluster Tree", () -> generateAndExportClusterTree(couplingGraph, processor))
                .addAction("Skip Export", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void exportCouplingGraph(CouplingGraph couplingGraph, String format) {
        String defaultFileName = "couplinggraph." + (format.equals("json") ? "json" : "dot");

        String filePath = new TextInputDialogBuilder()
                .setTitle("Export " + format.toUpperCase() + " - Save Path")
                .setDescription("Enter the file path to save the coupling graph:")
                .setInitialContent("./" + defaultFileName)
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                if (format.equals("json")) {
                    CouplingGraphExporter.exportToJson(couplingGraph, filePath.trim());
                } else {
                    CouplingGraphExporter.exportToDot(couplingGraph, filePath.trim());
                }

                new MessageDialogBuilder()
                        .setTitle("Export Successful")
                        .setText("Coupling graph exported to: " + filePath.trim())
                        .build()
                        .showDialog(gui);

            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Export Error")
                        .setText("Failed to export coupling graph:\n" + e.getMessage())
                        .build()
                        .showDialog(gui);
            }
        }
    }

    private void generateAndExportClusterTree(CouplingGraph couplingGraph, CouplingGraphProcessor processor) {
        try {

            new MessageDialogBuilder()
                    .setTitle("Generating Cluster Tree")
                    .setText("Building hierarchical cluster tree... Please wait.")
                    .build()
                    .showDialog(gui);

            ClusterTree clusterTree = processor.generateClusterTree(couplingGraph);

            new MessageDialogBuilder()
                    .setTitle("Cluster Tree Generated")
                    .setText("Hierarchical cluster tree generated successfully!\n\n" +
                            "Tree depth: " + clusterTree.getDepth() + "\n" +
                            "Merge steps: " + clusterTree.getMergeHistory().size() + "\n\n" +
                            "Algorithm: Agglomerative clustering with average linkage")
                    .build()
                    .showDialog(gui);

            askForClusterTreeExportOptions(clusterTree);

        } catch (Exception e) {
            new MessageDialogBuilder()
                    .setTitle("Error")
                    .setText("Failed to generate cluster tree:\n" + e.getMessage())
                    .build()
                    .showDialog(gui);
        }
    }

    private void askForClusterTreeExportOptions(ClusterTree clusterTree) {
        new ActionListDialogBuilder()
                .setTitle("Export Cluster Tree")
                .setDescription("Choose export format:")
                .addAction("JSON Format", () -> exportClusterTree(clusterTree, "json"))
                .addAction("DOT Format (Dendrogram)", () -> exportClusterTree(clusterTree, "dot"))
                .addAction("Text Format", () -> exportClusterTree(clusterTree, "text"))
                .addAction("Newick Format", () -> exportClusterTree(clusterTree, "newick"))
                .addAction("All Formats", () -> {
                    exportClusterTree(clusterTree, "json");
                    exportClusterTree(clusterTree, "dot");
                    exportClusterTree(clusterTree, "text");
                    exportClusterTree(clusterTree, "newick");
                })
                .addAction("Skip Export", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void exportClusterTree(ClusterTree clusterTree, String format) {
        String extension = switch (format) {
            case "json" -> "json";
            case "dot" -> "dot";
            case "text" -> "txt";
            case "newick" -> "nwk";
            default -> "txt";
        };

        String defaultFileName = "clustertree." + extension;

        String filePath = new TextInputDialogBuilder()
                .setTitle("Export " + format.toUpperCase() + " - Save Path")
                .setDescription("Enter the file path to save the cluster tree:")
                .setInitialContent("./" + defaultFileName)
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                switch (format) {
                    case "json" -> ClusterTreeExporter.exportToJson(clusterTree, filePath.trim());
                    case "dot" -> ClusterTreeExporter.exportToDot(clusterTree, filePath.trim());
                    case "text" -> ClusterTreeExporter.exportToText(clusterTree, filePath.trim());
                    case "newick" -> ClusterTreeExporter.exportToNewick(clusterTree, filePath.trim());
                }

                new MessageDialogBuilder()
                        .setTitle("Export Successful")
                        .setText("Cluster tree exported to: " + filePath.trim())
                        .build()
                        .showDialog(gui);

            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Export Error")
                        .setText("Failed to export cluster tree:\n" + e.getMessage())
                        .build()
                        .showDialog(gui);
            }
        }
    }
}
