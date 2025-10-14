package fr.jeremyhurel.models;

public class CouplingNode extends Node {

    private String className;

    public CouplingNode(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CouplingNode other = (CouplingNode) obj;
        return className != null ? className.equals(other.className) : other.className == null;
    }

    @Override
    public int hashCode() {
        return className != null ? className.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CouplingNode{" +
                "className='" + className + '\'' +
                '}';
    }
}
