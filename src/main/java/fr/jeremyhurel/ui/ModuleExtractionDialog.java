package fr.jeremyhurel.ui;

import java.io.IOException;

import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.dialogs.ActionListDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.ModuleGraph;
import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy.Mode;
import fr.jeremyhurel.processors.ClassDiagramProcessor;
import fr.jeremyhurel.processors.CouplingGraphProcessor;
import fr.jeremyhurel.utils.Dialog;
import fr.jeremyhurel.utils.strategies.ClassDiagramWithModulesPlantUMLExportStrategy;

public class ModuleExtractionDialog implements Dialog {

    private MultiWindowTextGUI gui;
    private BasicWindow window;
    private String projectPath;
    private String rootPackage;

    public ModuleExtractionDialog(MultiWindowTextGUI gui) {
        this.gui = gui;
    }

    @Override
    public void show() {
        askForProjectPath();
    }

    @Override
    public void close() {
        if (window != null) {
            window.close();
        }
    }

    @Override
    public void confirm() {
        askForClusteringStrategy();
    }

    private void askForProjectPath() {
        String inputPath = new TextInputDialogBuilder()
                .setTitle("Module Extraction - Step 1/3")
                .setDescription("Enter the path to your Java project:\n(e.g., ./src/main/java or /path/to/project)")
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
                .setTitle("Module Extraction - Step 2/3")
                .setDescription("Select scope for analysis:\n\n* All Packages: Analyze entire project\n* Specific Package: Focus on one package")
                .addAction("[*] All Packages (Recommended)", this::askForClusteringStrategy)
                .addAction("[ ] Specify Root Package", () -> {
                    String packageName = new TextInputDialogBuilder()
                            .setTitle("Package Selection")
                            .setDescription("Enter the root package name:\n(e.g., com.example, org.myproject)")
                            .build()
                            .showDialog(gui);

                    if (packageName != null && !packageName.trim().isEmpty()) {
                        this.rootPackage = packageName.trim();
                        askForClusteringStrategy();
                    }
                })
                .addAction("[X] Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void askForClusteringStrategy() {
        new ActionListDialogBuilder()
                .setTitle("Module Extraction - Step 3/3")
                .setDescription("""
                        Choose your clustering strategy:
                        
                        [A] AUTO: Smart detection using elbow method
                        [F] FIXED: Specify exact number of modules
                        [T] THRESHOLD: Set minimum coupling threshold
                        [C] COMBINED: Mix fixed count with threshold
                        """)
                .addAction("[A] Automatic (Elbow Method)", this::extractModulesAutomatic)
                .addAction("[F] Fixed Number of Modules", this::extractModulesFixedCount)
                .addAction("[T] Coupling Threshold", this::extractModulesCouplingThreshold)
                .addAction("[C] Combined (Fixed + Threshold)", this::extractModulesCombined)
                .addAction("[X] Cancel", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void extractModulesAutomatic() {
        try {
            new MessageDialogBuilder()
                    .setTitle("[AUTO] Automatic Module Extraction")
                    .setText("Analyzing project and detecting optimal modules...\nThis may take a moment.")
                    .build()
                    .showDialog(gui);

            CouplingGraph couplingGraph = generateCouplingGraph();
            if (couplingGraph == null) return;

            ClusterTree clusterTree = new ClusterTree();
            clusterTree.buildFromCouplingGraph(couplingGraph);

            ModuleGraph moduleGraph = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.AUTO),
                couplingGraph
            );

            displayResults(moduleGraph, "[AUTO] Automatic (Elbow Method)");
            askForExportOptions(moduleGraph);

        } catch (Exception e) {
            showError("Module extraction failed", e);
        }
    }

    private void extractModulesFixedCount() {
        String countStr = new TextInputDialogBuilder()
                .setTitle("[FIXED] Fixed Module Count")
                .setDescription("Enter the target number of modules:\n(Recommended: 3-10 for small projects, 10-20 for large)")
                .setInitialContent("5")
                .build()
                .showDialog(gui);

        if (countStr != null && !countStr.trim().isEmpty()) {
            try {
                int targetCount = Integer.parseInt(countStr.trim());

                if (targetCount < 1) {
                    new MessageDialogBuilder()
                            .setTitle("[ERROR] Invalid Input")
                            .setText("Module count must be at least 1.\nPlease try again with a valid number.")
                            .build()
                            .showDialog(gui);
                    return;
                }

                new MessageDialogBuilder()
                        .setTitle("[FIXED] Fixed Count Extraction")
                        .setText("Creating exactly " + targetCount + " modules...\nPlease wait.")
                        .build()
                        .showDialog(gui);

                CouplingGraph couplingGraph = generateCouplingGraph();
                if (couplingGraph == null) return;

                ClusterTree clusterTree = new ClusterTree();
                clusterTree.buildFromCouplingGraph(couplingGraph);

                ModuleGraph moduleGraph = clusterTree.extractModules(
                    new ParameterizedClusteringStrategy(Mode.FIXED_COUNT, targetCount),
                    couplingGraph
                );

                displayResults(moduleGraph, "[FIXED] Fixed Count (" + targetCount + " modules)");
                askForExportOptions(moduleGraph);

            } catch (NumberFormatException e) {
                new MessageDialogBuilder()
                        .setTitle("[ERROR] Invalid Input")
                        .setText("Please enter a valid number.\nExample: 5, 10, 15")
                        .build()
                        .showDialog(gui);
            } catch (Exception e) {
                showError("Module extraction failed", e);
            }
        }
    }

    private void extractModulesCouplingThreshold() {
        String thresholdStr = new TextInputDialogBuilder()
                .setTitle("[THRESHOLD] Coupling Threshold")
                .setDescription("""
                        Enter the minimum coupling threshold:
                        
                        * 0.01-0.05: Very loose modules (many small modules)
                        * 0.05-0.10: Moderate coupling (balanced)
                        * 0.10+: Tight coupling (fewer, larger modules)
                        """)
                .setInitialContent("0.05")
                .build()
                .showDialog(gui);

        if (thresholdStr != null && !thresholdStr.trim().isEmpty()) {
            try {
                double threshold = Double.parseDouble(thresholdStr.trim());

                if (threshold < 0 || threshold > 1) {
                    new MessageDialogBuilder()
                            .setTitle("[ERROR] Invalid Input")
                            .setText("Threshold must be between 0 and 1.\nExample: 0.05, 0.1, 0.15")
                            .build()
                            .showDialog(gui);
                    return;
                }

                new MessageDialogBuilder()
                        .setTitle("[THRESHOLD] Threshold Extraction")
                        .setText("Extracting modules with threshold " + threshold + "...\nPlease wait.")
                        .build()
                        .showDialog(gui);

                CouplingGraph couplingGraph = generateCouplingGraph();
                if (couplingGraph == null) return;

                ClusterTree clusterTree = new ClusterTree();
                clusterTree.buildFromCouplingGraph(couplingGraph);

                ModuleGraph moduleGraph = clusterTree.extractModules(
                    new ParameterizedClusteringStrategy(Mode.THRESHOLD, threshold),
                    couplingGraph
                );

                displayResults(moduleGraph, "[THRESHOLD] Coupling Threshold (" + threshold + ")");
                askForExportOptions(moduleGraph);

            } catch (NumberFormatException e) {
                new MessageDialogBuilder()
                        .setTitle("[ERROR] Invalid Input")
                        .setText("Please enter a valid decimal number.\nExample: 0.05, 0.1, 0.15")
                        .build()
                        .showDialog(gui);
            } catch (Exception e) {
                showError("Module extraction failed", e);
            }
        }
    }

    private void extractModulesCombined() {
        String countStr = new TextInputDialogBuilder()
                .setTitle("[COMBINED] Combined Strategy - Step 1/2")
                .setDescription("""
                        Enter the MAXIMUM number of modules:
                        
                        This sets an upper limit on module count.
                        Extraction may create fewer modules if threshold is reached.
                        """)
                .setInitialContent("10")
                .build()
                .showDialog(gui);

        if (countStr != null && !countStr.trim().isEmpty()) {
            try {
                int targetCount = Integer.parseInt(countStr.trim());

                if (targetCount < 1) {
                    new MessageDialogBuilder()
                            .setTitle("[ERROR] Invalid Input")
                            .setText("Module count must be at least 1.\nPlease try again.")
                            .build()
                            .showDialog(gui);
                    return;
                }

                String thresholdStr = new TextInputDialogBuilder()
                        .setTitle("[COMBINED] Combined Strategy - Step 2/2")
                        .setDescription("""
                                Enter the MINIMUM coupling threshold:
                                
                                Stops splitting when cluster coupling >= threshold
                                Recommended: 0.05 (prevents over-fragmentation)
                                """)
                        .setInitialContent("0.05")
                        .build()
                        .showDialog(gui);

                if (thresholdStr != null && !thresholdStr.trim().isEmpty()) {
                    double threshold = Double.parseDouble(thresholdStr.trim());

                    if (threshold < 0 || threshold > 1) {
                        new MessageDialogBuilder()
                                .setTitle("[ERROR] Invalid Input")
                                .setText("Threshold must be between 0 and 1.\nExample: 0.05, 0.1")
                                .build()
                                .showDialog(gui);
                        return;
                    }

                    new MessageDialogBuilder()
                            .setTitle("[COMBINED] Combined Extraction")
                            .setText("Creating modules (max " + targetCount + ", threshold " + threshold + ")...\nPlease wait.")
                            .build()
                            .showDialog(gui);

                    CouplingGraph couplingGraph = generateCouplingGraph();
                    if (couplingGraph == null) return;

                    ClusterTree clusterTree = new ClusterTree();
                    clusterTree.buildFromCouplingGraph(couplingGraph);

                    ModuleGraph moduleGraph = clusterTree.extractModules(
                        new ParameterizedClusteringStrategy(targetCount, threshold),
                        couplingGraph
                    );

                    displayResults(moduleGraph, 
                        "[COMBINED] Combined (max " + targetCount + " modules, threshold " + threshold + ")");
                    askForExportOptions(moduleGraph);
                }

            } catch (NumberFormatException e) {
                new MessageDialogBuilder()
                        .setTitle("Invalid Input")
                        .setText("Please enter valid numbers.")
                        .build()
                        .showDialog(gui);
            } catch (Exception e) {
                showError("Module extraction failed", e);
            }
        }
    }

    private CouplingGraph generateCouplingGraph() {
        try {
            new MessageDialogBuilder()
                    .setTitle("[PROCESSING] Analyzing Project")
                    .setText("Analyzing project coupling...\n\nThis may take a moment for large projects.")
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
                        .setTitle("[ERROR] No Classes Found")
                        .setText("No Java classes found in the specified path:\n" + projectPath + 
                                 "\n\nPlease check the path and try again.")
                        .build()
                        .showDialog(gui);
                return null;
            }

            return couplingGraph;

        } catch (Exception e) {
            showError("Coupling graph generation failed", e);
            return null;
        }
    }

