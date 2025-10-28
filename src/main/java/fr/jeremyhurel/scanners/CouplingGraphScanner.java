package fr.jeremyhurel.scanners;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.utils.ExternalLibraryFilter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

public class CouplingGraphScanner extends AbstractProcessor<CtType<?>> {

    private final CouplingGraph couplingGraph;
    private String rootPackage;

    public CouplingGraphScanner(CouplingGraph couplingGraph) {
        this.couplingGraph = couplingGraph;
    }

    public CouplingGraphScanner(CouplingGraph couplingGraph, String rootPackage) {
        this.couplingGraph = couplingGraph;
        this.rootPackage = rootPackage;
    }

    @Override
    public void process(CtType<?> type) {

        if (rootPackage != null && !type.getPackage().getQualifiedName().startsWith(rootPackage)) {
            return;
        }

        String className = type.getQualifiedName();

        if (isExternalLibraryClass(className)) {
            return;
        }

        couplingGraph.getOrCreateNode(className);

        analyzeMethodCalls(type, className);
    }

    private void analyzeMethodCalls(CtType<?> type, String fromClassName) {

        for (CtMethod<?> method : type.getMethods()) {
            if (method.getBody() != null) {
                analyzeMethodBody(method.getBody().getElements(e -> e instanceof CtInvocation),
                                fromClassName);
            }
        }

        if (type instanceof CtClass) {
            CtClass<?> ctClass = (CtClass<?>) type;
            for (CtConstructor<?> constructor : ctClass.getConstructors()) {
                if (constructor.getBody() != null) {
                    analyzeMethodBody(constructor.getBody().getElements(e -> e instanceof CtInvocation),
                                    fromClassName);
                }
            }
        }
    }

    private void analyzeMethodBody(java.util.List<?> invocations, String fromClassName) {
        for (Object element : invocations) {
            CtInvocation<?> invocation = (CtInvocation<?>) element;

            if (invocation.getExecutable() != null &&
                invocation.getExecutable().getDeclaringType() != null) {

                String toClassName = invocation.getExecutable().getDeclaringType().getQualifiedName();

                if (isExternalLibraryClass(toClassName)) {
                    continue;
                }

                if (toClassName.equals(fromClassName)) {
                    continue;
                }

                couplingGraph.getOrCreateNode(toClassName);

                couplingGraph.addMethodCall(fromClassName, toClassName);
            }
        }
    }

    private boolean isExternalLibraryClass(String typeName) {

        return ExternalLibraryFilter.isExternalLibraryClass(typeName, rootPackage);
    }
}
