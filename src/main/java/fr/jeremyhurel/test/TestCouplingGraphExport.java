package fr.jeremyhurel.test;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.processors.CouplingGraphProcessor;
import fr.jeremyhurel.utils.CouplingGraphExporter;

public class TestCouplingGraphExport {
    public static void main(String[] args) {
        try {
            System.out.println("=== Coupling Graph Export Test ===\n");
            System.out.println("Analyzing project: ./src/main/java");
            System.out.println("Root package: fr.jeremyhurel\n");

            CouplingGraphProcessor processor = new CouplingGraphProcessor("./src/main/java", "fr.jeremyhurel");

            System.out.println("Generating coupling graph...");
            CouplingGraph couplingGraph = processor.generateCouplingGraph();

            System.out.println("Classes found: " + couplingGraph.getNodeCount());
            System.out.println("Coupling relationships: " + couplingGraph.getCouplingCount());
            System.out.println("Total method calls: " + couplingGraph.getTotalMethodCalls());

            System.out.println("\nExporting coupling graph...");
            CouplingGraphExporter.exportToJson(couplingGraph, "./couplinggraph.json");
            CouplingGraphExporter.exportToDot(couplingGraph, "./couplinggraph.dot");

            System.out.println("✓ Exported to couplinggraph.json");
            System.out.println("✓ Exported to couplinggraph.dot");

            System.out.println("\nCoupling graph generation completed successfully!");
            System.out.println("\nVisualize with:");
            System.out.println("  dot -Tpng couplinggraph.dot -o couplinggraph.png");
            System.out.println("  cat couplinggraph.json");

        } catch (Exception e) {
            System.err.println("Error during coupling graph export:");
            e.printStackTrace();
        }
    }
}
