package fr.jeremyhurel.models;

public class CouplingNode extends Node {

    private String className;
    private float couplingValue;

    public CouplingNode() {
    }

    public CouplingNode(String className, float couplingValue) {
        this.className = className;
        this.couplingValue = couplingValue;
    }

    public CouplingNode(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getCouplingValue() {
        return couplingValue;
    }

    public void setCouplingValue(float couplingValue) {
        this.couplingValue = couplingValue;
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
                ", couplingValue=" + String.format("%.6f", couplingValue) +
                '}';
    }

    public CouplingNode merge(CouplingNode other) {
        if (other == null) {
            return this;
        }

        return new CouplingNode(this.className + ", " + other.className, this.couplingValue + other.couplingValue);
    }
}
