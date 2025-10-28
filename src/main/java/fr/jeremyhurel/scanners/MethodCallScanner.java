package fr.jeremyhurel.scanners;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.visitor.filter.TypeFilter;
import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.models.CalleeGraphNode;

public class MethodCallScanner extends AbstractProcessor<CtMethod<?>> {

    private CallGraph callGraph;
    private String rootClassName;
    private String rootMethodName;

    public MethodCallScanner(CallGraph callGraph) {
        this.callGraph = callGraph;
    }

    public MethodCallScanner(CallGraph callGraph, String rootClassName, String rootMethodName) {
        this.callGraph = callGraph;
        this.rootClassName = rootClassName;
        this.rootMethodName = rootMethodName;
    }

    @Override
    public void process(CtMethod<?> method) {

        if (rootClassName != null && rootMethodName != null) {
            String currentClassName = method.getDeclaringType().getQualifiedName();
            String currentMethodName = method.getSimpleName();

            if (!currentClassName.equals(rootClassName) || !currentMethodName.equals(rootMethodName)) {
                return;
            }
        }

        String className = method.getDeclaringType().getQualifiedName();
        String methodName = method.getSimpleName();
        String signature = method.getSignature();
        int lineNumber = method.getPosition() != null ? method.getPosition().getLine() : 0;

        CalleeGraphNode currentNode = callGraph.getOrCreateNode(className, methodName, signature, lineNumber);

        if (callGraph.getRootNode() == null ||
                (rootClassName != null && rootMethodName != null &&
                        className.equals(rootClassName) && methodName.equals(rootMethodName))) {
            callGraph.setRootNode(currentNode);
        }

        method.getElements(new TypeFilter<>(CtInvocation.class)).forEach(invocation -> {
            try {
                String targetClassName = invocation.getExecutable().getDeclaringType().getQualifiedName();
                String targetMethodName = invocation.getExecutable().getSimpleName();
                String targetSignature = invocation.getExecutable().getSignature();
                int targetLineNumber = invocation.getPosition() != null ? invocation.getPosition().getLine() : 0;

                CalleeGraphNode targetNode = callGraph.getOrCreateNode(
                        targetClassName, targetMethodName, targetSignature, targetLineNumber);

                currentNode.addCallee(targetNode);
            } catch (Exception e) {

                System.err.println("Could not resolve method call: " + invocation);
            }
        });
    }
}