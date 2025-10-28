package fr.jeremyhurel.ui;

import fr.jeremyhurel.utils.Dialog;
import fr.jeremyhurel.processors.CallGraphProcessor;
import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.utils.CallGraphExporter;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import java.io.IOException;

public class CalleeGraphDialog implements Dialog {

    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private String projectPath;
    private String rootClassName;
    private String rootMethodName;

    public CalleeGraphDialog(MultiWindowTextGUI gui) {
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
        generateCallGraph();
    }

    private void askForProjectPath() {
        String inputPath = new TextInputDialogBuilder()
                .setTitle("Called Graph - Project Path")
                .setDescription("Enter the path to your Java project:")
                .setInitialContent("./src/main/java")
                .build()
                .showDialog(gui);

        if (inputPath != null && !inputPath.trim().isEmpty()) {
            this.projectPath = inputPath.trim();
            askForRootClassOrNone();
        }
    }

    private void askForRootClassOrNone() {
        new ActionListDialogBuilder()
                .setTitle("Called Graph - Root Class")
                .setDescription("Select a root class or choose 'All Classes':")
                .addAction("All Classes", this::askForRootCalleeMethodOrNone)
                .addAction("Specify Root Class", () -> {
                    String className = new TextInputDialogBuilder()
                            .setTitle("Root Class")
                            .setDescription("Enter the fully qualified class name:")
                            .build()
                            .showDialog(gui);

                    if (className != null && !className.trim().isEmpty()) {
                        this.rootClassName = className.trim();
                        askForRootCalleeMethodOrNone();
                    }
                })
                .addAction("Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void askForRootCalleeMethodOrNone() {
        new ActionListDialogBuilder()
                .setTitle("Called Graph - Root Method")
                .setDescription("Select analysis scope:")
                .addAction("All Methods", this::generateCallGraph)
                .addAction("Specify Method", () -> {
                    String methodName = new TextInputDialogBuilder()
                            .setTitle("Root Method")
                            .setDescription("Enter the method name:")
                            .build()
                            .showDialog(gui);

                    if (methodName != null && !methodName.trim().isEmpty()) {
                        this.rootMethodName = methodName.trim();
                        generateCallGraph();
                    }
                })
                .addAction("Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void generateCallGraph() {
        try {

            new MessageDialogBuilder()
                    .setTitle("Generating Call Graph")
                    .setText("Analyzing project... Please wait.")
                    .build()
                    .showDialog(gui);

            CallGraphProcessor processor;
            if (rootClassName != null && rootMethodName != null) {
                processor = new CallGraphProcessor(projectPath, rootClassName, rootMethodName);
            } else {
                processor = new CallGraphProcessor(projectPath);
            }

            CallGraph callGraph = processor.generateCallGraph();

            if (callGraph.isEmpty()) {
                new MessageDialogBuilder()
                        .setTitle("Call Graph Generation")
                        .setText("No method calls found in the specified scope.")
                        .build()
                        .showDialog(gui);
                return;
            }

            new MessageDialogBuilder()
                    .setTitle("Call Graph Generated")
                    .setText("Call graph generated successfully!\n" +
                            "Nodes found: " + callGraph.getNodeCount() + "\n" +
                            "Root node: "
                            + (callGraph.getRootNode() != null ? callGraph.getRootNode().getFullName() : "None"))
                    .build()
                    .showDialog(gui);

            askForExportOptions(callGraph);

        } catch (Exception e) {
            new MessageDialogBuilder()
                    .setTitle("Error")
                    .setText("Failed to generate call graph:\n" + e.getMessage())
                    .build()
                    .showDialog(gui);
        }
    }

    private void askForExportOptions(CallGraph callGraph) {
        new ActionListDialogBuilder()
                .setTitle("Export Call Graph")
                .setDescription("Choose export format:")
                .addAction("JSON Format", () -> exportCallGraph(callGraph, "json"))
                .addAction("DOT Format (Graphviz)", () -> exportCallGraph(callGraph, "dot"))
                .addAction("Both Formats", () -> {
                    exportCallGraph(callGraph, "json");
                    exportCallGraph(callGraph, "dot");
                })
                .addAction("Skip Export", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void exportCallGraph(CallGraph callGraph, String format) {
        String defaultFileName = "callgraph." + (format.equals("json") ? "json" : "dot");

        String filePath = new TextInputDialogBuilder()
                .setTitle("Export " + format.toUpperCase() + " - Save Path")
                .setDescription("Enter the file path to save the call graph:")
                .setInitialContent("./" + defaultFileName)
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                if (format.equals("json")) {
                    CallGraphExporter.exportToJson(callGraph, filePath.trim());
                } else {
                    CallGraphExporter.exportToDot(callGraph, filePath.trim());
                }

                new MessageDialogBuilder()
                        .setTitle("Export Successful")
                        .setText("Call graph exported to: " + filePath.trim())
                        .build()
                        .showDialog(gui);

            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Export Error")
                        .setText("Failed to export call graph:\n" + e.getMessage())
                        .build()
                        .showDialog(gui);
            }
        }
    }
}
