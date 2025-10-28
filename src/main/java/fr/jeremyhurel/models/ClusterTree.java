package fr.jeremyhurel.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.jeremyhurel.models.strategies.ClusteringStrategy;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy;
import fr.jeremyhurel.models.strategies.ParameterizedClusteringStrategy.Mode;

public class ClusterTree {

    private ClusterNode root;
    private List<ClusterNode> mergeHistory;
    private int nodeIdCounter = 0;

    public ClusterTree() {
        this.mergeHistory = new ArrayList<>();
    }

    public void buildFromCouplingGraph(CouplingGraph couplingGraph) {

        Map<String, Map<String, Double>> couplingMatrix = couplingGraph.getCouplingMatrix();

        if (couplingMatrix.isEmpty()) {
            return;
        }

        Map<String, ClusterNode> activeClusters = new HashMap<>();
        for (String className : couplingGraph.getAllNodeNames()) {
            ClusterNode node = new ClusterNode(className);
            node.setId(nodeIdCounter++);
            activeClusters.put(className, node);
        }

        Map<String, Map<String, Double>> workingMatrix = deepCopyCouplingMatrix(couplingMatrix);

        while (activeClusters.size() > 1) {

            MaxCouplingPair maxPair = findMaxCoupling(workingMatrix, activeClusters);

            if (maxPair == null) {

                List<String> remainingKeys = new ArrayList<>(activeClusters.keySet());
                if (remainingKeys.size() >= 2) {
                    maxPair = new MaxCouplingPair(remainingKeys.get(0), remainingKeys.get(1), 0.0);
                } else {
                    break;
                }
            }

            ClusterNode node1 = activeClusters.get(maxPair.class1);
            ClusterNode node2 = activeClusters.get(maxPair.class2);

            String simpleName1 = getSimpleClassName(node1.getName());
            String simpleName2 = getSimpleClassName(node2.getName());
            String newClusterName = simpleName1 + "+" + simpleName2;

            ClusterNode newCluster = new ClusterNode(newClusterName);
            newCluster.setId(nodeIdCounter++);
            newCluster.setLeft(node1);
            newCluster.setRight(node2);
            newCluster.setCouplingValue(maxPair.coupling);

            mergeHistory.add(newCluster);

            activeClusters.remove(maxPair.class1);
            activeClusters.remove(maxPair.class2);

            updateCouplingMatrix(workingMatrix, maxPair.class1, maxPair.class2, newClusterName, activeClusters);

            activeClusters.put(newClusterName, newCluster);
        }

        if (!activeClusters.isEmpty()) {
            root = activeClusters.values().iterator().next();
        }
    }

    private MaxCouplingPair findMaxCoupling(Map<String, Map<String, Double>> matrix,
                                           Map<String, ClusterNode> activeClusters) {
        double maxCoupling = -1.0;
        String maxClass1 = null;
        String maxClass2 = null;

        for (Map.Entry<String, Map<String, Double>> entry : matrix.entrySet()) {
            String from = entry.getKey();
            if (!activeClusters.containsKey(from)) {
                continue;
            }

            for (Map.Entry<String, Double> couplingEntry : entry.getValue().entrySet()) {
                String to = couplingEntry.getKey();
                if (!activeClusters.containsKey(to)) {
                    continue;
                }

                double coupling = couplingEntry.getValue();
                if (coupling > maxCoupling) {
                    maxCoupling = coupling;
                    maxClass1 = from;
                    maxClass2 = to;
                }
            }
        }

        for (Map.Entry<String, Map<String, Double>> entry : matrix.entrySet()) {
            String to = entry.getKey();
            if (!activeClusters.containsKey(to)) {
                continue;
            }

            for (Map.Entry<String, Double> couplingEntry : entry.getValue().entrySet()) {
                String from = couplingEntry.getKey();
                if (!activeClusters.containsKey(from)) {
                    continue;
                }

                double coupling = couplingEntry.getValue();
                if (coupling > maxCoupling) {
                    maxCoupling = coupling;
                    maxClass1 = to;
                    maxClass2 = from;
                }
            }
        }

        if (maxClass1 != null && maxClass2 != null) {
            return new MaxCouplingPair(maxClass1, maxClass2, maxCoupling);
        }

        return null;
    }

    private void updateCouplingMatrix(Map<String, Map<String, Double>> matrix,
                                     String cluster1, String cluster2, String newClusterName,
                                     Map<String, ClusterNode> activeClusters) {

        matrix.remove(cluster1);
        matrix.remove(cluster2);

        for (Map<String, Double> row : matrix.values()) {
            row.remove(cluster1);
            row.remove(cluster2);
        }

        Map<String, Double> newRow = new HashMap<>();

        for (String otherCluster : activeClusters.keySet()) {
            if (otherCluster.equals(cluster1) || otherCluster.equals(cluster2)) {
                continue;
            }

            double coupling1 = getCouplingValue(matrix, cluster1, otherCluster);
            double coupling2 = getCouplingValue(matrix, cluster2, otherCluster);
            double avgCoupling = (coupling1 + coupling2) / 2.0;

            if (avgCoupling > 0) {
                newRow.put(otherCluster, avgCoupling);
            }
        }

        matrix.put(newClusterName, newRow);

        for (String otherCluster : activeClusters.keySet()) {
            if (otherCluster.equals(cluster1) || otherCluster.equals(cluster2)) {
                continue;
            }

            double coupling1 = getCouplingValue(matrix, otherCluster, cluster1);
            double coupling2 = getCouplingValue(matrix, otherCluster, cluster2);
            double avgCoupling = (coupling1 + coupling2) / 2.0;

            if (avgCoupling > 0) {
                matrix.putIfAbsent(otherCluster, new HashMap<>());
                matrix.get(otherCluster).put(newClusterName, avgCoupling);
            }
        }
    }

