package fr.jeremyhurel.test;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.Module;
import fr.jeremyhurel.models.ModuleGraph;
import fr.jeremyhurel.processors.CouplingGraphProcessor;

public class TestModuleExtraction {

    public static void main(String[] args) {
        try {
            String projectPath = "./src/main/java";

            System.out.println("========================================");
            System.out.println("MODULE EXTRACTION DEMONSTRATION");
            System.out.println("========================================\n");

            System.out.println("Step 1: Generating coupling graph...");
            CouplingGraphProcessor processor = new CouplingGraphProcessor(projectPath);
            CouplingGraph couplingGraph = processor.generateCouplingGraph();
            System.out.println("  - Total classes: " + couplingGraph.getNodeCount());
            System.out.println("  - Total couplings: " + couplingGraph.getCouplingCount());
            System.out.println();

            System.out.println("Step 2: Building hierarchical cluster tree...");
            ClusterTree clusterTree = processor.generateClusterTree();
            System.out.println("  - Tree depth: " + clusterTree.getDepth());
            System.out.println("  - Merge steps: " + clusterTree.getMergeHistory().size());
            System.out.println();

            System.out.println("========================================");
            System.out.println("TEST CASE 1: Automatic Clustering");
            System.out.println("========================================");
            ModuleGraph autoModules = clusterTree.extractModules(null, null, couplingGraph);
            displayModuleGraph(autoModules, "Automatic (Elbow Method)");

            System.out.println("\n========================================");
            System.out.println("TEST CASE 2: Fixed Number of Modules");
            System.out.println("========================================");

            int[] targetCounts = {3, 5, 7};
            for (int targetCount : targetCounts) {
                ModuleGraph modules = clusterTree.extractModules(targetCount, null, couplingGraph);
                displayModuleGraph(modules, "Target: " + targetCount + " modules");
            }

            System.out.println("\n========================================");
            System.out.println("TEST CASE 3: Coupling Threshold");
            System.out.println("========================================");

            double[] thresholds = {0.01, 0.05, 0.1};
            for (double threshold : thresholds) {
                ModuleGraph modules = clusterTree.extractModules(null, threshold, couplingGraph);
                displayModuleGraph(modules, "Threshold: " + String.format("%.2f", threshold));
            }

            System.out.println("\n========================================");
            System.out.println("CLUSTERING COMPLETE");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayModuleGraph(ModuleGraph moduleGraph, String description) {
        System.out.println("\n" + description);
        System.out.println("-".repeat(60));
        System.out.println("Total modules: " + moduleGraph.getModuleCount());
        System.out.println("Average cohesion: " + String.format("%.4f", moduleGraph.getAverageCohesion()));
        System.out.println();

        for (Module module : moduleGraph.getModules()) {
            System.out.println("  " + module.getName() + ":");
            System.out.println("    - Size: " + module.getSize() + " classes");
            System.out.println("    - Cohesion: " + String.format("%.4f", module.getCohesion()));

            int displayCount = Math.min(5, module.getClasses().size());
            System.out.println("    - Classes (" + displayCount + "/" + module.getSize() + "):");
            for (int i = 0; i < displayCount; i++) {
                String className = module.getClasses().get(i);

                String simpleName = className.contains(".") ?
                    className.substring(className.lastIndexOf('.') + 1) : className;
                System.out.println("        • " + simpleName);
            }
            if (module.getSize() > displayCount) {
                System.out.println("        ... (" + (module.getSize() - displayCount) + " more)");
            }
            System.out.println();
        }
    }
}
