package fr.jeremyhurel.models;

import java.util.ArrayList;
import java.util.List;

public class Module {

    private int id;
    private String name;
    private List<String> classes;
    private double cohesion;

    public Module(int id, String name) {
        this.id = id;
        this.name = name;
        this.classes = new ArrayList<>();
        this.cohesion = 0.0;
    }

    public void addClass(String className) {
        if (!classes.contains(className)) {
            classes.add(className);
        }
    }

    public void addClasses(List<String> classNames) {
        for (String className : classNames) {
            addClass(className);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getClasses() {
        return new ArrayList<>(classes);
    }

    public int getSize() {
        return classes.size();
    }

    public double getCohesion() {
        return cohesion;
    }

    public void setCohesion(double cohesion) {
        this.cohesion = cohesion;
    }

    public boolean containsClass(String className) {
        return classes.contains(className);
    }

    @Override
    public String toString() {
        return "Module{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", classes=" + classes.size() +
                ", cohesion=" + String.format("%.4f", cohesion) +
                '}';
    }
}
