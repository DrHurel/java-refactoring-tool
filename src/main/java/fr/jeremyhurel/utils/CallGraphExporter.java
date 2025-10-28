package fr.jeremyhurel.utils;

import java.io.IOException;

import fr.jeremyhurel.models.CallGraph;
import fr.jeremyhurel.utils.strategies.CallGraphJsonExportStrategy;
import fr.jeremyhurel.utils.strategies.CallGraphDotExportStrategy;
import fr.jeremyhurel.utils.strategies.ExportStrategy;

public class CallGraphExporter {

    private CallGraphExporter() {

    }

    public static void exportToJson(CallGraph callGraph, String filePath) throws IOException {
        ExportStrategy<CallGraph> strategy = new CallGraphJsonExportStrategy();
        strategy.export(callGraph, filePath);
    }

    public static void exportToDot(CallGraph callGraph, String filePath) throws IOException {
        ExportStrategy<CallGraph> strategy = new CallGraphDotExportStrategy();
        strategy.export(callGraph, filePath);
    }

    public static void export(CallGraph callGraph, ExportStrategy<CallGraph> strategy, String filePath)
            throws IOException {
        strategy.export(callGraph, filePath);
    }
}
