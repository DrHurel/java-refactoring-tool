package fr.jeremyhurel.utils.strategies;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.models.CalleeGraphNode;
import static fr.jeremyhurel.utils.StringFormatter.escapeDot;
import static fr.jeremyhurel.utils.StringFormatter.sanitizeForDot;

public class CallGraphDotExportStrategy implements ExportStrategy<CallGraph> {

    @Override
    public void export(CallGraph data, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("digraph CallGraph {\n");
            writer.write("    rankdir=LR;\n");
            writer.write("    node [shape=box, style=filled, fillcolor=lightblue];\n");
            writer.write("    edge [color=black, arrowhead=open];\n\n");

            Set<String> writtenNodes = new HashSet<>();
            Set<String> writtenEdges = new HashSet<>();

            if (data.getRootNode() != null) {

                String rootNodeId = sanitizeForDot(data.getRootNode().getFullName());
                writer.write("    \"" + rootNodeId + "\" [fillcolor=lightgreen, label=\""
                        + escapeDot(data.getRootNode().getFullName()) + "\\n(ROOT)\"];\n");
                writtenNodes.add(rootNodeId);

                writeDotNode(writer, data.getRootNode(), writtenNodes, writtenEdges, new HashSet<>());
            } else {

                for (CalleeGraphNode node : data.getNodes().values()) {
                    writeDotNode(writer, node, writtenNodes, writtenEdges, new HashSet<>());
                }
            }

            writer.write("}\n");
        }
    }

    private void writeDotNode(FileWriter writer, CalleeGraphNode node, Set<String> writtenNodes,
            Set<String> writtenEdges, Set<CalleeGraphNode> visited) throws IOException {
        if (visited.contains(node)) {
            return;
        }
        visited.add(node);

        String nodeId = sanitizeForDot(node.getFullName());

        if (!writtenNodes.contains(nodeId)) {
            String label = escapeDot(node.getClassName()) + "\\n" + escapeDot(node.getMethodName());
            writer.write("    \"" + nodeId + "\" [label=\"" + label + "\"];\n");
            writtenNodes.add(nodeId);
        }

        for (CalleeGraphNode callee : node.getCallees()) {
            String calleeId = sanitizeForDot(callee.getFullName());
            String edge = nodeId + " -> " + calleeId;

            if (!writtenNodes.contains(calleeId)) {
                String calleeLabel = escapeDot(callee.getClassName()) + "\\n" + escapeDot(callee.getMethodName());
                writer.write("    \"" + calleeId + "\" [label=\"" + calleeLabel + "\"];\n");
                writtenNodes.add(calleeId);
            }

            if (!writtenEdges.contains(edge)) {
                writer.write("    \"" + nodeId + "\" -> \"" + calleeId + "\";\n");
                writtenEdges.add(edge);
            }

            writeDotNode(writer, callee, writtenNodes, writtenEdges, new HashSet<>(visited));
        }
    }

    @Override
    public String getFormatName() {
        return "DOT";
    }

    @Override
    public String getFileExtension() {
        return "dot";
    }
}
