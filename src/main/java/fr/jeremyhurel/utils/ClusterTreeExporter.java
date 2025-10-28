package fr.jeremyhurel.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.ClusterTree.ClusterNode;
import static fr.jeremyhurel.utils.StringFormatter.escapeDot;
import static fr.jeremyhurel.utils.StringFormatter.escapeJson;
import static fr.jeremyhurel.utils.StringFormatter.formatScientific;

public class ClusterTreeExporter {

    private ClusterTreeExporter() {

    }

    public static void exportToJson(ClusterTree clusterTree, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"clusterTree\": {\n");
            writer.write("    \"depth\": " + clusterTree.getDepth() + ",\n");
            writer.write("    \"mergeSteps\": " + clusterTree.getMergeHistory().size() + ",\n");
            writer.write("    \"root\": ");

            if (clusterTree.getRoot() != null) {
                writeNodeJson(writer, clusterTree.getRoot(), 2);
            } else {
                writer.write("null");
            }

            writer.write(",\n");
            writer.write("    \"mergeHistory\": [\n");

            boolean first = true;
            for (ClusterNode node : clusterTree.getMergeHistory()) {
                if (!first) {
                    writer.write(",\n");
                }
                writer.write("      {\n");
                writer.write("        \"step\": " + (clusterTree.getMergeHistory().indexOf(node) + 1) + ",\n");
                writer.write("        \"clusterName\": \"" + escapeJson(node.getName()) + "\",\n");
                writer.write("        \"couplingValue\": " + node.getCouplingValue() + ",\n");
                writer.write("        \"leftChild\": \"" + escapeJson(node.getLeft().getName()) + "\",\n");
                writer.write("        \"rightChild\": \"" + escapeJson(node.getRight().getName()) + "\"\n");
                writer.write("      }");
                first = false;
            }

            writer.write("\n    ]\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
    }

    private static void writeNodeJson(FileWriter writer, ClusterNode node, int indent) throws IOException {
        String indentStr = " ".repeat(indent);

        writer.write("{\n");
        writer.write(indentStr + "  \"id\": " + node.getId() + ",\n");
        writer.write(indentStr + "  \"name\": \"" + escapeJson(node.getName()) + "\",\n");
        writer.write(indentStr + "  \"isLeaf\": " + node.isLeaf() + ",\n");
        writer.write(indentStr + "  \"couplingValue\": " + node.getCouplingValue());

        if (!node.isLeaf()) {
            writer.write(",\n");
            writer.write(indentStr + "  \"left\": ");
            writeNodeJson(writer, node.getLeft(), indent + 2);
            writer.write(",\n");
            writer.write(indentStr + "  \"right\": ");
            writeNodeJson(writer, node.getRight(), indent + 2);
        }

        writer.write("\n" + indentStr + "}");
    }

    public static void exportToDot(ClusterTree clusterTree, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("digraph ClusterTree {\n");
            writer.write("  rankdir=TB;\n");
            writer.write("  node [shape=box, style=\"rounded,filled\", fillcolor=lightblue];\n");
            writer.write("  edge [dir=none];\n\n");

            writer.write("  // Leaf nodes\n");
            if (clusterTree.getRoot() != null) {
                List<ClusterNode> leaves = getLeafNodes(clusterTree.getRoot());
                for (ClusterNode leaf : leaves) {
                    writer.write("  node" + leaf.getId() + " [label=\"" + escapeDot(leaf.getName())
                            + "\", fillcolor=lightgreen];\n");
                }
            }

            writer.write("\n  // Internal nodes\n");
            if (clusterTree.getRoot() != null) {
                List<ClusterNode> internals = getInternalNodes(clusterTree.getRoot());
                for (ClusterNode internal : internals) {

                    String nodeName = internal.getName();
                    String label = nodeName + "\\n" + formatScientific(internal.getCouplingValue());
                    writer.write("  node" + internal.getId() + " [label=\"" + escapeDot(label)
                            + "\", fillcolor=lightyellow];\n");
                }
            }

            writer.write("\n  // Edges\n");
            if (clusterTree.getRoot() != null) {
                writeDotEdges(writer, clusterTree.getRoot());
            }

            writer.write("}\n");
        }
    }

    public static void exportToNewick(ClusterTree clusterTree, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            if (clusterTree.getRoot() != null) {
                writer.write(writeNewick(clusterTree.getRoot()));
                writer.write(";\n");
            }
        }
    }

    private static String writeNewick(ClusterNode node) {
        if (node.isLeaf()) {
            return node.getName() + ":" + formatScientific(node.getCouplingValue());
        }

        String left = writeNewick(node.getLeft());
        String right = writeNewick(node.getRight());

        return "(" + left + "," + right + "):" + formatScientific(node.getCouplingValue());
    }

    public static void exportToText(ClusterTree clusterTree, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("Hierarchical Cluster Tree\n");
            writer.write("=========================\n\n");
            writer.write("Tree Depth: " + clusterTree.getDepth() + "\n");
            writer.write("Merge Steps: " + clusterTree.getMergeHistory().size() + "\n\n");

            writer.write("Merge History (chronological):\n");
            writer.write("------------------------------\n");

            int step = 1;
            for (ClusterNode node : clusterTree.getMergeHistory()) {
                writer.write(String.format("Step %d: Merged '%s' and '%s' (coupling: %s) -> '%s'\n",
                        step++,
                        node.getLeft().getName(),
                        node.getRight().getName(),
                        formatScientific(node.getCouplingValue()),
                        node.getName()));
            }

            writer.write("\n\nTree Structure:\n");
            writer.write("---------------\n");
            if (clusterTree.getRoot() != null) {
                writeTextTree(writer, clusterTree.getRoot(), "", true);
            }
        }
    }

    private static void writeTextTree(FileWriter writer, ClusterNode node, String prefix, boolean isTail)
            throws IOException {
        writer.write(prefix + (isTail ? "└── " : "├── ") + node.getName());
        if (!node.isLeaf()) {
            writer.write(" [coupling: " + formatScientific(node.getCouplingValue()) + "]");
        }
        writer.write("\n");

        if (!node.isLeaf()) {
            writeTextTree(writer, node.getLeft(), prefix + (isTail ? "    " : "│   "), false);
            writeTextTree(writer, node.getRight(), prefix + (isTail ? "    " : "│   "), true);
        }
    }

    private static void writeDotEdges(FileWriter writer, ClusterNode node) throws IOException {
        if (!node.isLeaf()) {
            writer.write("  node" + node.getId() + " -> node" + node.getLeft().getId()
                    + " [label=\"\"];\n");
            writer.write("  node" + node.getId() + " -> node" + node.getRight().getId()
                    + " [label=\"\"];\n");

            writeDotEdges(writer, node.getLeft());
            writeDotEdges(writer, node.getRight());
        }
    }

    private static List<ClusterNode> getLeafNodes(ClusterNode node) {
        List<ClusterNode> leaves = new ArrayList<>();
        if (node.isLeaf()) {
            leaves.add(node);
        } else {
            leaves.addAll(getLeafNodes(node.getLeft()));
            leaves.addAll(getLeafNodes(node.getRight()));
        }
        return leaves;
    }

    private static List<ClusterNode> getInternalNodes(ClusterNode node) {
        List<ClusterNode> internals = new ArrayList<>();
        if (!node.isLeaf()) {
            internals.add(node);
            internals.addAll(getInternalNodes(node.getLeft()));
            internals.addAll(getInternalNodes(node.getRight()));
        }
        return internals;
    }
}
