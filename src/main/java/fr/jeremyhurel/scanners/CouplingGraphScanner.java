package fr.jeremyhurel.scanners;

import fr.jeremyhurel.models.CouplingGraph;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

/**
 * Scanner that analyzes method call coupling between classes.
 * According to the specification:
 * Couplage(A,B) = Number of method calls between classes A and B / Total method calls in application
 * 
 * A method call is counted when a method in class A calls a method in class B.
 */
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
        // Skip if we're filtering by root package and this doesn't match
        if (rootPackage != null && !type.getPackage().getQualifiedName().startsWith(rootPackage)) {
            return;
        }

        String className = type.getQualifiedName();
        
        // Skip external library classes
        if (isExternalLibraryClass(className)) {
            return;
        }

        // Ensure the node exists in the graph
        couplingGraph.getOrCreateNode(className);

        // Analyze method calls from this class to other classes
        analyzeMethodCalls(type, className);
    }

    /**
     * Analyzes method calls from the given type to other classes.
     * Only counts actual method invocations (calls), not field references or inheritance.
     */
    private void analyzeMethodCalls(CtType<?> type, String fromClassName) {
        // Process regular methods
        for (CtMethod<?> method : type.getMethods()) {
            if (method.getBody() != null) {
                analyzeMethodBody(method.getBody().getElements(e -> e instanceof CtInvocation), 
                                fromClassName);
            }
        }

        // Process constructors
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

    /**
     * Analyzes method invocations in a method body and counts calls between classes.
     */
    private void analyzeMethodBody(java.util.List<?> invocations, String fromClassName) {
        for (Object element : invocations) {
            CtInvocation<?> invocation = (CtInvocation<?>) element;
            
            if (invocation.getExecutable() != null && 
                invocation.getExecutable().getDeclaringType() != null) {
                
                String toClassName = invocation.getExecutable().getDeclaringType().getQualifiedName();
                
                // Skip if it's an external library class
                if (isExternalLibraryClass(toClassName)) {
                    continue;
                }
                
                // Skip if it's a self-call (same class)
                if (toClassName.equals(fromClassName)) {
                    continue;
                }
                
                // Ensure the target class node exists
                couplingGraph.getOrCreateNode(toClassName);
                
                // Count this as a method call relation
                couplingGraph.addMethodCall(fromClassName, toClassName);
            }
        }
    }

    /**
     * Checks if a class name belongs to an external library.
     * This filters out JDK classes, common frameworks, and third-party libraries.
     * 
     * @param typeName the fully qualified class name
     * @return true if the class is from an external library, false otherwise
     */
    private boolean isExternalLibraryClass(String typeName) {
        if (typeName == null) {
            return true;
        }

        // Primitive types
        if (isPrimitiveType(typeName)) {
            return true;
        }

        // Java standard library packages
        if (typeName.startsWith("java.") ||
            typeName.startsWith("javax.") ||
            typeName.startsWith("jdk.") ||
            typeName.startsWith("sun.") ||
            typeName.startsWith("com.sun.")) {
            return true;
        }

        // Common third-party libraries and frameworks
        if (typeName.startsWith("org.springframework.") ||
            typeName.startsWith("org.hibernate.") ||
            typeName.startsWith("org.apache.") ||
            typeName.startsWith("com.google.") ||
            typeName.startsWith("com.fasterxml.jackson.") ||
            typeName.startsWith("org.junit.") ||
            typeName.startsWith("org.mockito.") ||
            typeName.startsWith("org.slf4j.") ||
            typeName.startsWith("ch.qos.logback.") ||
            typeName.startsWith("org.eclipse.") ||
            typeName.startsWith("com.googlecode.lanterna.") ||
            typeName.startsWith("spoon.")) {
            return true;
        }

        // If rootPackage is specified, only include classes within that package
        if (rootPackage != null && !typeName.startsWith(rootPackage)) {
            return true;
        }

        return false;
    }

    /**
     * Checks if a type name is a primitive type.
     * 
     * @param typeName the type name
     * @return true if it's a primitive type, false otherwise
     */
    private boolean isPrimitiveType(String typeName) {
        return typeName.equals("int") ||
               typeName.equals("long") ||
               typeName.equals("double") ||
               typeName.equals("float") ||
               typeName.equals("boolean") ||
               typeName.equals("char") ||
               typeName.equals("byte") ||
               typeName.equals("short") ||
               typeName.equals("void");
    }
}
