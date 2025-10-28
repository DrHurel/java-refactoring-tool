package fr.jeremyhurel.utils;

import java.io.IOException;

import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.utils.strategies.ClassDiagramJsonExportStrategy;
import fr.jeremyhurel.utils.strategies.ClassDiagramPlantUMLExportStrategy;
import fr.jeremyhurel.utils.strategies.ExportStrategy;

public class ClassDiagramExporter {

    private ClassDiagramExporter() {

    }

    public static void exportToJson(ClassDiagram classDiagram, String filePath) throws IOException {
        ExportStrategy<ClassDiagram> strategy = new ClassDiagramJsonExportStrategy();
        strategy.export(classDiagram, filePath);
    }

    public static void exportToPlantUML(ClassDiagram classDiagram, String filePath) throws IOException {
        ExportStrategy<ClassDiagram> strategy = new ClassDiagramPlantUMLExportStrategy();
        strategy.export(classDiagram, filePath);
    }

    public static void export(ClassDiagram classDiagram, ExportStrategy<ClassDiagram> strategy, String filePath)
            throws IOException {
        strategy.export(classDiagram, filePath);
    }
}
