package fr.jeremyhurel.scanners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.jeremyhurel.models.MethodStats;
import fr.jeremyhurel.models.class_models.ClassStats;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.ModifierKind;

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

        packages.add(packageName);

        ClassStats classStats = new ClassStats(className, packageName);
        classStats.setInterface(type.isInterface());
        classStats.setAbstract(type.hasModifier(ModifierKind.ABSTRACT));

        classStats.setAttributeCount(type.getFields().size());

        int methodCount = 0;
        int totalClassLinesOfCode = 0;

        for (CtMethod<?> method : type.getMethods()) {
            methodCount++;
            MethodStats methodStats = processMethod(method, className, packageName);
            methodStatsList.add(methodStats);
            totalClassLinesOfCode += methodStats.getLinesOfCode();
        }

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

        methodStats.setReturnType(method.getType().getSimpleName());
        methodStats.setVisibility(getVisibility(method.getModifiers()));
        methodStats.setStatic(method.hasModifier(ModifierKind.STATIC));
        methodStats.setAbstract(method.hasModifier(ModifierKind.ABSTRACT));
        methodStats.setConstructor(false);

        methodStats.setParameterCount(method.getParameters().size());

        int linesOfCode = countLinesOfCode(method.getBody());
        methodStats.setLinesOfCode(linesOfCode);

        return methodStats;
    }

    private MethodStats processConstructor(CtConstructor<?> constructor, String className, String packageName) {
        MethodStats constructorStats = new MethodStats(className, className, packageName);

        constructorStats.setReturnType("");
        constructorStats.setVisibility(getVisibility(constructor.getModifiers()));
        constructorStats.setStatic(false);
        constructorStats.setAbstract(false);
        constructorStats.setConstructor(true);

        constructorStats.setParameterCount(constructor.getParameters().size());

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

        if (statement.getPosition() != null && statement.getPosition().isValidPosition()) {
            int startLine = statement.getPosition().getLine();
            int endLine = statement.getPosition().getEndLine();
            if (endLine >= startLine) {
                return endLine - startLine + 1;
            }
        }

        String statementStr = statement.toString();
        if (statementStr != null && !statementStr.trim().isEmpty()) {
            return Math.max(1, statementStr.split("\n").length);
        }

        return 1;
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