package fr.jeremyhurel.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

import fr.jeremyhurel.models.MethodStats;
import fr.jeremyhurel.models.ProjectStatistics;
import fr.jeremyhurel.models.class_models.ClassStats;

public class StatisticsExporter {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    private StatisticsExporter() {

    }

    public static void exportToText(ProjectStatistics stats, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writeStatisticsReport(writer, stats);
        }
    }

    private static void writeStatisticsReport(FileWriter writer, ProjectStatistics stats) throws IOException {
        writer.write("=".repeat(80) + "\n");
        writer.write("RAPPORT STATISTIQUES DU PROJET JAVA\n");
        writer.write("=".repeat(80) + "\n\n");

        writer.write("STATISTIQUES GENERALES\n");
        writer.write("-".repeat(30) + "\n");
        writer.write("1. Nombre de classes de l'application: " + stats.getTotalClasses() + "\n");
        writer.write("2. Nombre de lignes de code de l'application: " + stats.getTotalLinesOfCode() + "\n");
        writer.write("3. Nombre total de méthodes de l'application: " + stats.getTotalMethods() + "\n");
        writer.write("4. Nombre total de packages de l'application: " + stats.getTotalPackages() + "\n");
        writer.write("5. Nombre moyen de méthodes par classe: "
                + DECIMAL_FORMAT.format(stats.getAverageMethodsPerClass()) + "\n");
        writer.write("6. Nombre moyen de lignes de code par méthode: "
                + DECIMAL_FORMAT.format(stats.getAverageLinesPerMethod()) + "\n");
        writer.write("7. Nombre moyen d'attributs par classe: "
                + DECIMAL_FORMAT.format(stats.getAverageAttributesPerClass()) + "\n\n");

        writer.write("8. LES 10% DES CLASSES AVEC LE PLUS DE METHODES\n");
        writer.write("-".repeat(50) + "\n");
        for (ClassStats cls : stats.getTop10PercentClassesByMethods()) {
            writer.write("   - " + cls.getFullName() + " (" + cls.getMethodCount() + " méthodes)\n");
        }
        writer.write("\n");

        writer.write("9. LES 10% DES CLASSES AVEC LE PLUS D'ATTRIBUTS\n");
        writer.write("-".repeat(50) + "\n");
        for (ClassStats cls : stats.getTop10PercentClassesByAttributes()) {
            writer.write("   - " + cls.getFullName() + " (" + cls.getAttributeCount() + " attributs)\n");
        }
        writer.write("\n");

        writer.write("10. CLASSES DANS LES DEUX CATEGORIES PRECEDENTES\n");
        writer.write("-".repeat(50) + "\n");
        if (stats.getClassesInBothTopCategories().isEmpty()) {
            writer.write("   Aucune classe ne fait partie des deux catégories.\n");
        } else {
            for (ClassStats cls : stats.getClassesInBothTopCategories()) {
                writer.write("   - " + cls.getFullName() + " (" + cls.getMethodCount() + " méthodes, "
                        + cls.getAttributeCount() + " attributs)\n");
            }
        }
        writer.write("\n");

        writer.write("11. CLASSES AVEC PLUS DE " + stats.getMethodThreshold() + " METHODES\n");
        writer.write("-".repeat(50) + "\n");
        if (stats.getClassesWithMoreThanXMethods().isEmpty()) {
            writer.write("   Aucune classe n'a plus de " + stats.getMethodThreshold() + " méthodes.\n");
        } else {
            for (ClassStats cls : stats.getClassesWithMoreThanXMethods()) {
                writer.write("   - " + cls.getFullName() + " (" + cls.getMethodCount() + " méthodes)\n");
            }
        }
        writer.write("\n");

        writer.write("12. LES 10% DES METHODES AVEC LE PLUS DE LIGNES\n");
        writer.write("-".repeat(50) + "\n");
        for (MethodStats method : stats.getTop10PercentMethodsByLines()) {
            writer.write("   - " + method.getFullMethodName() + " (" + method.getLinesOfCode() + " lignes)\n");
        }
        writer.write("\n");

        writer.write("13. NOMBRE MAXIMAL DE PARAMETRES\n");
        writer.write("-".repeat(50) + "\n");
        writer.write("   Le nombre maximal de paramètres dans l'application: " + stats.getMaxParametersInApplication()
                + "\n\n");

        writer.write("=".repeat(80) + "\n");
        writer.write("FIN DU RAPPORT\n");
        writer.write("=".repeat(80) + "\n");
    }

    public static String formatStatisticsForDisplay(ProjectStatistics stats) {
        StringBuilder sb = new StringBuilder();

        sb.append("=== STATISTIQUES DU PROJET ===\n\n");

        sb.append("RESUME GENERAL:\n");
        sb.append("- Classes: ").append(stats.getTotalClasses()).append("\n");
        sb.append("- Lignes de code: ").append(stats.getTotalLinesOfCode()).append("\n");
        sb.append("- Méthodes: ").append(stats.getTotalMethods()).append("\n");
        sb.append("- Packages: ").append(stats.getTotalPackages()).append("\n");

        sb.append("\nMOYENNES:\n");
        sb.append("- Méthodes/classe: ").append(DECIMAL_FORMAT.format(stats.getAverageMethodsPerClass())).append("\n");
        sb.append("- Lignes/méthode: ").append(DECIMAL_FORMAT.format(stats.getAverageLinesPerMethod())).append("\n");
        sb.append("- Attributs/classe: ").append(DECIMAL_FORMAT.format(stats.getAverageAttributesPerClass()))
                .append("\n");

        sb.append("\nTOP CLASSES (Méthodes):\n");
        stats.getTop10PercentClassesByMethods().stream()
                .limit(5)
                .forEach(cls -> sb.append("- ").append(cls.getClassName()).append(" (").append(cls.getMethodCount())
                        .append(")\n"));

        sb.append("\nTOP CLASSES (Attributs):\n");
        stats.getTop10PercentClassesByAttributes().stream()
                .limit(5)
                .forEach(cls -> sb.append("- ").append(cls.getClassName()).append(" (").append(cls.getAttributeCount())
                        .append(")\n"));

        sb.append("\nClasses > ").append(stats.getMethodThreshold()).append(" méthodes: ")
                .append(stats.getClassesWithMoreThanXMethods().size());
        sb.append("\nMax paramètres: ").append(stats.getMaxParametersInApplication());

        return sb.toString();
    }
}