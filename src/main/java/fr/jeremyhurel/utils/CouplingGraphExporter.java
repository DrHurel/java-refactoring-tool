package fr.jeremyhurel.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.CouplingNode;

public class CouplingGraphExporter {

    public static void exportToJson(CouplingGraph couplingGraph, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"couplingGraph\": {\n");
            writer.write("    \"specification\": \"Couplage(A,B) = Number of method calls between A and B / Total method calls in application\",\n");
            writer.write("    \"nodeCount\": " + couplingGraph.getNodeCount() + ",\n");
            writer.write("    \"couplingCount\": " + couplingGraph.getCouplingCount() + ",\n");
            writer.write("    \"totalMethodCalls\": " + couplingGraph.getTotalMethodCalls() + ",\n");
            writer.write("    \"nodes\": [\n");

            boolean firstNode = true;
            for (CouplingNode node : couplingGraph.getNodes().values()) {
                if (!firstNode) {
                    writer.write(",\n");
                }
                writer.write("      {\n");
                writer.write("        \"className\": \"" + escapeJson(node.getClassName()) + "\"\n");
                writer.write("      }");
                firstNode = false;
            }
            writer.write("\n    ],\n");

            writer.write("    \"couplings\": [\n");
            boolean firstCoupling = true;
            for (Map.Entry<String, Map<String, Integer>> entry : couplingGraph.getCallCountMatrix().entrySet()) {
                String from = entry.getKey();
                for (Map.Entry<String, Integer> callEntry : entry.getValue().entrySet()) {
                    String to = callEntry.getKey();
                    int callCount = callEntry.getValue();
                    double normalizedCoupling = couplingGraph.getCouplingWeight(from, to);

                    if (!firstCoupling) {
                        writer.write(",\n");
                    }
                    writer.write("      {\n");
                    writer.write("        \"from\": \"" + escapeJson(from) + "\",\n");
                    writer.write("        \"to\": \"" + escapeJson(to) + "\",\n");
                    writer.write("        \"methodCallCount\": " + callCount + ",\n");
                    writer.write("        \"normalizedCoupling\": " + String.format("%.6f", normalizedCoupling) + "\n");
                    writer.write("      }");
                    firstCoupling = false;
                }
            }
            writer.write("\n    ]\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
    }

    public static void exportToDot(CouplingGraph couplingGraph, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("digraph CouplingGraph {\n");
            writer.write("    rankdir=LR;\n");
            writer.write("    node [shape=box, style=filled, fillcolor=lightblue];\n");
            writer.write("    edge [color=gray];\n\n");

            // Write all nodes
            for (CouplingNode node : couplingGraph.getNodes().values()) {
                String nodeId = sanitizeForDot(node.getClassName());
                String label = getSimpleClassName(node.getClassName());
                writer.write("    \"" + nodeId + "\" [label=\"" + escapeDot(label) + "\"];\n");
            }

            writer.write("\n");

            // Write all coupling relationships with call counts and normalized values
            for (Map.Entry<String, Map<String, Integer>> entry : couplingGraph.getCallCountMatrix().entrySet()) {
                String from = entry.getKey();
                String fromId = sanitizeForDot(from);

                for (Map.Entry<String, Integer> callEntry : entry.getValue().entrySet()) {
                    String to = callEntry.getKey();
                    String toId = sanitizeForDot(to);
                    int callCount = callEntry.getValue();
                    double normalizedCoupling = couplingGraph.getCouplingWeight(from, to);

                    // Vary edge attributes based on coupling strength
                    String edgeStyle = getEdgeStyle(normalizedCoupling);
                    String edgeColor = getEdgeColor(normalizedCoupling);

                    // Label shows both call count and normalized coupling
                    String label = "calls=" + callCount + "\\ncoupling=" + String.format("%.4f", normalizedCoupling);

                    writer.write("    \"" + fromId + "\" -> \"" + toId + "\" [label=\""
                            + label + "\", " + edgeStyle + ", color=\"" + edgeColor
                            + "\"];\n");
                }
            }

            writer.write("}\n");
        }
    }

    private static String getSimpleClassName(String fullClassName) {
        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < fullClassName.length() - 1) {
            return fullClassName.substring(lastDotIndex + 1);
        }
        return fullClassName;
    }

    /**
     * Returns edge style based on normalized coupling value.
     * Since normalized coupling is typically small (1/total_calls), we adjust thresholds.
     */
    private static String getEdgeStyle(double normalizedCoupling) {
        if (normalizedCoupling >= 0.1) {
            return "style=bold, penwidth=3";
        } else if (normalizedCoupling >= 0.05) {
            return "style=solid, penwidth=2";
        } else if (normalizedCoupling >= 0.01) {
            return "style=solid, penwidth=1.5";
        } else {
            return "style=solid, penwidth=1";
        }
    }

    /**
     * Returns edge color based on normalized coupling value.
     * Higher coupling = stronger/more critical relationship.
     */
    private static String getEdgeColor(double normalizedCoupling) {
        if (normalizedCoupling >= 0.1) {
            return "red";
        } else if (normalizedCoupling >= 0.05) {
            return "orange";
        } else if (normalizedCoupling >= 0.01) {
            return "blue";
        } else {
            return "gray";
        }
    }

    private static String sanitizeForDot(String name) {
        return name.replaceAll("[^a-zA-Z0-9_.]", "_");
    }

    private static String escapeDot(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
