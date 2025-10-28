package fr.jeremyhurel.models.class_models;

public class ClassAttr {
    private String name;
    private String type;
    private String visibility;
    private boolean isStatic;
    private boolean isFinal;

    public ClassAttr(String name, String type, String visibility) {
        this.name = name;
        this.type = type;
        this.visibility = visibility;
        this.isStatic = false;
        this.isFinal = false;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getVisibility() {
        return visibility;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public String getSignature() {
        StringBuilder sig = new StringBuilder();
        sig.append(visibility).append(" ");
        if (isStatic)
            sig.append("static ");
        if (isFinal)
            sig.append("final ");
        sig.append(type).append(" ").append(name);
        return sig.toString();
    }

    @Override
    public String toString() {
        return getSignature();
    }
}
