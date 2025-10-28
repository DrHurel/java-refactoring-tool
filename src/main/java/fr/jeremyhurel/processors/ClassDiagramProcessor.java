package fr.jeremyhurel.processors;

import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.scanners.ClassDiagramScanner;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class ClassDiagramProcessor extends BaseProcessor {

    private String rootPackage;

    public ClassDiagramProcessor(String projectPath) {
        super(projectPath);
    }

    public ClassDiagramProcessor(String projectPath, String rootPackage) {
        super(projectPath);
        this.rootPackage = rootPackage;
    }

    public ClassDiagram generateClassDiagram() {
        ClassDiagram classDiagram = new ClassDiagram();

        Launcher launcher = createLauncher();
        CtModel model = buildModel(launcher);

        ClassDiagramScanner scanner = createScanner(classDiagram);

        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(scanner::process);

        return classDiagram;
    }

    private ClassDiagramScanner createScanner(ClassDiagram classDiagram) {
        if (rootPackage != null) {
            classDiagram.setRootPackage(rootPackage);
            return new ClassDiagramScanner(classDiagram, rootPackage);
        }
        return new ClassDiagramScanner(classDiagram);
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }
}