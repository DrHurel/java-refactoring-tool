package fr.jeremyhurel.processors;

import spoon.Launcher;
import spoon.reflect.CtModel;
import fr.jeremyhurel.models.ClassDiagram;
import fr.jeremyhurel.scanners.ClassDiagramScanner;

public class ClassDiagramProcessor {

    private String projectPath;
    private String rootPackage;

    public ClassDiagramProcessor(String projectPath) {
        this.projectPath = projectPath;
    }

    public ClassDiagramProcessor(String projectPath, String rootPackage) {
        this.projectPath = projectPath;
        this.rootPackage = rootPackage;
    }

    public ClassDiagram generateClassDiagram() {
        ClassDiagram classDiagram = new ClassDiagram();

        // Create Spoon launcher
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setNoClasspath(true);

        // Build the model
        CtModel model = launcher.buildModel();

        // Create and add the scanner
        ClassDiagramScanner scanner;
        if (rootPackage != null) {
            scanner = new ClassDiagramScanner(classDiagram, rootPackage);
            classDiagram.setRootPackage(rootPackage);
        } else {
            scanner = new ClassDiagramScanner(classDiagram);
        }

        // Process the model
        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(scanner::process);

        return classDiagram;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }
}