    private double getCouplingValue(Map<String, Map<String, Double>> matrix, String from, String to) {
        if (matrix.containsKey(from) && matrix.get(from).containsKey(to)) {
            return matrix.get(from).get(to);
        }
        if (matrix.containsKey(to) && matrix.get(to).containsKey(from)) {
            return matrix.get(to).get(from);
        }
        return 0.0;
    }

    private Map<String, Map<String, Double>> deepCopyCouplingMatrix(Map<String, Map<String, Double>> original) {
        Map<String, Map<String, Double>> copy = new HashMap<>();
        for (Map.Entry<String, Map<String, Double>> entry : original.entrySet()) {
            Map<String, Double> rowCopy = new HashMap<>(entry.getValue());
            copy.put(entry.getKey(), rowCopy);
        }
        return copy;
    }

    public ClusterNode getRoot() {
        return root;
    }

    public List<ClusterNode> getMergeHistory() {
        return mergeHistory;
    }

    public int getDepth() {
        return calculateDepth(root);
    }

    private int calculateDepth(ClusterNode node) {
        if (node == null || node.isLeaf()) {
            return 0;
        }
        return 1 + Math.max(calculateDepth(node.getLeft()), calculateDepth(node.getRight()));
    }

    private String getSimpleClassName(String fullName) {
        if (fullName == null) {
            return "";
        }

        if (fullName.contains("+")) {
            return fullName;
        }

        int lastDotIndex = fullName.lastIndexOf('.');
        if (lastDotIndex >= 0 && lastDotIndex < fullName.length() - 1) {
            return fullName.substring(lastDotIndex + 1);
        }
        return fullName;
    }

    public ModuleGraph extractModules(Integer targetModuleCount, Double minCouplingThreshold,
                                     CouplingGraph couplingGraph) {
        if (root == null) {
            return new ModuleGraph();
        }

        ClusteringStrategy strategy;

        if (targetModuleCount != null && targetModuleCount > 0) {
            strategy = new ParameterizedClusteringStrategy(Mode.FIXED_COUNT, targetModuleCount);
        } else if (minCouplingThreshold != null && minCouplingThreshold > 0) {
            strategy = new ParameterizedClusteringStrategy(Mode.THRESHOLD, minCouplingThreshold);
        } else {
            strategy = new ParameterizedClusteringStrategy(Mode.AUTO);
        }

        return extractModules(strategy, couplingGraph);
    }

    public ModuleGraph extractModules(ClusteringStrategy strategy, CouplingGraph couplingGraph) {
        if (root == null) {
            return new ModuleGraph();
        }

        List<ClusterNode> clusters = strategy.cut(root);

        return createModuleGraph(clusters, couplingGraph);
    }

    private ModuleGraph createModuleGraph(List<ClusterNode> clusters, CouplingGraph couplingGraph) {
        ModuleGraph moduleGraph = new ModuleGraph();
        int moduleId = 1;

        for (ClusterNode cluster : clusters) {

            String moduleName = "Module_" + moduleId;
            Module module = new Module(moduleId, moduleName);

            List<String> classes = new ArrayList<>();
            collectLeafClassNames(cluster, classes);
            module.addClasses(classes);

            double cohesion = calculateModuleCohesion(classes, couplingGraph);
            module.setCohesion(cohesion);

            moduleGraph.addModule(module);
            moduleId++;
        }

        return moduleGraph;
    }

    private void collectLeafClassNames(ClusterNode node, List<String> result) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            result.add(node.getName());
        } else {
            collectLeafClassNames(node.getLeft(), result);
            collectLeafClassNames(node.getRight(), result);
        }
    }

    private double calculateModuleCohesion(List<String> classes, CouplingGraph couplingGraph) {
        if (classes.size() <= 1) {
            return 0.0;
        }

        double totalCoupling = 0.0;
        int pairCount = 0;

        for (int i = 0; i < classes.size(); i++) {
            for (int j = i + 1; j < classes.size(); j++) {
                double coupling = couplingGraph.getCouplingWeight(classes.get(i), classes.get(j));
                coupling += couplingGraph.getCouplingWeight(classes.get(j), classes.get(i));

                if (coupling > 0) {
                    totalCoupling += coupling;
                    pairCount++;
                }
            }
        }

        return pairCount > 0 ? totalCoupling / pairCount : 0.0;
    }

    public static class ClusterNode {
        private int id;
        private String name;
        private ClusterNode left;
        private ClusterNode right;
        private double couplingValue;

        public ClusterNode(String name) {
            this.name = name;
        }

        public boolean isLeaf() {
            return left == null && right == null;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ClusterNode getLeft() {
            return left;
        }

        public void setLeft(ClusterNode left) {
            this.left = left;
        }

        public ClusterNode getRight() {
            return right;
        }

        public void setRight(ClusterNode right) {
            this.right = right;
        }

        public double getCouplingValue() {
            return couplingValue;
        }

        public void setCouplingValue(double couplingValue) {
            this.couplingValue = couplingValue;
        }

        @Override
        public String toString() {
            if (isLeaf()) {
                return name;
            }
            return name + " [" + couplingValue + "]";
        }
    }

    private static class MaxCouplingPair {
        String class1;
        String class2;
        double coupling;

        MaxCouplingPair(String class1, String class2, double coupling) {
            this.class1 = class1;
            this.class2 = class2;
            this.coupling = coupling;
        }
    }
}
