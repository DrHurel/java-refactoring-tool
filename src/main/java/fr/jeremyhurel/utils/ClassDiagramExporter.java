package fr.jeremyhurel.utils;

import fr.jeremyhurel.models.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class ClassDiagramExporter {

    private ClassDiagramExporter() {
        // Private constructor to hide implicit public one
    }

    public static void exportToJson(ClassDiagram classDiagram, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("{\n");
            writer.write("  \"classDiagram\": {\n");
            writer.write("    \"classCount\": " + classDiagram.getClassCount() + ",\n");
            writer.write("    \"relationshipCount\": " + classDiagram.getRelationshipCount() + ",\n");
            writer.write("    \"rootPackage\": \""
                    + escapeJson(classDiagram.getRootPackage() != null ? classDiagram.getRootPackage() : "") + "\",\n");
            writer.write("    \"classes\": [\n");

            boolean first = true;
            for (ClassDiagramNode classNode : classDiagram.getClasses().values()) {
                if (!first) {
                    writer.write(",\n");
                }
                writeClassJson(writer, classNode);
                first = false;
            }

            writer.write("\n    ],\n");
            writer.write("    \"relationships\": [\n");

            first = true;
            for (ClassRelationship relationship : classDiagram.getRelationships()) {
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

    private static void writeClassJson(FileWriter writer, ClassDiagramNode classNode) throws IOException {
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

    public static void exportToPlantUML(ClassDiagram classDiagram, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("title Class Diagram\n\n");

            Set<String> writtenClasses = new HashSet<>();

            // Write classes
            for (ClassDiagramNode classNode : classDiagram.getClasses().values()) {
                writePlantUMLClass(writer, classNode, writtenClasses);
            }

            writer.write("\n");

            // Write relationships
            for (ClassRelationship relationship : classDiagram.getRelationships()) {
                writePlantUMLRelationship(writer, relationship);
            }

            writer.write("\n@enduml\n");
        }
    }

    private static void writePlantUMLClass(FileWriter writer, ClassDiagramNode classNode, Set<String> writtenClasses)
            throws IOException {
        String className = sanitizeForPlantUML(classNode.getClassName());

        if (writtenClasses.contains(className)) {
            return;
        }
        writtenClasses.add(className);

        // Write class declaration
        if (classNode.isInterface()) {
            writer.write("interface " + className + " {\n");
        } else if (classNode.isAbstract()) {
            writer.write("abstract class " + className + " {\n");
        } else {
            writer.write("class " + className + " {\n");
        }

        // Write attributes
        for (ClassAttr attribute : classNode.getAttributes()) {
            writer.write("  " + attribute.getVisibility() + " ");
            if (attribute.isStatic())
                writer.write("{static} ");
            if (attribute.isFinal())
                writer.write("{final} ");
            writer.write(attribute.getType() + " " + attribute.getName() + "\n");
        }

        if (!classNode.getAttributes().isEmpty() && !classNode.getMethods().isEmpty()) {
            writer.write("  --\n");
        }

        // Write methods
        for (ClassMethod method : classNode.getMethods()) {
            writer.write("  " + method.getVisibility() + " ");
            if (method.isStatic())
                writer.write("{static} ");
            if (method.isAbstract())
                writer.write("{abstract} ");
            if (method.isConstructor()) {
                writer.write("<<constructor>> ");
            }
            if (!method.isConstructor()) {
                writer.write(method.getReturnType() + " ");
            }
            writer.write(method.getName() + "(");
            writer.write(String.join(", ", method.getParameters()));
            writer.write(")\n");
        }

        writer.write("}\n\n");
    }

    private static void writePlantUMLRelationship(FileWriter writer, ClassRelationship relationship)
            throws IOException {
        String source = sanitizeForPlantUML(getSimpleClassName(relationship.getSourceClass()));
        String target = sanitizeForPlantUML(getSimpleClassName(relationship.getTargetClass()));

        String relationSymbol = switch (relationship.getType()) {
            case INHERITANCE -> " --|> ";
            case IMPLEMENTATION -> " ..|> ";
            case COMPOSITION -> " *-- ";
            case AGGREGATION -> " o-- ";
            case ASSOCIATION -> " -- ";
            case DEPENDENCY -> " ..> ";
        };

        writer.write(source + relationSymbol + target);
        if (!relationship.getLabel().isEmpty()) {
            writer.write(" : " + relationship.getLabel());
        }
        writer.write("\n");
    }

    private static String getSimpleClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot >= 0 ? fullClassName.substring(lastDot + 1) : fullClassName;
    }

    private static String sanitizeForPlantUML(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    private static String escapeJson(String value) {
        if (value == null)
            return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}