    private void displayResults(ModuleGraph moduleGraph, String strategyName) {
        StringBuilder result = new StringBuilder();
        result.append("+========================================+\n");
        result.append("|   MODULE EXTRACTION RESULTS            |\n");
        result.append("+========================================+\n\n");
        
        result.append("Strategy: ").append(strategyName).append("\n");
        result.append("-----------------------------------------\n\n");
        
        result.append("Total Modules: ").append(moduleGraph.getModuleCount()).append("\n");
        result.append("Average Cohesion: ").append(String.format("%.4f", moduleGraph.getAverageCohesion())).append("\n\n");
        
        result.append("Module Distribution:\n");
        for (int i = 0; i < Math.min(moduleGraph.getModuleCount(), 10); i++) {
            var module = moduleGraph.getModules().get(i);
            result.append("  * Module ").append(i + 1).append(": ");
            result.append(module.getClasses().size()).append(" classes ");
            result.append("(cohesion: ").append(String.format("%.3f", module.getCohesion())).append(")\n");
        }
        
        if (moduleGraph.getModuleCount() > 10) {
            result.append("  ... and ").append(moduleGraph.getModuleCount() - 10).append(" more modules\n");
        }

        new MessageDialogBuilder()
                .setTitle("[SUCCESS] Extraction Complete")
                .setText(result.toString())
                .build()
                .showDialog(gui);
    }

