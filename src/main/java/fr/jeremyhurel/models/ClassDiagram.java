package fr.jeremyhurel.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ClassDiagram {

    private Map<String, ClassDiagramNode> classes;
    private Set<ClassRelationship> relationships;
    private String rootPackage;
    private boolean packageEncapsulation;

    public ClassDiagram() {
        this.classes = new HashMap<>();
        this.relationships = new HashSet<>();
        this.packageEncapsulation = false;
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

    public boolean isPackageEncapsulation() {
        return packageEncapsulation;
    }

    public void setPackageEncapsulation(boolean packageEncapsulation) {
        this.packageEncapsulation = packageEncapsulation;
    }

    /**
     * Returns all unique package names in the diagram.
     * 
     * @return set of package names
     */
    public Set<String> getAllPackages() {
        return classes.values().stream()
                .map(ClassDiagramNode::getPackageName)
                .collect(Collectors.toSet());
    }

    /**
     * Returns all classes belonging to a specific package.
     * 
     * @param packageName the package name
     * @return list of class nodes in that package
     */
    public List<ClassDiagramNode> getClassesInPackage(String packageName) {
        return classes.values().stream()
                .filter(node -> node.getPackageName().equals(packageName))
                .collect(Collectors.toList());
    }

    /**
     * Groups classes by package.
     * 
     * @return map of package name to list of classes
     */
    public Map<String, List<ClassDiagramNode>> getClassesByPackage() {
        return classes.values().stream()
                .collect(Collectors.groupingBy(ClassDiagramNode::getPackageName));
    }

    /**
     * Returns the number of packages in the diagram.
     * 
     * @return package count
     */
    public int getPackageCount() {
        return getAllPackages().size();
    }
}