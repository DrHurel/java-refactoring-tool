package fr.jeremyhurel.processors;

import fr.jeremyhurel.models.ClusterTree;
import fr.jeremyhurel.models.CouplingGraph;
import fr.jeremyhurel.scanners.CouplingGraphScanner;
import spoon.Launcher;
import spoon.reflect.CtModel;

public class CouplingGraphProcessor extends BaseProcessor {

    private String rootPackage;

    public CouplingGraphProcessor(String projectPath) {
        super(projectPath);
    }

    public CouplingGraphProcessor(String projectPath, String rootPackage) {
        super(projectPath);
        this.rootPackage = rootPackage;
    }

    public CouplingGraph generateCouplingGraph() {
        CouplingGraph couplingGraph = new CouplingGraph();

        Launcher launcher = createLauncher();
        CtModel model = buildModel(launcher);

        CouplingGraphScanner scanner = createScanner(couplingGraph);

        scanner.setFactory(launcher.getFactory());
        model.getAllTypes().forEach(scanner::process);

        couplingGraph.removeOrphanedNodes();
        couplingGraph.calculateNormalizedCoupling();

        return couplingGraph;
    }

    private CouplingGraphScanner createScanner(CouplingGraph couplingGraph) {
        if (rootPackage != null) {
            return new CouplingGraphScanner(couplingGraph, rootPackage);
        }
        return new CouplingGraphScanner(couplingGraph);
    }

    public ClusterTree generateClusterTree() {
        CouplingGraph couplingGraph = generateCouplingGraph();
        return generateClusterTree(couplingGraph);
    }

    public ClusterTree generateClusterTree(CouplingGraph couplingGraph) {
        ClusterTree clusterTree = new ClusterTree();
        clusterTree.buildFromCouplingGraph(couplingGraph);
        return clusterTree;
    }

    public String getRootPackage() {
        return rootPackage;
    }

    public void setRootPackage(String rootPackage) {
        this.rootPackage = rootPackage;
    }
}
