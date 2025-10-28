package fr.jeremyhurel.processors;

import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.scanners.MethodCallScanner;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class CallGraphProcessor extends BaseProcessor {

    private String rootClassName;
    private String rootMethodName;

    public CallGraphProcessor(String projectPath) {
        super(projectPath);
    }

    public CallGraphProcessor(String projectPath, String rootClassName, String rootMethodName) {
        super(projectPath);
        this.rootClassName = rootClassName;
        this.rootMethodName = rootMethodName;
    }

    public CallGraph generateCallGraph() {
        CallGraph callGraph = new CallGraph();

        Launcher launcher = createLauncher();
        CtModel model = buildModel(launcher);

        MethodCallScanner scanner = createScanner(callGraph);

        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(type -> {
            type.getMethods().forEach(scanner::process);
        });

        return callGraph;
    }

    private MethodCallScanner createScanner(CallGraph callGraph) {
        if (rootClassName != null && rootMethodName != null) {
            return new MethodCallScanner(callGraph, rootClassName, rootMethodName);
        }
        return new MethodCallScanner(callGraph);
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