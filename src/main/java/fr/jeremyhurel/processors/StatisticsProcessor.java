package fr.jeremyhurel.processors;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import fr.jeremyhurel.constants.AppConstants;
import fr.jeremyhurel.models.MethodStats;
import fr.jeremyhurel.models.ProjectStatistics;
import fr.jeremyhurel.models.class_models.ClassStats;
import fr.jeremyhurel.scanners.StatisticsScanner;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class StatisticsProcessor extends BaseProcessor {

    public StatisticsProcessor(String projectPath) {
        super(projectPath);
    }

    public ProjectStatistics generateStatistics() {
        return generateStatistics(AppConstants.DEFAULT_METHOD_THRESHOLD);
    }

    public ProjectStatistics generateStatistics(int methodThreshold) {

        Launcher launcher = createLauncher();
        CtModel model = buildModel(launcher);

        StatisticsScanner scanner = new StatisticsScanner();
        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(scanner::process);

        return calculateStatistics(scanner, methodThreshold);
    }

    private ProjectStatistics calculateStatistics(StatisticsScanner scanner, int methodThreshold) {
        ProjectStatistics stats = new ProjectStatistics();

        Map<String, ClassStats> classStatsMap = scanner.getClassStatsMap();
        List<MethodStats> methodStatsList = scanner.getMethodStatsList();
        Set<String> packages = scanner.getPackages();

        stats.setTotalClasses(classStatsMap.size());

        int totalLOC = classStatsMap.values().stream()
                .mapToInt(ClassStats::getLinesOfCode)
                .sum();
        stats.setTotalLinesOfCode(totalLOC);

        stats.setTotalMethods(methodStatsList.size());

        stats.setTotalPackages(packages.size());

        double avgMethodsPerClass = classStatsMap.isEmpty() ? 0
                : (double) methodStatsList.size() / classStatsMap.size();
        stats.setAverageMethodsPerClass(avgMethodsPerClass);

        double avgLinesPerMethod = methodStatsList.isEmpty() ? 0
                : methodStatsList.stream().mapToInt(MethodStats::getLinesOfCode).average().orElse(0);
        stats.setAverageLinesPerMethod(avgLinesPerMethod);

        double avgAttributesPerClass = classStatsMap.isEmpty() ? 0
                : classStatsMap.values().stream().mapToInt(ClassStats::getAttributeCount).average().orElse(0);
        stats.setAverageAttributesPerClass(avgAttributesPerClass);

        List<ClassStats> sortedByMethods = classStatsMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getMethodCount(), a.getMethodCount()))
                .collect(Collectors.toList());
        int top10PercentCount = Math.max(1, (int) Math.ceil(sortedByMethods.size() * 0.1));
        stats.setTop10PercentClassesByMethods(
                sortedByMethods.subList(0, Math.min(top10PercentCount, sortedByMethods.size())));

        List<ClassStats> sortedByAttributes = classStatsMap.values().stream()
                .sorted((a, b) -> Integer.compare(b.getAttributeCount(), a.getAttributeCount()))
                .collect(Collectors.toList());
        stats.setTop10PercentClassesByAttributes(
                sortedByAttributes.subList(0, Math.min(top10PercentCount, sortedByAttributes.size())));

        Set<String> topMethodClasses = stats.getTop10PercentClassesByMethods().stream()
                .map(ClassStats::getFullName)
                .collect(Collectors.toSet());
        List<ClassStats> bothCategories = stats.getTop10PercentClassesByAttributes().stream()
                .filter(cls -> topMethodClasses.contains(cls.getFullName()))
                .collect(Collectors.toList());
        stats.setClassesInBothTopCategories(bothCategories);

        List<ClassStats> classesWithMoreThanX = classStatsMap.values().stream()
                .filter(cls -> cls.getMethodCount() > methodThreshold)
                .sorted((a, b) -> Integer.compare(b.getMethodCount(), a.getMethodCount()))
                .collect(Collectors.toList());
        stats.setClassesWithMoreThanXMethods(classesWithMoreThanX);
        stats.setMethodThreshold(methodThreshold);

        List<MethodStats> sortedByLines = methodStatsList.stream()
                .sorted((a, b) -> Integer.compare(b.getLinesOfCode(), a.getLinesOfCode()))
                .collect(Collectors.toList());
        int top10PercentMethods = Math.max(AppConstants.MIN_TOP_ITEMS,
                (int) Math.ceil(sortedByLines.size() * AppConstants.TOP_PERCENTAGE));
        stats.setTop10PercentMethodsByLines(
                sortedByLines.subList(0, Math.min(top10PercentMethods, sortedByLines.size())));

        int maxParams = methodStatsList.stream()
                .mapToInt(MethodStats::getParameterCount)
                .max()
                .orElse(0);
        stats.setMaxParametersInApplication(maxParams);

        return stats;
    }
}