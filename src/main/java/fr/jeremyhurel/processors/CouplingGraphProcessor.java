package fr.jeremyhurel.processors;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.scanners.CouplingGraphScanner;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class CouplingGraphProcessor {

    private String projectPath;
    private String rootPackage;

    public CouplingGraphProcessor(String projectPath) {
        this.projectPath = projectPath;
    }

    public CouplingGraphProcessor(String projectPath, String rootPackage) {
        this.projectPath = projectPath;
        this.rootPackage = rootPackage;
    }

    public CouplingGraph generateCouplingGraph() {
        CouplingGraph couplingGraph = new CouplingGraph();

        // Create Spoon launcher
        Launcher launcher = new Launcher();
        launcher.addInputResource(projectPath);
        launcher.getEnvironment().setAutoImports(true);
        launcher.getEnvironment().setCommentEnabled(false);
        launcher.getEnvironment().setNoClasspath(true);

        // Build the model
        CtModel model = launcher.buildModel();

        // Create and add the scanner
        CouplingGraphScanner scanner;
        if (rootPackage != null) {
            scanner = new CouplingGraphScanner(couplingGraph, rootPackage);
        } else {
            scanner = new CouplingGraphScanner(couplingGraph);
        }

        // Process the model
        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(scanner::process);

        // Remove any orphaned nodes (external library classes that were referenced but not analyzed)
        couplingGraph.removeOrphanedNodes();

        // Calculate normalized coupling values based on the specification
        // Couplage(A,B) = Number of method calls between A and B / Total method calls
        couplingGraph.calculateNormalizedCoupling();

        return couplingGraph;
    }

    /**
     * Generates a hierarchical cluster tree from the coupling graph
     * using agglomerative clustering with average linkage
     */
    public ClusterTree generateClusterTree() {
        CouplingGraph couplingGraph = generateCouplingGraph();
        return generateClusterTree(couplingGraph);
    }

    /**
     * Generates a hierarchical cluster tree from an existing coupling graph
     */
    public ClusterTree generateClusterTree(CouplingGraph couplingGraph) {
        ClusterTree clusterTree = new ClusterTree();
        clusterTree.buildFromCouplingGraph(couplingGraph);
        return clusterTree;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }
}
