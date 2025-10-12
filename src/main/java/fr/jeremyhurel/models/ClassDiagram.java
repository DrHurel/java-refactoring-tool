package fr.jeremyhurel.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class ClassDiagram {

    private Map<String, ClassDiagramNode> classes;
    private Set<ClassRelationship> relationships;
    private String rootPackage;

    public ClassDiagram() {
        this.classes = new HashMap<>();
        this.relationships = new HashSet<>();
    }

    public void addClass(ClassDiagramNode classNode) {
        classes.put(classNode.getFullName(), classNode);
    }

    public ClassDiagramNode getClass(String fullClassName) {
        return classes.get(fullClassName);
    }

    public ClassDiagramNode getOrCreateClass(String className, String packageName) {
        String fullName = packageName + "." + className;
        ClassDiagramNode node = classes.get(fullName);
        if (node == null) {
            node = new ClassDiagramNode(className, packageName);
            classes.put(fullName, node);
        }
        return node;
    }

    public void addRelationship(ClassRelationship relationship) {
        relationships.add(relationship);
    }

    public Set<ClassRelationship> getRelationships() {
        return relationships;
    }

    public Map<String, ClassDiagramNode> getClasses() {
        return classes;
    }

    public Set<String> getAllClassNames() {
        return classes.keySet();
    }

    public int getClassCount() {
        return classes.size();
    }

    public int getRelationshipCount() {
        return relationships.size();
    }

    public boolean isEmpty() {
        return classes.isEmpty();
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }
}