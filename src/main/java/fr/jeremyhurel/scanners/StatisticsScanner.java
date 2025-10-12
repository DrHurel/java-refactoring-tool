package fr.jeremyhurel.scanners;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.*;
import spoon.reflect.code.CtStatement;
import fr.jeremyhurel.models.*;
import java.util.*;

public class StatisticsScanner extends AbstractProcessor<CtType<?>> {

    private Map<String, ClassStats> classStatsMap;
    private List<MethodStats> methodStatsList;
    private Set<String> packages;

    public StatisticsScanner() {
        this.classStatsMap = new HashMap<>();
        this.methodStatsList = new ArrayList<>();
        this.packages = new HashSet<>();
    }

    @Override
    public void process(CtType<?> type) {
        String className = type.getSimpleName();
        String packageName = type.getPackage().getQualifiedName();

        // Add package to set
        packages.add(packageName);

        // Create class statistics
        ClassStats classStats = new ClassStats(className, packageName);
        classStats.setInterface(type.isInterface());
        classStats.setAbstract(type.hasModifier(ModifierKind.ABSTRACT));

        // Count attributes
        classStats.setAttributeCount(type.getFields().size());

        // Count methods and analyze them
        int methodCount = 0;
        int totalClassLinesOfCode = 0;

        // Process methods
        for (CtMethod<?> method : type.getMethods()) {
            methodCount++;
            MethodStats methodStats = processMethod(method, className, packageName);
            methodStatsList.add(methodStats);
            totalClassLinesOfCode += methodStats.getLinesOfCode();
        }

        // Process constructors (only for classes)
        if (type instanceof CtClass) {
            CtClass<?> ctClass = (CtClass<?>) type;
            for (CtConstructor<?> constructor : ctClass.getConstructors()) {
                methodCount++;
                MethodStats constructorStats = processConstructor(constructor, className, packageName);
                methodStatsList.add(constructorStats);
                totalClassLinesOfCode += constructorStats.getLinesOfCode();
            }
        }

        classStats.setMethodCount(methodCount);
        classStats.setLinesOfCode(totalClassLinesOfCode);

        classStatsMap.put(classStats.getFullName(), classStats);
    }

    private MethodStats processMethod(CtMethod<?> method, String className, String packageName) {
        String methodName = method.getSimpleName();
        MethodStats methodStats = new MethodStats(methodName, className, packageName);

        // Set method properties
        methodStats.setReturnType(method.getType().getSimpleName());
        methodStats.setVisibility(getVisibility(method.getModifiers()));
        methodStats.setStatic(method.hasModifier(ModifierKind.STATIC));
        methodStats.setAbstract(method.hasModifier(ModifierKind.ABSTRACT));
        methodStats.setConstructor(false);

        // Count parameters
        methodStats.setParameterCount(method.getParameters().size());

        // Count lines of code
        int linesOfCode = countLinesOfCode(method.getBody());
        methodStats.setLinesOfCode(linesOfCode);

        return methodStats;
    }

    private MethodStats processConstructor(CtConstructor<?> constructor, String className, String packageName) {
        MethodStats constructorStats = new MethodStats(className, className, packageName);

        // Set constructor properties
        constructorStats.setReturnType("");
        constructorStats.setVisibility(getVisibility(constructor.getModifiers()));
        constructorStats.setStatic(false);
        constructorStats.setAbstract(false);
        constructorStats.setConstructor(true);

        // Count parameters
        constructorStats.setParameterCount(constructor.getParameters().size());

        // Count lines of code
        int linesOfCode = countLinesOfCode(constructor.getBody());
        constructorStats.setLinesOfCode(linesOfCode);

        return constructorStats;
    }

    private String getVisibility(Set<ModifierKind> modifiers) {
        if (modifiers.contains(ModifierKind.PUBLIC))
            return "public";
        if (modifiers.contains(ModifierKind.PRIVATE))
            return "private";
        if (modifiers.contains(ModifierKind.PROTECTED))
            return "protected";
        return "package-private";
    }

    private int countLinesOfCode(spoon.reflect.code.CtBlock<?> body) {
        if (body == null)
            return 0;

        int count = 0;
        for (CtStatement statement : body.getStatements()) {
            count += countStatementLines(statement);
        }
        return count;
    }

    private int countStatementLines(CtStatement statement) {
        if (statement == null)
            return 0;

        // Simple line counting based on source position
        if (statement.getPosition() != null && statement.getPosition().isValidPosition()) {
            int startLine = statement.getPosition().getLine();
            int endLine = statement.getPosition().getEndLine();
            if (endLine >= startLine) {
                return endLine - startLine + 1;
            }
        }

        // Fallback: count newlines in string representation
        String statementStr = statement.toString();
        if (statementStr != null && !statementStr.trim().isEmpty()) {
            return Math.max(1, statementStr.split("\n").length);
        }

        return 1; // At least 1 line per statement
    }

    public Map<String, ClassStats> getClassStatsMap() {
        return classStatsMap;
    }

    public List<MethodStats> getMethodStatsList() {
        return methodStatsList;
    }

    public Set<String> getPackages() {
        return packages;
    }
}