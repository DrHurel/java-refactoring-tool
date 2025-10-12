package fr.jeremyhurel.test;

import fr.jeremyhurel.processors.ClassDiagramProcessor;
import fr.jeremyhurel.models.ClassDiagram;
import fr.jeremyhurel.utils.ClassDiagramExporter;

public class TestClassDiagramExport {
    public static void main(String[] args) {
        try {
            // Generate class diagram for the test sample
            ClassDiagramProcessor processor = new ClassDiagramProcessor("./src/main/java");
            ClassDiagram classDiagram = processor.generateClassDiagram();

            // Export to PlantUML and JSON formats
            ClassDiagramExporter.exportToPlantUML(classDiagram, "./test-classdiagram.puml");
            ClassDiagramExporter.exportToJson(classDiagram, "./test-classdiagram.json");

            System.out.println("Class diagram exported successfully!");
            System.out.println("Classes found: " + classDiagram.getClassCount());
            System.out.println("Relationships found: " + classDiagram.getRelationshipCount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}