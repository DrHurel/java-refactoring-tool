package fr.jeremyhurel.utils;

import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.models.CalleeGraphNode;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CallGraphExporter {

    public static void exportToJson(CallGraph callGraph, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"callGraph\": {\n");
            writer.write("    \"nodeCount\": " + callGraph.getNodeCount() + ",\n");
            writer.write("    \"rootNode\": ");

            if (callGraph.getRootNode() != null) {
                writeNodeJson(writer, callGraph.getRootNode(), "    ", new HashSet<>());
            } else {
                writer.write("null");
            }

            writer.write(",\n    \"allNodes\": [\n");
            boolean first = true;
            for (CalleeGraphNode node : callGraph.getNodes().values()) {
                if (!first) {
                    writer.write(",\n");
                }
                writer.write("      {\n");
                writer.write("        \"className\": \"" + escapeJson(node.getClassName()) + "\",\n");
                writer.write("        \"methodName\": \"" + escapeJson(node.getMethodName()) + "\",\n");
                writer.write("        \"signature\": \"" + escapeJson(node.getSignature()) + "\",\n");
                writer.write("        \"lineNumber\": " + node.getLineNumber() + ",\n");
                writer.write("        \"calleeCount\": " + node.getCallees().size() + "\n");
                writer.write("      }");
                first = false;
            }
            writer.write("\n    ]\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
    }

    private static void writeNodeJson(FileWriter writer, CalleeGraphNode node, String indent,
            Set<CalleeGraphNode> visited) throws IOException {
        if (visited.contains(node)) {
            writer.write("{\n");
            writer.write(indent + "  \"className\": \"" + escapeJson(node.getClassName()) + "\",\n");
            writer.write(indent + "  \"methodName\": \"" + escapeJson(node.getMethodName()) + "\",\n");
            writer.write(indent + "  \"signature\": \"" + escapeJson(node.getSignature()) + "\",\n");
            writer.write(indent + "  \"lineNumber\": " + node.getLineNumber() + ",\n");
            writer.write(indent + "  \"callees\": \"[CIRCULAR_REFERENCE]\"\n");
            writer.write(indent + "}");
            return;
        }

        visited.add(node);
        writer.write("{\n");
        writer.write(indent + "  \"className\": \"" + escapeJson(node.getClassName()) + "\",\n");
        writer.write(indent + "  \"methodName\": \"" + escapeJson(node.getMethodName()) + "\",\n");
        writer.write(indent + "  \"signature\": \"" + escapeJson(node.getSignature()) + "\",\n");
        writer.write(indent + "  \"lineNumber\": " + node.getLineNumber() + ",\n");
        writer.write(indent + "  \"callees\": [\n");

        boolean first = true;
        for (CalleeGraphNode callee : node.getCallees()) {
            if (!first) {
                writer.write(",\n");
            }
            writer.write(indent + "    ");
            writeNodeJson(writer, callee, indent + "    ", new HashSet<>(visited));
            first = false;
        }

        writer.write("\n" + indent + "  ]\n");
        writer.write(indent + "}");
        visited.remove(node);
    }

    public static void exportToDot(CallGraph callGraph, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("digraph CallGraph {\n");
            writer.write("    rankdir=TB;\n");
            writer.write("    node [shape=box, style=filled, fillcolor=lightblue];\n");
            writer.write("    edge [color=black, arrowhead=open];\n\n");

            Set<String> writtenNodes = new HashSet<>();
            Set<String> writtenEdges = new HashSet<>();

            if (callGraph.getRootNode() != null) {
                // Mark root node with special styling
                String rootNodeId = sanitizeForDot(callGraph.getRootNode().getFullName());
                writer.write("    \"" + rootNodeId + "\" [fillcolor=lightgreen, label=\""
                        + escapeDot(callGraph.getRootNode().getFullName()) + "\\n(ROOT)\"];\n");
                writtenNodes.add(rootNodeId);

                writeDotNode(writer, callGraph.getRootNode(), writtenNodes, writtenEdges, new HashSet<>());
            } else {
                // If no root node, write all nodes and their relationships
                for (CalleeGraphNode node : callGraph.getNodes().values()) {
                    writeDotNode(writer, node, writtenNodes, writtenEdges, new HashSet<>());
                }
            }

            writer.write("}\n");
        }
    }

    private static void writeDotNode(FileWriter writer, CalleeGraphNode node, Set<String> writtenNodes,
            Set<String> writtenEdges, Set<CalleeGraphNode> visited) throws IOException {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);

        String nodeId = sanitizeForDot(node.getFullName());

        // Write node definition if not already written
        if (!writtenNodes.contains(nodeId)) {
            String label = escapeDot(node.getClassName()) + "\\n" + escapeDot(node.getMethodName());
            writer.write("    \"" + nodeId + "\" [label=\"" + label + "\"];\n");
            writtenNodes.add(nodeId);
        }

        // Write edges to callees
        for (CalleeGraphNode callee : node.getCallees()) {
            String calleeId = sanitizeForDot(callee.getFullName());
            String edge = nodeId + " -> " + calleeId;

            // Write callee node if not already written
            if (!writtenNodes.contains(calleeId)) {
                String calleeLabel = escapeDot(callee.getClassName()) + "\\n" + escapeDot(callee.getMethodName());
                writer.write("    \"" + calleeId + "\" [label=\"" + calleeLabel + "\"];\n");
                writtenNodes.add(calleeId);
            }

            // Write edge if not already written
            if (!writtenEdges.contains(edge)) {
                writer.write("    \"" + nodeId + "\" -> \"" + calleeId + "\";\n");
                writtenEdges.add(edge);
            }

            writeDotNode(writer, callee, writtenNodes, writtenEdges, new HashSet<>(visited));
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