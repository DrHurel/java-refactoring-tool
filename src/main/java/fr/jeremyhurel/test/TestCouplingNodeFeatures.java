package fr.jeremyhurel.test;

import java.util.Map;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.CouplingNode;
import fr.jeremyhurel.processors.CouplingGraphProcessor;

public class TestCouplingNodeFeatures {
    public static void main(String[] args) {
        System.out.println("=== CouplingNode Features Test ===\n");

        System.out.println("Test 1: Basic CouplingNode creation");
        System.out.println("-----------------------------------");
        CouplingNode node1 = new CouplingNode("com.example.ClassA", 0.125f);
        CouplingNode node2 = new CouplingNode("com.example.ClassB", 0.075f);
        System.out.println("Node 1: " + node1);
        System.out.println("Node 2: " + node2);
        System.out.println();

        System.out.println("Test 2: Node merge functionality");
        System.out.println("---------------------------------");
        CouplingNode merged = node1.merge(node2);
        System.out.println("Merged node: " + merged);
        System.out.println("Combined coupling value: " + merged.getCouplingValue());
        System.out.println();

        System.out.println("Test 3: Real coupling graph analysis");
        System.out.println("-------------------------------------");
        CouplingGraphProcessor processor = new CouplingGraphProcessor("./src/main/java", "fr.jeremyhurel");
        CouplingGraph graph = processor.generateCouplingGraph();

        System.out.println("Total classes analyzed: " + graph.getNodeCount());
        System.out.println("Total method calls: " + graph.getTotalMethodCalls());
        System.out.println();

        System.out.println("Test 4: Top 10 classes by total outgoing coupling");
        System.out.println("--------------------------------------------------");

        Map<String, Float> couplingValues = graph.getNodeCouplingValues();
        couplingValues.entrySet().stream()
                .sorted((e1, e2) -> Float.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .forEach(entry -> {
                    String className = entry.getKey();
                    String simpleClassName = className.substring(className.lastIndexOf('.') + 1);
                    float coupling = entry.getValue();
                    System.out.printf("  %-30s %.6f%n", simpleClassName, coupling);
                });
        System.out.println();

        System.out.println("Test 5: Get specific node coupling value");
        System.out.println("-----------------------------------------");
        String testClassName = "fr.jeremyhurel.utils.ClusterTreeExporter";
        float coupling = graph.getNodeCouplingValue(testClassName);
        System.out.println("Class: ClusterTreeExporter");
        System.out.println("Total outgoing coupling: " + String.format("%.6f", coupling));
        System.out.println();

        System.out.println("Test 6: Merge two nodes from graph");
        System.out.println("-----------------------------------");
        String class1 = "fr.jeremyhurel.models.CouplingNode";
        String class2 = "fr.jeremyhurel.models.CouplingGraph";

        if (graph.getNode(class1) != null && graph.getNode(class2) != null) {
            System.out.println("Node 1: " + graph.getNode(class1));
            System.out.println("Node 2: " + graph.getNode(class2));

            CouplingNode mergedFromGraph = graph.mergeNodes(class1, class2);
            System.out.println("Merged: " + mergedFromGraph);
        } else {
            System.out.println("One or both nodes not found in graph");
        }
        System.out.println();

        System.out.println("=== All tests completed successfully! ===");
    }
}
