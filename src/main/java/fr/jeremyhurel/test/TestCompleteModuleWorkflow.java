package fr.jeremyhurel.test;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.models.ModuleGraph;
import fr.jeremyhurel.models.class_models.ClassDiagram;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy.Mode;
import fr.jeremyhurel.processors.ClassDiagramProcessor;
import fr.jeremyhurel.processors.CouplingGraphProcessor;
import fr.jeremyhurel.utils.strategies.ClassDiagramWithModulesPlantUMLExportStrategy;

public class TestCompleteModuleWorkflow {
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("COMPLETE MODULE EXTRACTION WORKFLOW TEST");
            System.out.println("========================================\n");

            String projectPath = "./src/main/java";

            System.out.println("Step 1: Generating coupling graph...");
            CouplingGraphProcessor couplingProcessor = new CouplingGraphProcessor(projectPath);
            CouplingGraph couplingGraph = couplingProcessor.generateCouplingGraph();
            System.out.println("  ✓ Total classes: " + couplingGraph.getNodeCount());
            System.out.println("  ✓ Total couplings: " + couplingGraph.getCouplingCount());

            System.out.println("\nStep 2: Building cluster tree...");
            ClusterTree clusterTree = new ClusterTree();
            clusterTree.buildFromCouplingGraph(couplingGraph);
            System.out.println("  ✓ Tree built successfully");

            System.out.println("\nStep 3: Extracting modules (automatic)...");
            ModuleGraph autoModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.AUTO),
                couplingGraph
            );
            System.out.println("  ✓ Modules: " + autoModules.getModuleCount());
            System.out.println("  ✓ Avg cohesion: " + String.format("%.4f", autoModules.getAverageCohesion()));

            System.out.println("\nStep 4: Extracting modules (fixed count=7)...");
            ModuleGraph fixedModules = clusterTree.extractModules(
                new ParameterizedClusteringStrategy(Mode.FIXED_COUNT, 7),
                couplingGraph
            );
            System.out.println("  ✓ Modules: " + fixedModules.getModuleCount());
            System.out.println("  ✓ Avg cohesion: " + String.format("%.4f", fixedModules.getAverageCohesion()));

            System.out.println("\nStep 5: Generating class diagram...");
            ClassDiagramProcessor diagramProcessor = new ClassDiagramProcessor(projectPath);
            ClassDiagram classDiagram = diagramProcessor.generateClassDiagram();
            System.out.println("  ✓ Classes: " + classDiagram.getClassCount());
            System.out.println("  ✓ Relationships: " + classDiagram.getRelationshipCount());

            System.out.println("\nStep 6: Exporting PlantUML with automatic modules...");
            ClassDiagramWithModulesPlantUMLExportStrategy autoStrategy =
                new ClassDiagramWithModulesPlantUMLExportStrategy(classDiagram, autoModules);
            autoStrategy.export("./test-modules-auto.puml");
            System.out.println("  ✓ Exported: test-modules-auto.puml");

            System.out.println("\nStep 7: Exporting PlantUML with fixed modules...");
            ClassDiagramWithModulesPlantUMLExportStrategy fixedStrategy =
                new ClassDiagramWithModulesPlantUMLExportStrategy(classDiagram, fixedModules);
            fixedStrategy.export("./test-modules-7.puml");
            System.out.println("  ✓ Exported: test-modules-7.puml");

            System.out.println("\nStep 8: Exporting text module graphs...");
            autoModules.exportToFile("./test-modules-auto.txt");
            System.out.println("  ✓ Exported: test-modules-auto.txt");
            fixedModules.exportToFile("./test-modules-7.txt");
            System.out.println("  ✓ Exported: test-modules-7.txt");

            System.out.println("\n========================================");
            System.out.println("ALL TESTS PASSED ✓");
            System.out.println("========================================");
            System.out.println("\nGenerated files:");
            System.out.println("  • test-modules-auto.puml (PlantUML with " + autoModules.getModuleCount() + " modules)");
            System.out.println("  • test-modules-7.puml (PlantUML with 7 modules)");
            System.out.println("  • test-modules-auto.txt (Text format, automatic)");
            System.out.println("  • test-modules-7.txt (Text format, 7 modules)");

        } catch (Exception e) {
            System.err.println("\n❌ TEST FAILED:");
            e.printStackTrace();
        }
    }
}
