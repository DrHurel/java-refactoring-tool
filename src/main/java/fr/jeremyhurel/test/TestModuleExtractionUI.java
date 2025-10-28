package fr.jeremyhurel.test;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.ModuleGraph;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy.Mode;
import fr.jeremyhurel.processors.CouplingGraphProcessor;

public class TestModuleExtractionUI {
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("MODULE EXTRACTION UI TEST");
            System.out.println("========================================\n");

            String projectPath = "./src/main/java";
            System.out.println("Analyzing project: " + projectPath);

            CouplingGraphProcessor processor = new CouplingGraphProcessor(projectPath);
            CouplingGraph couplingGraph = processor.generateCouplingGraph();

            System.out.println("✓ Coupling graph generated");
            System.out.println("  Classes found: " + couplingGraph.getNodeCount() + "\n");

            System.out.println("Building cluster tree...");
            ClusterTree clusterTree = new ClusterTree();
            clusterTree.buildFromCouplingGraph(couplingGraph);
            System.out.println("✓ Cluster tree built\n");

            System.out.println("----------------------------------------");
            System.out.println("Test 1: Automatic (Elbow Method)");
            System.out.println("----------------------------------------");
            ModuleGraph autoModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.AUTO),
                couplingGraph
            );
            displayResults(autoModules, "Automatic (Elbow Method)");

            System.out.println("\n----------------------------------------");
            System.out.println("Test 2: Fixed Count (5 modules)");
            System.out.println("----------------------------------------");
            ModuleGraph fixedModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.FIXED_COUNT, 5),
                couplingGraph
            );
            displayResults(fixedModules, "Fixed Count (5)");

            System.out.println("\n----------------------------------------");
            System.out.println("Test 3: Export to File");
            System.out.println("----------------------------------------");
            String exportPath = "./test-modules-ui.txt";
            autoModules.exportToFile(exportPath);
            System.out.println("✓ Module graph exported to: " + exportPath);

            System.out.println("\n========================================");
            System.out.println("ALL TESTS PASSED ✓");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("\n✗ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayResults(ModuleGraph moduleGraph, String strategyName) {
        System.out.println("Strategy: " + strategyName);
        System.out.println("Total Modules: " + moduleGraph.getModuleCount());
        System.out.println("Average Cohesion: " + String.format("%.4f", moduleGraph.getAverageCohesion()));

        int count = 0;
        for (var module : moduleGraph.getModules()) {
            if (count++ >= 3) {
                System.out.println("  ... (" + (moduleGraph.getModuleCount() - 3) + " more modules)");
                break;
            }
            System.out.println("  • " + module.getName() + ": " +
                module.getClasses().size() + " classes, " +
                "cohesion=" + String.format("%.4f", module.getCohesion()));
        }
    }
}
