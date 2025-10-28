package fr.jeremyhurel.test;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.ModuleGraph;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy.Mode;
import fr.jeremyhurel.processors.CouplingGraphProcessor;

public class TestCombinedStrategy {
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("COMBINED STRATEGY TEST");
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
            System.out.println("Test 1: Fixed Count Only (10 modules)");
            System.out.println("----------------------------------------");
            ModuleGraph fixedModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.FIXED_COUNT, 10),
                couplingGraph
            );
            displayResults(fixedModules);

            System.out.println("\n----------------------------------------");
            System.out.println("Test 2: Threshold Only (0.05)");
            System.out.println("----------------------------------------");
            ModuleGraph thresholdModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.THRESHOLD, 0.05),
                couplingGraph
            );
            displayResults(thresholdModules);

            System.out.println("\n----------------------------------------");
            System.out.println("Test 3: Combined (max 10 modules, threshold 0.05)");
            System.out.println("----------------------------------------");
            System.out.println("This will create up to 10 modules, but stops if");
            System.out.println("the next split would break a cluster with coupling >= 0.05");
            ModuleGraph combinedModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(10, 0.05),
                couplingGraph
            );
            displayResults(combinedModules);

            System.out.println("\n----------------------------------------");
            System.out.println("Test 4: Combined with stricter threshold");
            System.out.println("(max 15 modules, threshold 0.1)");
            System.out.println("----------------------------------------");
            ModuleGraph strictModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(15, 0.1),
                couplingGraph
            );
            displayResults(strictModules);

            System.out.println("\n========================================");
            System.out.println("TEST COMPLETE");
            System.out.println("========================================");

        } catch (Exception e) {
            System.err.println("Error during test: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void displayResults(ModuleGraph moduleGraph) {
        System.out.println("Modules extracted: " + moduleGraph.getModuleCount());
        System.out.println("Average cohesion: " + String.format("%.4f", moduleGraph.getAverageCohesion()));
        
        System.out.println("\nModule details:");
        for (int i = 0; i < moduleGraph.getModuleCount(); i++) {
            var module = moduleGraph.getModules().get(i);
            System.out.println("  Module " + (i + 1) + ": " + 
                module.getClasses().size() + " classes, " +
                "cohesion = " + String.format("%.4f", module.getCohesion()));
        }
    }
}
