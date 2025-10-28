package fr.jeremyhurel.test;

import fr.jeremyhurel.processors.StatisticsProcessor;
import fr.jeremyhurel.models.ProjectStatistics;
import fr.jeremyhurel.utils.StatisticsExporter;

public class TestStatisticsExport {
    public static void main(String[] args) {
        try {

            StatisticsProcessor processor = new StatisticsProcessor("./src/main/java");
            ProjectStatistics stats = processor.generateStatistics(5);

            StatisticsExporter.exportToText(stats, "./test-statistics.txt");

            System.out.println("Statistiques du projet exportées avec succès!");
            System.out.println("Classes trouvées: " + stats.getTotalClasses());
            System.out.println("Méthodes trouvées: " + stats.getTotalMethods());
            System.out.println("Lignes de code: " + stats.getTotalLinesOfCode());
            System.out.println("Packages: " + stats.getTotalPackages());
            System.out.println("Classes avec > 5 méthodes: " + stats.getClassesWithMoreThanXMethods().size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}