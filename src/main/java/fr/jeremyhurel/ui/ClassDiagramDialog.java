package fr.jeremyhurel.ui;

import java.io.IOException;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.processors.ClassDiagramProcessor;
import fr.jeremyhurel.utils.ClassDiagramExporter;
import fr.jeremyhurel.utils.Dialog;

public class ClassDiagramDialog implements Dialog {

    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private String projectPath;
    private String rootPackage;

    public ClassDiagramDialog(MultiWindowTextGUI gui) {
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
        generateClassDiagram();
    }

    public void askForProjectPath() {
        String inputPath = new TextInputDialogBuilder()
                .setTitle("Class Diagram - Project Path")
                .setDescription("Enter the path to your Java project:")
                .setInitialContent("./src/main/java")
                .build()
                .showDialog(gui);

        if (inputPath != null && !inputPath.trim().isEmpty()) {
            this.projectPath = inputPath.trim();
            askForRootClassOrNone();
        }
    }

    public void askForRootClassOrNone() {
        new ActionListDialogBuilder()
                .setTitle("Class Diagram - Root Package")
                .setDescription("Select a root package or choose 'All Packages':")
                .addAction("All Packages", this::generateClassDiagram)
                .addAction("Specify Root Package", () -> {
                    String packageName = new TextInputDialogBuilder()
                            .setTitle("Root Package")
                            .setDescription("Enter the root package name (e.g., com.example):")
                            .build()
                            .showDialog(gui);

                    if (packageName != null && !packageName.trim().isEmpty()) {
                        this.rootPackage = packageName.trim();
                        generateClassDiagram();
                    }
                })
                .addAction("Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void generateClassDiagram() {
        try {

            new MessageDialogBuilder()
                    .setTitle("Generating Class Diagram")
                    .setText("Analyzing project structure... Please wait.")
                    .build()
                    .showDialog(gui);

            ClassDiagramProcessor processor;
            if (rootPackage != null) {
                processor = new ClassDiagramProcessor(projectPath, rootPackage);
            } else {
                processor = new ClassDiagramProcessor(projectPath);
            }

            ClassDiagram classDiagram = processor.generateClassDiagram();

            if (classDiagram.isEmpty()) {
                new MessageDialogBuilder()
                        .setTitle("Class Diagram Generation")
                        .setText("No classes found in the specified scope.")
                        .build()
                        .showDialog(gui);
                return;
            }

            new MessageDialogBuilder()
                    .setTitle("Class Diagram Generated")
                    .setText("Class diagram generated successfully!\n" +
                            "Classes found: " + classDiagram.getClassCount() + "\n" +
                            "Packages found: " + classDiagram.getPackageCount() + "\n" +
                            "Relationships: " + classDiagram.getRelationshipCount() + "\n" +
                            "Root package: "
                            + (classDiagram.getRootPackage() != null ? classDiagram.getRootPackage() : "All packages"))
                    .build()
                    .showDialog(gui);

            askForPackageEncapsulation(classDiagram);

        } catch (Exception e) {
            new MessageDialogBuilder()
                    .setTitle("Error")
                    .setText("Failed to generate class diagram:\n" + e.getMessage())
                    .build()
                    .showDialog(gui);
        }
    }

    private void askForPackageEncapsulation(ClassDiagram classDiagram) {
        new ActionListDialogBuilder()
                .setTitle("Package Encapsulation")
                .setDescription("Group classes by package in diagram?")
                .addAction("Yes - Show packages", () -> {
                    classDiagram.setPackageEncapsulation(true);
                    askForExportOptions(classDiagram);
                })
                .addAction("No - Flat structure", () -> {
                    classDiagram.setPackageEncapsulation(false);
                    askForExportOptions(classDiagram);
                })
                .build()
                .showDialog(gui);
    }

    private void askForExportOptions(ClassDiagram classDiagram) {
        new ActionListDialogBuilder()
                .setTitle("Export Class Diagram")
                .setDescription("Choose export format:")
                .addAction("JSON Format", () -> exportClassDiagram(classDiagram, "json"))
                .addAction("PlantUML Format", () -> exportClassDiagram(classDiagram, "plantuml"))
                .addAction("Both Formats", () -> {
                    exportClassDiagram(classDiagram, "json");
                    exportClassDiagram(classDiagram, "plantuml");
                })
                .addAction("Skip Export", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void exportClassDiagram(ClassDiagram classDiagram, String format) {
        String defaultFileName = "classdiagram." + (format.equals("json") ? "json" : "puml");

        String filePath = new TextInputDialogBuilder()
                .setTitle("Export " + format.toUpperCase() + " - Save Path")
                .setDescription("Enter the file path to save the class diagram:")
                .setInitialContent("./" + defaultFileName)
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                if (format.equals("json")) {
                    ClassDiagramExporter.exportToJson(classDiagram, filePath.trim());
                } else {
                    ClassDiagramExporter.exportToPlantUML(classDiagram, filePath.trim());
                }

                new MessageDialogBuilder()
                        .setTitle("Export Successful")
                        .setText("Class diagram exported to: " + filePath.trim())
                        .build()
                        .showDialog(gui);

            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Export Error")
                        .setText("Failed to export class diagram:\n" + e.getMessage())
                        .build()
                        .showDialog(gui);
            }
        }
    }
}
