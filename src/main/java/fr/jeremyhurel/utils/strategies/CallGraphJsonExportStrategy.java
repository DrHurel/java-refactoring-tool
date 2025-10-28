package fr.jeremyhurel.utils.strategies;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.models.CalleeGraphNode;
import static fr.jeremyhurel.utils.StringFormatter.escapeJson;

public class CallGraphJsonExportStrategy implements ExportStrategy<CallGraph> {

    @Override
    public void export(CallGraph data, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"callGraph\": {\n");
            writer.write("    \"nodeCount\": " + data.getNodeCount() + ",\n");
            writer.write("    \"rootNode\": ");

            if (data.getRootNode() != null) {
                writeNodeJson(writer, data.getRootNode(), "    ", new HashSet<>());
            } else {
                writer.write("null");
            }

            writer.write(",\n    \"allNodes\": [\n");
            boolean first = true;
            for (CalleeGraphNode node : data.getNodes().values()) {
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

    private void writeNodeJson(FileWriter writer, CalleeGraphNode node, String indent,
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
    }

    @Override
    public String getFormatName() {
        return "JSON";
    }

    @Override
    public String getFileExtension() {
        return "json";
    }
}
