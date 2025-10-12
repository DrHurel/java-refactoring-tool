package fr.jeremyhurel.models;

import java.util.List;
import java.util.ArrayList;

public class ProjectStatistics {

    // Basic counts
    private int totalClasses;
    private int totalLinesOfCode;
    private int totalMethods;
    private int totalPackages;

    // Averages
    private double averageMethodsPerClass;
    private double averageLinesPerMethod;
    private double averageAttributesPerClass;

    // Top percentiles
    private List<ClassStats> top10PercentClassesByMethods;
    private List<ClassStats> top10PercentClassesByAttributes;
    private List<ClassStats> classesInBothTopCategories;

    // Custom threshold
    private List<ClassStats> classesWithMoreThanXMethods;
    private int methodThreshold;

    // Method statistics
    private List<MethodStats> top10PercentMethodsByLines;
    private int maxParametersInApplication;

    public ProjectStatistics() {
        this.top10PercentClassesByMethods = new ArrayList<>();
        this.top10PercentClassesByAttributes = new ArrayList<>();
        this.classesInBothTopCategories = new ArrayList<>();
        this.classesWithMoreThanXMethods = new ArrayList<>();
        this.top10PercentMethodsByLines = new ArrayList<>();
    }

    // Getters and setters
    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getTotalLinesOfCode() {
        return totalLinesOfCode;
    }

    public void setTotalLinesOfCode(int totalLinesOfCode) {
        this.totalLinesOfCode = totalLinesOfCode;
    }

    public int getTotalMethods() {
        return totalMethods;
    }

    public void setTotalMethods(int totalMethods) {
        this.totalMethods = totalMethods;
    }

    public int getTotalPackages() {
        return totalPackages;
    }

    public void setTotalPackages(int totalPackages) {
        this.totalPackages = totalPackages;
    }

    public double getAverageMethodsPerClass() {
        return averageMethodsPerClass;
    }

    public void setAverageMethodsPerClass(double averageMethodsPerClass) {
        this.averageMethodsPerClass = averageMethodsPerClass;
    }

    public double getAverageLinesPerMethod() {
        return averageLinesPerMethod;
    }

    public void setAverageLinesPerMethod(double averageLinesPerMethod) {
        this.averageLinesPerMethod = averageLinesPerMethod;
    }

    public double getAverageAttributesPerClass() {
        return averageAttributesPerClass;
    }

    public void setAverageAttributesPerClass(double averageAttributesPerClass) {
        this.averageAttributesPerClass = averageAttributesPerClass;
    }

    public List<ClassStats> getTop10PercentClassesByMethods() {
        return top10PercentClassesByMethods;
    }

    public void setTop10PercentClassesByMethods(List<ClassStats> top10PercentClassesByMethods) {
        this.top10PercentClassesByMethods = top10PercentClassesByMethods;
    }

    public List<ClassStats> getTop10PercentClassesByAttributes() {
        return top10PercentClassesByAttributes;
    }

    public void setTop10PercentClassesByAttributes(List<ClassStats> top10PercentClassesByAttributes) {
        this.top10PercentClassesByAttributes = top10PercentClassesByAttributes;
    }

    public List<ClassStats> getClassesInBothTopCategories() {
        return classesInBothTopCategories;
    }

    public void setClassesInBothTopCategories(List<ClassStats> classesInBothTopCategories) {
        this.classesInBothTopCategories = classesInBothTopCategories;
    }

    public List<ClassStats> getClassesWithMoreThanXMethods() {
        return classesWithMoreThanXMethods;
    }

    public void setClassesWithMoreThanXMethods(List<ClassStats> classesWithMoreThanXMethods) {
        this.classesWithMoreThanXMethods = classesWithMoreThanXMethods;
    }

    public int getMethodThreshold() {
        return methodThreshold;
    }

    public void setMethodThreshold(int methodThreshold) {
        this.methodThreshold = methodThreshold;
    }

    public List<MethodStats> getTop10PercentMethodsByLines() {
        return top10PercentMethodsByLines;
    }

    public void setTop10PercentMethodsByLines(List<MethodStats> top10PercentMethodsByLines) {
        this.top10PercentMethodsByLines = top10PercentMethodsByLines;
    }

    public int getMaxParametersInApplication() {
        return maxParametersInApplication;
    }

    public void setMaxParametersInApplication(int maxParametersInApplication) {
        this.maxParametersInApplication = maxParametersInApplication;
    }
}