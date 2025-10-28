package fr.jeremyhurel.models.class_models;

public class ClassRelationship {
    public enum RelationType {
        INHERITANCE("extends"),
        IMPLEMENTATION("implements"),
        COMPOSITION("--*"),
        AGGREGATION("--o"),
        ASSOCIATION("--"),
        DEPENDENCY("..>");

        private final String umlSymbol;

        RelationType(String umlSymbol) {
            this.umlSymbol = umlSymbol;
        }

        public String getUmlSymbol() {
            return umlSymbol;
        }
    }

    private String sourceClass;
    private String targetClass;
    private RelationType type;
    private String label;

    public ClassRelationship(String sourceClass, String targetClass, RelationType type) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.type = type;
        this.label = "";
    }

    public ClassRelationship(String sourceClass, String targetClass, RelationType type, String label) {
        this.sourceClass = sourceClass;
        this.targetClass = targetClass;
        this.type = type;
        this.label = label;
    }

    public String getSourceClass() {
        return sourceClass;
    }

    public String getTargetClass() {
        return targetClass;
    }

    public RelationType getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        ClassRelationship that = (ClassRelationship) obj;
        return sourceClass.equals(that.sourceClass) &&
                targetClass.equals(that.targetClass) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return (sourceClass + targetClass + type.name()).hashCode();
    }

    @Override
    public String toString() {
        return sourceClass + " " + type.getUmlSymbol() + " " + targetClass +
                (label.isEmpty() ? "" : " : " + label);
    }
}