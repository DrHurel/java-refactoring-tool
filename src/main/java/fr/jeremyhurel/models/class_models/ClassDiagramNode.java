package fr.jeremyhurel.models.class_models;

import java.util.ArrayList;
import java.util.List;

import fr.jeremyhurel.models.Node;

public class ClassDiagramNode extends Node {

    private String className;
    private String packageName;
    private List<ClassAttr> attributes;
    private List<ClassMethod> methods;
    private List<ClassRelationship> relationships;
    private boolean isInterface;
    private boolean isAbstract;
    private String superClass;
    private List<String> interfaces;

    public ClassDiagramNode(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
        this.attributes = new ArrayList<>();
        this.methods = new ArrayList<>();
        this.relationships = new ArrayList<>();
        this.interfaces = new ArrayList<>();
        this.isInterface = false;
        this.isAbstract = false;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getFullName() {
        return packageName + "." + className;
    }

    public List<ClassAttr> getAttributes() {
        return attributes;
    }

    public List<ClassMethod> getMethods() {
        return methods;
    }

    public List<ClassRelationship> getRelationships() {
        return relationships;
    }

    public void addAttribute(ClassAttr attribute) {
        attributes.add(attribute);
    }

    public void addMethod(ClassMethod method) {
        methods.add(method);
    }

    public void addRelationship(ClassRelationship relationship) {
        relationships.add(relationship);
    }

    public boolean isInterface() {
        return isInterface;
    }

    public void setInterface(boolean isInterface) {
        this.isInterface = isInterface;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public String getSuperClass() {
        return superClass;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public List<String> getInterfaces() {
        return interfaces;
    }

    public void addInterface(String interfaceName) {
        interfaces.add(interfaceName);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ClassDiagramNode that = (ClassDiagramNode) obj;
        return getFullName().equals(that.getFullName());
    }

    @Override
    public int hashCode() {
        return getFullName().hashCode();
    }

    @Override
    public String toString() {
        return getFullName();
    }
}
