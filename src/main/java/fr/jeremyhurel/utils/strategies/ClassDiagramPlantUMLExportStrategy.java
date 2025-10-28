package fr.jeremyhurel.utils.strategies;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import fr.jeremyhurel.models.class_models.ClassAttr;
import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.models.class_models.ClassDiagramNode;
import fr.jeremyhurel.models.class_models.ClassMethod;
import fr.jeremyhurel.models.class_models.ClassRelationship;

public class ClassDiagramPlantUMLExportStrategy implements ExportStrategy<ClassDiagram> {

    @Override
    public void export(ClassDiagram data, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("@startuml\n");
            writer.write("!theme plain\n");
            writer.write("title Class Diagram\n\n");

            writer.write("skinparam class {\n");
            writer.write("  BackgroundColor<<interface>> LightBlue\n");
            writer.write("  BackgroundColor<<abstract>> LightYellow\n");
            writer.write("  BackgroundColor<<concrete>> LightGreen\n");
            writer.write("  BorderColor Black\n");
            writer.write("  ArrowColor Black\n");
            writer.write("}\n");
            writer.write("skinparam package {\n");
            writer.write("  BackgroundColor WhiteSmoke\n");
            writer.write("  BorderColor Gray\n");
            writer.write("  FontStyle bold\n");
            writer.write("}\n");
            writer.write("skinparam stereotypeCBackgroundColor Technology\n");
            writer.write("skinparam stereotypeIBackgroundColor Strategy\n");
            writer.write("skinparam stereotypeABackgroundColor Implementation\n\n");

            Set<String> writtenClasses = new HashSet<>();

            if (data.isPackageEncapsulation()) {

                java.util.Map<String, java.util.List<ClassDiagramNode>> classesByPackage = data.getClassesByPackage();

                for (java.util.Map.Entry<String, java.util.List<ClassDiagramNode>> entry : classesByPackage.entrySet()) {
                    String packageName = entry.getKey();
                    java.util.List<ClassDiagramNode> classesInPackage = entry.getValue();

                    writer.write("package \"" + packageName + "\" {\n");

                    for (ClassDiagramNode classNode : classesInPackage) {
                        writePlantUMLClass(writer, classNode, writtenClasses, "  ");
                    }

                    writer.write("}\n\n");
                }
            } else {

                for (ClassDiagramNode classNode : data.getClasses().values()) {
                    writePlantUMLClass(writer, classNode, writtenClasses, "");
                }
            }

            writer.write("\n");

            for (ClassRelationship relationship : data.getRelationships()) {
                writePlantUMLRelationship(writer, relationship);
            }

            writer.write("\n@enduml\n");
        }
    }

    private void writePlantUMLClass(FileWriter writer, ClassDiagramNode classNode,
            Set<String> writtenClasses, String indent) throws IOException {
        String className = sanitizeForPlantUML(classNode.getClassName());

        if (writtenClasses.contains(className)) {
            return;
        }
        writtenClasses.add(className);

        if (classNode.isInterface()) {
            writer.write(indent + "interface " + className + " <<interface>> {\n");
        } else if (classNode.isAbstract()) {
            writer.write(indent + "abstract class " + className + " <<abstract>> {\n");
        } else {
            writer.write(indent + "class " + className + " <<concrete>> {\n");
        }

        for (ClassAttr attribute : classNode.getAttributes()) {
            writer.write(indent + "  " + attribute.getVisibility() + " ");
            if (attribute.isStatic())
                writer.write("{static} ");
            if (attribute.isFinal())
                writer.write("{final} ");
            writer.write(attribute.getType() + " " + attribute.getName() + "\n");
        }

        if (!classNode.getAttributes().isEmpty() && !classNode.getMethods().isEmpty()) {
            writer.write(indent + "  --\n");
        }

        for (ClassMethod method : classNode.getMethods()) {
            writer.write(indent + "  " + method.getVisibility() + " ");
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

        writer.write(indent + "}\n\n");
    }

    private void writePlantUMLRelationship(FileWriter writer, ClassRelationship relationship)
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

    private String getSimpleClassName(String fullClassName) {
        int lastDot = fullClassName.lastIndexOf('.');
        return lastDot >= 0 ? fullClassName.substring(lastDot + 1) : fullClassName;
    }

    private String sanitizeForPlantUML(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }

    @Override
    public String getFormatName() {
        return "PlantUML";
    }

    @Override
    public String getFileExtension() {
        return "puml";
    }
}
