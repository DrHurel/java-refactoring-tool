package fr.jeremyhurel.utils.strategies;

import java.io.FileWriter;
import java.io.IOException;

import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.models.class_models.ClassDiagramNode;
import fr.jeremyhurel.models.class_models.ClassRelationship;
import static fr.jeremyhurel.utils.StringFormatter.escapeJson;

public class ClassDiagramJsonExportStrategy implements ExportStrategy<ClassDiagram> {

    @Override
    public void export(ClassDiagram data, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"classDiagram\": {\n");
            writer.write("    \"classCount\": " + data.getClassCount() + ",\n");
            writer.write("    \"packageCount\": " + data.getPackageCount() + ",\n");
            writer.write("    \"relationshipCount\": " + data.getRelationshipCount() + ",\n");
            writer.write("    \"rootPackage\": \""
                    + escapeJson(data.getRootPackage() != null ? data.getRootPackage() : "") + "\",\n");
            writer.write("    \"packageEncapsulation\": " + data.isPackageEncapsulation() + ",\n");
            writer.write("    \"classes\": [\n");

            boolean first = true;
            for (ClassDiagramNode classNode : data.getClasses().values()) {
                if (!first) {
                    writer.write(",\n");
                }
                writeClassJson(writer, classNode);
                first = false;
            }

            writer.write("\n    ],\n");
            writer.write("    \"relationships\": [\n");

            first = true;
            for (ClassRelationship relationship : data.getRelationships()) {
                if (!first) {
                    writer.write(",\n");
                }
                writer.write("      {\n");
                writer.write("        \"source\": \"" + escapeJson(relationship.getSourceClass()) + "\",\n");
                writer.write("        \"target\": \"" + escapeJson(relationship.getTargetClass()) + "\",\n");
                writer.write("        \"type\": \"" + relationship.getType().name() + "\",\n");
                writer.write("        \"label\": \"" + escapeJson(relationship.getLabel()) + "\"\n");
                writer.write("      }");
                first = false;
            }

            writer.write("\n    ]\n");
            writer.write("  }\n");
            writer.write("}\n");
        }
    }

    private void writeClassJson(FileWriter writer, ClassDiagramNode classNode) throws IOException {
        writer.write("      {\n");
        writer.write("        \"className\": \"" + escapeJson(classNode.getClassName()) + "\",\n");
        writer.write("        \"packageName\": \"" + escapeJson(classNode.getPackageName()) + "\",\n");
        writer.write("        \"fullName\": \"" + escapeJson(classNode.getFullName()) + "\",\n");
        writer.write("        \"isInterface\": " + classNode.isInterface() + ",\n");
        writer.write("        \"isAbstract\": " + classNode.isAbstract() + ",\n");
        writer.write("        \"superClass\": \""
                + escapeJson(classNode.getSuperClass() != null ? classNode.getSuperClass() : "") + "\",\n");
        writer.write("        \"attributeCount\": " + classNode.getAttributes().size() + ",\n");
        writer.write("        \"methodCount\": " + classNode.getMethods().size() + "\n");
        writer.write("      }");
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
