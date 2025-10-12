package fr.jeremyhurel.test;

import fr.jeremyhurel.processors.CallGraphProcessor;
import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.utils.CallGraphExporter;

public class TestDotExport {
    public static void main(String[] args) {
        try {
            // Generate call graph for the test sample
            CallGraphProcessor processor = new CallGraphProcessor("./src/main/java");
            CallGraph callGraph = processor.generateCallGraph();

            // Export to DOT format
            CallGraphExporter.exportToDot(callGraph, "./test-callgraph.dot");
            CallGraphExporter.exportToJson(callGraph, "./test-callgraph.json");

            System.out.println("Call graph exported successfully!");
            System.out.println("Nodes found: " + callGraph.getNodeCount());
            if (callGraph.getRootNode() != null) {
                System.out.println("Root node: " + callGraph.getRootNode().getFullName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}