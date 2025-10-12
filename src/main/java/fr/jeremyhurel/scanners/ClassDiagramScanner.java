package fr.jeremyhurel.scanners;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;
import spoon.reflect.reference.CtTypeReference;
import fr.jeremyhurel.models.*;
import java.util.Set;

public class ClassDiagramScanner extends AbstractProcessor<CtType<?>> {

    private ClassDiagram classDiagram;
    private String rootPackage;

    public ClassDiagramScanner(ClassDiagram classDiagram) {
        this.classDiagram = classDiagram;
    }

    public ClassDiagramScanner(ClassDiagram classDiagram, String rootPackage) {
        this.classDiagram = classDiagram;
        this.rootPackage = rootPackage;
    }

    @Override
    public void process(CtType<?> type) {
        // Skip if we're filtering by root package and this doesn't match
        if (rootPackage != null && !type.getPackage().getQualifiedName().startsWith(rootPackage)) {
            return;
        }

        String className = type.getSimpleName();
        String packageName = type.getPackage().getQualifiedName();

        ClassDiagramNode classNode = classDiagram.getOrCreateClass(className, packageName);

        // Set class type information
        classNode.setInterface(type.isInterface());
        classNode.setAbstract(type.hasModifier(ModifierKind.ABSTRACT));

        // Process superclass
        if (type.getSuperclass() != null) {
            String superClassName = type.getSuperclass().getQualifiedName();
            classNode.setSuperClass(superClassName);

            // Add inheritance relationship
            ClassRelationship inheritance = new ClassRelationship(
                    classNode.getFullName(),
                    superClassName,
                    ClassRelationship.RelationType.INHERITANCE);
            classDiagram.addRelationship(inheritance);
        }

        // Process interfaces
        for (CtTypeReference<?> interfaceRef : type.getSuperInterfaces()) {
            String interfaceName = interfaceRef.getQualifiedName();
            classNode.addInterface(interfaceName);

            // Add implementation relationship
            ClassRelationship implementation = new ClassRelationship(
                    classNode.getFullName(),
                    interfaceName,
                    ClassRelationship.RelationType.IMPLEMENTATION);
            classDiagram.addRelationship(implementation);
        }

        // Process fields/attributes
        for (CtField<?> field : type.getFields()) {
            processField(classNode, field);
        }

        // Process methods
        for (CtMethod<?> method : type.getMethods()) {
            processMethod(classNode, method);
        }

        // Process constructors (only for classes, not interfaces)
        if (type instanceof CtClass) {
            CtClass<?> ctClass = (CtClass<?>) type;
            for (CtConstructor<?> constructor : ctClass.getConstructors()) {
                processConstructor(classNode, constructor);
            }
        }
    }

    private void processField(ClassDiagramNode classNode, CtField<?> field) {
        String fieldName = field.getSimpleName();
        String fieldType = field.getType().getSimpleName();
        String visibility = getVisibility(field.getModifiers());

        ClassAttr attribute = new ClassAttr(fieldName, fieldType, visibility);
        attribute.setStatic(field.hasModifier(ModifierKind.STATIC));
        attribute.setFinal(field.hasModifier(ModifierKind.FINAL));

        classNode.addAttribute(attribute);

        // Check for composition/aggregation relationships
        if (!isPrimitiveType(fieldType)) {
            String targetClass = field.getType().getQualifiedName();
            ClassRelationship.RelationType relType = field.hasModifier(ModifierKind.FINAL)
                    ? ClassRelationship.RelationType.COMPOSITION
                    : ClassRelationship.RelationType.AGGREGATION;

            ClassRelationship relationship = new ClassRelationship(
                    classNode.getFullName(),
                    targetClass,
                    relType,
                    fieldName);
            classDiagram.addRelationship(relationship);
        }
    }

    private void processMethod(ClassDiagramNode classNode, CtMethod<?> method) {
        String methodName = method.getSimpleName();
        String returnType = method.getType().getSimpleName();
        String visibility = getVisibility(method.getModifiers());

        ClassMethod classMethod = new ClassMethod(methodName, returnType, visibility);
        classMethod.setStatic(method.hasModifier(ModifierKind.STATIC));
        classMethod.setAbstract(method.hasModifier(ModifierKind.ABSTRACT));

        // Add parameters
        for (CtParameter<?> param : method.getParameters()) {
            String paramType = param.getType().getSimpleName();
            String paramName = param.getSimpleName();
            classMethod.addParameter(paramType + " " + paramName);
        }

        classNode.addMethod(classMethod);
    }

    private void processConstructor(ClassDiagramNode classNode, CtConstructor<?> constructor) {
        String visibility = getVisibility(constructor.getModifiers());

        ClassMethod classMethod = new ClassMethod(classNode.getClassName(), "", visibility);
        classMethod.setConstructor(true);

        // Add parameters
        for (CtParameter<?> param : constructor.getParameters()) {
            String paramType = param.getType().getSimpleName();
            String paramName = param.getSimpleName();
            classMethod.addParameter(paramType + " " + paramName);
        }

        classNode.addMethod(classMethod);
    }

    private String getVisibility(Set<ModifierKind> modifiers) {
        if (modifiers.contains(ModifierKind.PUBLIC))
            return "+";
        if (modifiers.contains(ModifierKind.PRIVATE))
            return "-";
        if (modifiers.contains(ModifierKind.PROTECTED))
            return "#";
        return "~"; // package-private
    }

    private boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("long") || type.equals("double") ||
                type.equals("float") || type.equals("boolean") || type.equals("char") ||
                type.equals("byte") || type.equals("short") || type.equals("String");
    }
}