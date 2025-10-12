package fr.jeremyhurel.processors;

import spoon.Launcher;
import spoon.reflect.CtModel;
import fr.jeremyhurel.models.*;
import fr.jeremyhurel.scanners.StatisticsScanner;
import java.util.*;
import java.util.stream.Collectors;

public class StatisticsProcessor {

    private String projectPath;

    public StatisticsProcessor(String projectPath) {
        this.projectPath = projectPath;
    }

    public ProjectStatistics generateStatistics() {
        return generateStatistics(10); // Default threshold of 10 methods
    }

    public ProjectStatistics generateStatistics(int methodThreshold) {
        // Create Spoon launcher
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setNoClasspath(true);

        // Build the model
        CtModel model = launcher.buildModel();

        // Create and run the scanner
        StatisticsScanner scanner = new StatisticsScanner();
        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(scanner::process);

        // Process collected data
        return calculateStatistics(scanner, methodThreshold);
    }

    private ProjectStatistics calculateStatistics(StatisticsScanner scanner, int methodThreshold) {
        ProjectStatistics stats = new ProjectStatistics();

        Map<String, ClassStats> classStatsMap = scanner.getClassStatsMap();
        List<MethodStats> methodStatsList = scanner.getMethodStatsList();
        Set<String> packages = scanner.getPackages();

        // 1. Nombre de classes de l'application
        stats.setTotalClasses(classStatsMap.size());

        // 2. Nombre de lignes de code de l'application
        int totalLOC = classStatsMap.values().stream()
                .mapToInt(ClassStats::getLinesOfCode)
                .sum();
        stats.setTotalLinesOfCode(totalLOC);

        // 3. Nombre total de méthodes de l'application
        stats.setTotalMethods(methodStatsList.size());

        // 4. Nombre total de packages de l'application
        stats.setTotalPackages(packages.size());

        // 5. Nombre moyen de méthodes par classe
        double avgMethodsPerClass = classStatsMap.isEmpty() ? 0
                : (double) methodStatsList.size() / classStatsMap.size();
        stats.setAverageMethodsPerClass(avgMethodsPerClass);

        // 6. Nombre moyen de lignes de code par méthode
        double avgLinesPerMethod = methodStatsList.isEmpty() ? 0
                : methodStatsList.stream().mapToInt(MethodStats::getLinesOfCode).average().orElse(0);
        stats.setAverageLinesPerMethod(avgLinesPerMethod);

        // 7. Nombre moyen d'attributs par classe
        double avgAttributesPerClass = classStatsMap.isEmpty() ? 0
                : classStatsMap.values().stream().mapToInt(ClassStats::getAttributeCount).average().orElse(0);
        stats.setAverageAttributesPerClass(avgAttributesPerClass);

        // 8. Les 10% des classes qui possèdent le plus grand nombre de méthodes
        List<ClassStats> sortedByMethods = classStatsMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getMethodCount(), a.getMethodCount()))
                .collect(Collectors.toList());
        int top10PercentCount = Math.max(1, (int) Math.ceil(sortedByMethods.size() * 0.1));
        stats.setTop10PercentClassesByMethods(
                sortedByMethods.subList(0, Math.min(top10PercentCount, sortedByMethods.size())));

        // 9. Les 10% des classes qui possèdent le plus grand nombre d'attributs
        List<ClassStats> sortedByAttributes = classStatsMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getAttributeCount(), a.getAttributeCount()))
                .collect(Collectors.toList());
        stats.setTop10PercentClassesByAttributes(
                sortedByAttributes.subList(0, Math.min(top10PercentCount, sortedByAttributes.size())));

        // 10. Les classes qui font partie en même temps des deux catégories précédentes
        Set<String> topMethodClasses = stats.getTop10PercentClassesByMethods().stream()
                .map(ClassStats::getFullName)
                .collect(Collectors.toSet());
        List<ClassStats> bothCategories = stats.getTop10PercentClassesByAttributes().stream()
                .filter(cls -> topMethodClasses.contains(cls.getFullName()))
                .collect(Collectors.toList());
        stats.setClassesInBothTopCategories(bothCategories);

        // 11. Les classes qui possèdent plus de X méthodes
        List<ClassStats> classesWithMoreThanX = classStatsMap.values().stream()
                .filter(cls -> cls.getMethodCount() > methodThreshold)
                .sorted((a, b) -> Integer.compare(b.getMethodCount(), a.getMethodCount()))
                .collect(Collectors.toList());
        stats.setClassesWithMoreThanXMethods(classesWithMoreThanX);
        stats.setMethodThreshold(methodThreshold);

        // 12. Les 10% des méthodes qui possèdent le plus grand nombre de lignes de code
        List<MethodStats> sortedByLines = methodStatsList.stream()
                .sorted((a, b) -> Integer.compare(b.getLinesOfCode(), a.getLinesOfCode()))
                .collect(Collectors.toList());
        int top10PercentMethods = Math.max(1, (int) Math.ceil(sortedByLines.size() * 0.1));
        stats.setTop10PercentMethodsByLines(
                sortedByLines.subList(0, Math.min(top10PercentMethods, sortedByLines.size())));

        // 13. Le nombre maximal de paramètres par rapport à toutes les méthodes
        int maxParams = methodStatsList.stream()
                .mapToInt(MethodStats::getParameterCount)
                .max()
                .orElse(0);
        stats.setMaxParametersInApplication(maxParams);

        return stats;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}