    private void askForExportOptions(ModuleGraph moduleGraph) {
        new ActionListDialogBuilder()
                .setTitle("Export Options")
                .setDescription("""
                        Choose export format:
                        
                        [T] Text: Simple text file with module details
                        [P] PlantUML: UML diagram with module grouping
                        [B] Both: Export in both formats
                        """)
                .addAction("[T] Export to Text File", () -> exportModuleGraph(moduleGraph))
                .addAction("[P] Export to PlantUML Diagram", () -> exportModuleDiagram(moduleGraph))
                .addAction("[B] Export Both Formats", () -> {
                    exportModuleGraph(moduleGraph);
                    exportModuleDiagram(moduleGraph);
                })
                .addAction("[X] Skip Export", () -> {
                })
                .build()
                .showDialog(gui);
    }

    private void exportModuleGraph(ModuleGraph moduleGraph) {
        String filePath = new TextInputDialogBuilder()
                .setTitle("Export to Text - File Path")
                .setDescription("Enter the file path to save module details:\n(Default: ./modules.txt)")
                .setInitialContent("./modules.txt")
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {
                moduleGraph.exportToFile(filePath.trim());

                new MessageDialogBuilder()
                        .setTitle("[SUCCESS] Export Successful")
                        .setText("Module graph exported successfully!\n\nLocation: " + filePath.trim() + 
                                 "\n\nYou can now view the module details in this file.")
                        .build()
                        .showDialog(gui);

            } catch (IOException e) {
                showError("Export failed", e);
            }
        }
    }

    private void exportModuleDiagram(ModuleGraph moduleGraph) {
        String filePath = new TextInputDialogBuilder()
                .setTitle("Export PlantUML - File Path")
                .setDescription("Enter the file path for PlantUML diagram:\n(Default: ./classdiagram-with-modules.puml)")
                .setInitialContent("./classdiagram-with-modules.puml")
                .build()
                .showDialog(gui);

        if (filePath != null && !filePath.trim().isEmpty()) {
            try {

                new MessageDialogBuilder()
                        .setTitle("[PROCESSING] Generating Diagram")
                        .setText("Creating PlantUML class diagram with modules...\nPlease wait.")
                        .build()
                        .showDialog(gui);

                ClassDiagramProcessor processor;
                if (rootPackage != null) {
                    processor = new ClassDiagramProcessor(projectPath, rootPackage);
                } else {
                    processor = new ClassDiagramProcessor(projectPath);
                }
                ClassDiagram classDiagram = processor.generateClassDiagram();

                ClassDiagramWithModulesPlantUMLExportStrategy strategy =
                    new ClassDiagramWithModulesPlantUMLExportStrategy(classDiagram, moduleGraph);
                strategy.export(filePath.trim());

                new MessageDialogBuilder()
                        .setTitle("[SUCCESS] Export Successful")
                        .setText("PlantUML diagram exported successfully!\n\n" +
                                "Location: " + filePath.trim() + "\n" +
                                "-----------------------------------------\n" +
                                "Modules: " + moduleGraph.getModuleCount() + "\n" +
                                "Classes: " + classDiagram.getClassCount() + "\n\n" +
                                "Visualize at: https://www.plantuml.com/plantuml/uml/")
                        .build()
                        .showDialog(gui);

            } catch (Exception e) {
                showError("PlantUML export failed", e);
            }
        }
    }

    private void showError(String title, Exception e) {
        new MessageDialogBuilder()
                .setTitle("[ERROR] " + title)
                .setText("An error occurred:\n\n" + e.getMessage() + 
                         "\n\nPlease check your inputs and try again.")
                .build()
                .showDialog(gui);
    }
}
