package fr.jeremyhurel.utils.strategies;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.CouplingNode;
import static fr.jeremyhurel.utils.StringFormatter.escapeJson;

public class CouplingGraphJsonExportStrategy implements ExportStrategy<CouplingGraph> {

    @Override
    public void export(CouplingGraph data, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"couplingGraph\": {\n");
            writer.write("    \"specification\": \"Couplage(A,B) = Number of method calls between A and B / Total method calls in application\",\n");
            writer.write("    \"nodeCount\": " + data.getNodeCount() + ",\n");
            writer.write("    \"couplingCount\": " + data.getCouplingCount() + ",\n");
            writer.write("    \"totalMethodCalls\": " + data.getTotalMethodCalls() + ",\n");
            writer.write("    \"nodes\": [\n");

            boolean firstNode = true;
            for (CouplingNode node : data.getNodes().values()) {
                if (!firstNode) {
                    writer.write(",\n");
                }
                writer.write("      {\n");
                writer.write("        \"className\": \"" + escapeJson(node.getClassName()) + "\",\n");
                writer.write("        \"totalOutgoingCoupling\": " + String.format("%.6f", node.getCouplingValue()) + "\n");
                writer.write("      }");
                firstNode = false;
            }
            writer.write("\n    ],\n");

            writer.write("    \"couplings\": [\n");
            boolean firstCoupling = true;
            for (Map.Entry<String, Map<String, Integer>> entry : data.getCallCountMatrix().entrySet()) {
                String from = entry.getKey();
                for (Map.Entry<String, Integer> callEntry : entry.getValue().entrySet()) {
                    String to = callEntry.getKey();
                    int callCount = callEntry.getValue();
                    double normalizedCoupling = data.getCouplingWeight(from, to);

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

    @Override
    public String getFormatName() {
        return "JSON";
    }

    @Override
    public String getFileExtension() {
        return "json";
    }
}
