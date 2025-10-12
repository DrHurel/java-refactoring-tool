package fr.jeremyhurel.processors;

import spoon.Launcher;
import spoon.reflect.CtModel;
import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.scanners.MethodCallScanner;

public class CallGraphProcessor {

    private String projectPath;
    private String rootClassName;
    private String rootMethodName;

    public CallGraphProcessor(String projectPath) {
        this.projectPath = projectPath;
    }

    public CallGraphProcessor(String projectPath, String rootClassName, String rootMethodName) {
        this.projectPath = projectPath;
        this.rootClassName = rootClassName;
        this.rootMethodName = rootMethodName;
    }

    public CallGraph generateCallGraph() {
        CallGraph callGraph = new CallGraph();

        // Create Spoon launcher
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setNoClasspath(true);

        // Build the model
        CtModel model = launcher.buildModel();

        // Create and add the scanner
        MethodCallScanner scanner;
        if (rootClassName != null && rootMethodName != null) {
            scanner = new MethodCallScanner(callGraph, rootClassName, rootMethodName);
        } else {
            scanner = new MethodCallScanner(callGraph);
        }

        // Process the model
        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(type -> {
            type.getMethods().forEach(scanner::process);
        });

        return callGraph;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getRootClassName() {
        return rootClassName;
    }

    public void setRootClassName(String rootClassName) {
        this.rootClassName = rootClassName;
    }

    public String getRootMethodName() {
        return rootMethodName;
    }

    public void setRootMethodName(String rootMethodName) {
        this.rootMethodName = rootMethodName;
    }
}