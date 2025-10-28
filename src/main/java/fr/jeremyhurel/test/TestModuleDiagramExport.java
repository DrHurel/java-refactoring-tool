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

public class TestModuleDiagramExport {
    public static void main(String[] args) {
        try {
            String projectPath = "./src/main/java";

            System.out.println("========================================");
            System.out.println("MODULE-BASED CLASS DIAGRAM EXPORT TEST");
            System.out.println("========================================\n");

            System.out.println("Step 1: Generating class diagram...");
            ClassDiagramProcessor classProcessor = new ClassDiagramProcessor(projectPath);
            ClassDiagram classDiagram = classProcessor.generateClassDiagram();
            System.out.println("  - Total classes: " + classDiagram.getClassCount());
            System.out.println("  - Total relationships: " + classDiagram.getRelationshipCount());

            System.out.println("\nStep 2: Generating coupling graph...");
            CouplingGraphProcessor couplingProcessor = new CouplingGraphProcessor(projectPath);
            CouplingGraph couplingGraph = couplingProcessor.generateCouplingGraph();
            System.out.println("  - Total couplings: " + couplingGraph.getCouplingCount());

            System.out.println("\nStep 3: Building cluster tree...");
            ClusterTree clusterTree = new ClusterTree();
            clusterTree.buildFromCouplingGraph(couplingGraph);

            System.out.println("\nStep 4: Extracting modules (automatic)...");
            ModuleGraph moduleGraphAuto = clusterTree.extractModules(new ParameterizedClusteringStrategy(Mode.AUTO), couplingGraph);
            System.out.println("  - Modules found: " + moduleGraphAuto.getModuleCount());
            System.out.println("  - Average cohesion: " + String.format("%.4f", moduleGraphAuto.getAverageCohesion()));

            System.out.println("\nExporting class diagram with automatic modules...");
            ClassDiagramWithModulesPlantUMLExportStrategy autoStrategy =
                new ClassDiagramWithModulesPlantUMLExportStrategy(classDiagram, moduleGraphAuto);
            autoStrategy.export("./classdiagram-with-modules-auto.puml");
            System.out.println("  ✓ Exported to: classdiagram-with-modules-auto.puml");

            System.out.println("\nStep 5: Extracting modules (fixed count=5)...");
            ModuleGraph moduleGraphFixed = clusterTree.extractModules(new ParameterizedClusteringStrategy(Mode.FIXED_COUNT, 5), couplingGraph);
            System.out.println("  - Modules found: " + moduleGraphFixed.getModuleCount());
            System.out.println("  - Average cohesion: " + String.format("%.4f", moduleGraphFixed.getAverageCohesion()));

            System.out.println("\nExporting class diagram with 5 modules...");
            ClassDiagramWithModulesPlantUMLExportStrategy fixedStrategy =
                new ClassDiagramWithModulesPlantUMLExportStrategy(classDiagram, moduleGraphFixed);
            fixedStrategy.export("./classdiagram-with-modules-5.puml");
            System.out.println("  ✓ Exported to: classdiagram-with-modules-5.puml");

            System.out.println("\n========================================");
            System.out.println("EXPORT COMPLETE");
            System.out.println("========================================");
            System.out.println("\nGenerated files:");
            System.out.println("  1. classdiagram-with-modules-auto.puml - " + moduleGraphAuto.getModuleCount() + " modules (automatic)");
            System.out.println("  2. classdiagram-with-modules-5.puml - " + moduleGraphFixed.getModuleCount() + " modules (fixed)");
            System.out.println("\nYou can visualize these files using PlantUML:");
            System.out.println("  https://www.plantuml.com/plantuml/uml/");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
