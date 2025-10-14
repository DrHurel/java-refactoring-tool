package fr.jeremyhurel.test;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.processors.CouplingGraphProcessor;
import fr.jeremyhurel.utils.ClusterTreeExporter;

public class TestClusterTreeExport {

    public static void main(String[] args) {
        try {
            System.out.println("=== Cluster Tree Generation Test ===\n");

            // Test on this project itself
            String projectPath = "./src/main/java";
            String rootPackage = "fr.jeremyhurel";

            System.out.println("Analyzing project: " + projectPath);
            System.out.println("Root package: " + rootPackage);
            System.out.println("\nGenerating coupling graph...");

            // Generate coupling graph
            CouplingGraphProcessor processor = new CouplingGraphProcessor(projectPath, rootPackage);
            CouplingGraph couplingGraph = processor.generateCouplingGraph();

            System.out.println("Classes found: " + couplingGraph.getNodeCount());
            System.out.println("Coupling relationships: " + couplingGraph.getCouplingCount());
            System.out.println("Total method calls: " + couplingGraph.getTotalMethodCalls());

            if (couplingGraph.getNodeCount() < 2) {
                System.out.println("\nNot enough classes to build cluster tree.");
                return;
            }

            System.out.println("\nBuilding hierarchical cluster tree...");

            // Generate cluster tree
            ClusterTree clusterTree = processor.generateClusterTree(couplingGraph);

            System.out.println("Tree depth: " + clusterTree.getDepth());
            System.out.println("Merge steps: " + clusterTree.getMergeHistory().size());

            // Export to all formats
            System.out.println("\nExporting cluster tree...");

            ClusterTreeExporter.exportToJson(clusterTree, "clustertree.json");
            System.out.println("✓ Exported to clustertree.json");

            ClusterTreeExporter.exportToDot(clusterTree, "clustertree.dot");
            System.out.println("✓ Exported to clustertree.dot");

            ClusterTreeExporter.exportToText(clusterTree, "clustertree.txt");
            System.out.println("✓ Exported to clustertree.txt");

            ClusterTreeExporter.exportToNewick(clusterTree, "clustertree.nwk");
            System.out.println("✓ Exported to clustertree.nwk");

            System.out.println("\nCluster tree generation completed successfully!");
            System.out.println("\nVisualize with:");
            System.out.println("  dot -Tpng clustertree.dot -o clustertree.png");
            System.out.println("  cat clustertree.txt");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
