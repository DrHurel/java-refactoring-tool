package fr.jeremyhurel.utils;

import java.io.IOException;

import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.utils.strategies.CouplingGraphJsonExportStrategy;
import fr.jeremyhurel.utils.strategies.CouplingGraphDotExportStrategy;
import fr.jeremyhurel.utils.strategies.ExportStrategy;

public class CouplingGraphExporter {

    private CouplingGraphExporter() {

    }

    public static void exportToJson(CouplingGraph couplingGraph, String filePath) throws IOException {
        ExportStrategy<CouplingGraph> strategy = new CouplingGraphJsonExportStrategy();
        strategy.export(couplingGraph, filePath);
    }

    public static void exportToDot(CouplingGraph couplingGraph, String filePath) throws IOException {
        ExportStrategy<CouplingGraph> strategy = new CouplingGraphDotExportStrategy();
        strategy.export(couplingGraph, filePath);
    }

    public static void export(CouplingGraph couplingGraph, ExportStrategy<CouplingGraph> strategy, String filePath)
            throws IOException {
        strategy.export(couplingGraph, filePath);
    }
}
