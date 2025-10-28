package fr.jeremyhurel.scanners;

import java.util.Set;

import fr.jeremyhurel.models.class_models.ClassAttr;
import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.models.class_models.ClassDiagramNode;
import fr.jeremyhurel.models.class_models.ClassMethod;
import fr.jeremyhurel.models.class_models.ClassRelationship;
import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtTypeReference;

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

        if (rootPackage != null && !type.getPackage().getQualifiedName().startsWith(rootPackage)) {
            return;
        }

        String className = type.getSimpleName();
        String packageName = type.getPackage().getQualifiedName();

        ClassDiagramNode classNode = classDiagram.getOrCreateClass(className, packageName);

        classNode.setInterface(type.isInterface());
        classNode.setAbstract(type.hasModifier(ModifierKind.ABSTRACT));

        if (type.getSuperclass() != null) {
            String superClassName = type.getSuperclass().getQualifiedName();
            classNode.setSuperClass(superClassName);

            ClassRelationship inheritance = new ClassRelationship(
                    classNode.getFullName(),
                    superClassName,
                    ClassRelationship.RelationType.INHERITANCE);
            classDiagram.addRelationship(inheritance);
        }

        for (CtTypeReference<?> interfaceRef : type.getSuperInterfaces()) {
            String interfaceName = interfaceRef.getQualifiedName();
            classNode.addInterface(interfaceName);

            ClassRelationship implementation = new ClassRelationship(
                    classNode.getFullName(),
                    interfaceName,
                    ClassRelationship.RelationType.IMPLEMENTATION);
            classDiagram.addRelationship(implementation);
        }

        for (CtField<?> field : type.getFields()) {
            processField(classNode, field);
        }

        for (CtMethod<?> method : type.getMethods()) {
            processMethod(classNode, method);
        }

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
        return "~";
    }

    private boolean isPrimitiveType(String type) {
        return type.equals("int") || type.equals("long") || type.equals("double") ||
                type.equals("float") || type.equals("boolean") || type.equals("char") ||
                type.equals("byte") || type.equals("short") || type.equals("String");
    }
}