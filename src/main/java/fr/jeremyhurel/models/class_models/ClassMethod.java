package fr.jeremyhurel.models.class_models;

import java.util.ArrayList;
import java.util.List;

public class ClassMethod {
    private String name;
    private String returnType;
    private String visibility;
    private List<String> parameters;
    private boolean isStatic;
    private boolean isAbstract;
    private boolean isConstructor;

    public ClassMethod(String name, String returnType, String visibility) {
        this.name = name;
        this.returnType = returnType;
        this.visibility = visibility;
        this.parameters = new ArrayList<>();
        this.isStatic = false;
        this.isAbstract = false;
        this.isConstructor = false;
    }

    public String getName() {
        return name;
    }

    public String getReturnType() {
        return returnType;
    }

    public String getVisibility() {
        return visibility;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void addParameter(String parameter) {
        parameters.add(parameter);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    public boolean isAbstract() {
        return isAbstract;
    }

    public void setAbstract(boolean isAbstract) {
        this.isAbstract = isAbstract;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    public String getSignature() {
        StringBuilder sig = new StringBuilder();
        sig.append(visibility).append(" ");
        if (isStatic)
            sig.append("static ");
        if (isAbstract)
            sig.append("abstract ");
        if (!isConstructor)
            sig.append(returnType).append(" ");
        sig.append(name).append("(");
        sig.append(String.join(", ", parameters));
        sig.append(")");
        return sig.toString();
    }

    @Override
    public String toString() {
        return getSignature();
    }
}