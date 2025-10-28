package fr.jeremyhurel.utils.strategies;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.CouplingNode;
import static fr.jeremyhurel.utils.StringFormatter.escapeDot;
import static fr.jeremyhurel.utils.StringFormatter.sanitizeForDot;

public class CouplingGraphDotExportStrategy implements ExportStrategy<CouplingGraph> {

    @Override
    public void export(CouplingGraph data, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("digraph CouplingGraph {\n");
            writer.write("    rankdir=LR;\n");
            writer.write("    node [shape=box, style=filled, fillcolor=lightblue];\n");
            writer.write("    edge [color=gray];\n\n");

            for (CouplingNode node : data.getNodes().values()) {
                String nodeId = sanitizeForDot(node.getClassName());
                String label = getSimpleClassName(node.getClassName());
                float totalCoupling = node.getCouplingValue();

                String tooltip = String.format("Total outgoing coupling: %.6f", totalCoupling);

                writer.write("    \"" + nodeId + "\" [label=\"" + escapeDot(label) + "\", tooltip=\""
                            + escapeDot(tooltip) + "\"];\n");
            }

            writer.write("\n");

            for (Map.Entry<String, Map<String, Integer>> entry : data.getCallCountMatrix().entrySet()) {
                String from = entry.getKey();
                String fromId = sanitizeForDot(from);

                for (Map.Entry<String, Integer> callEntry : entry.getValue().entrySet()) {
                    String to = callEntry.getKey();
                    String toId = sanitizeForDot(to);
                    int callCount = callEntry.getValue();
                    double normalizedCoupling = data.getCouplingWeight(from, to);

                    String edgeStyle = getEdgeStyle(normalizedCoupling);
                    String edgeColor = getEdgeColor(normalizedCoupling);
                    String label = "calls=" + callCount + "\\ncoupling=" + String.format("%.4f", normalizedCoupling);

                    writer.write("    \"" + fromId + "\" -> \"" + toId + "\" [label=\""
                            + label + "\", " + edgeStyle + ", color=\"" + edgeColor
                            + "\"];\n");
                }
            }

            writer.write("}\n");
        }
    }

    private String getSimpleClassName(String fullClassName) {
        int lastDotIndex = fullClassName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < fullClassName.length() - 1) {
            return fullClassName.substring(lastDotIndex + 1);
        }
        return fullClassName;
    }

    private String getEdgeStyle(double normalizedCoupling) {
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

    private String getEdgeColor(double normalizedCoupling) {
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

    @Override
    public String getFormatName() {
        return "DOT";
    }

    @Override
    public String getFileExtension() {
        return "dot";
    }
}
