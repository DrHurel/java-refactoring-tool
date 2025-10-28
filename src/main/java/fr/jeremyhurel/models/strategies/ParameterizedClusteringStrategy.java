package fr.jeremyhurel.models.strategies;

import java.util.ArrayList;
import java.util.List;

import fr.jeremyhurel.models.ClusterTree.ClusterNode;

public class ParameterizedClusteringStrategy implements ClusteringStrategy {

    public enum Mode {
        AUTO,
        FIXED_COUNT,
        THRESHOLD,
        COMBINED
    }

    private final Mode mode;
    private Integer targetCount;
    private Double threshold;

    public ParameterizedClusteringStrategy(Mode mode) {
        this.mode = mode;
        if (mode == Mode.FIXED_COUNT || mode == Mode.THRESHOLD || mode == Mode.COMBINED) {
            throw new IllegalArgumentException("Mode " + mode + " requires additional parameters");
        }
    }

    public ParameterizedClusteringStrategy(Mode mode, int targetCount) {
        this.mode = mode;
        this.targetCount = targetCount;
        if (targetCount <= 0) {
            throw new IllegalArgumentException("Target count must be positive");
        }
    }

    public ParameterizedClusteringStrategy(Mode mode, double threshold) {
        this.mode = mode;
        this.threshold = threshold;
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be non-negative");
        }
    }

    public ParameterizedClusteringStrategy(int targetCount, double threshold) {
        this.mode = Mode.COMBINED;
        this.targetCount = targetCount;
        this.threshold = threshold;
        if (targetCount <= 0) {
            throw new IllegalArgumentException("Target count must be positive");
        }
        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold must be non-negative");
        }
    }

    @Override
    public List<ClusterNode> cut(ClusterNode root) {
        switch (mode) {
            case AUTO:
                return cutAutomatic(root);
            case FIXED_COUNT:
                return cutFixedCount(root, targetCount);
            case THRESHOLD:
                return cutByThreshold(root, threshold);
            case COMBINED:
                return cutCombined(root, targetCount, threshold);
            default:
                throw new IllegalStateException("Unknown mode: " + mode);
        }
    }

    private List<ClusterNode> cutAutomatic(ClusterNode root) {
        List<ClusterNode> internalNodes = new ArrayList<>();
        collectInternalNodes(root, internalNodes);

        List<ClusterNode> nonZeroNodes = new ArrayList<>();
        for (ClusterNode node : internalNodes) {
            if (node.getCouplingValue() > 0.00001) {
                nonZeroNodes.add(node);
            }
        }

        if (nonZeroNodes.isEmpty()) {
            List<ClusterNode> leaves = new ArrayList<>();
            collectLeaves(root, leaves);
            return leaves;
        }

        nonZeroNodes.sort((a, b) -> Double.compare(b.getCouplingValue(), a.getCouplingValue()));

        double maxRelativeDrop = 0.0;
        int elbowIndex = -1;

        for (int i = 0; i < nonZeroNodes.size() - 1; i++) {
            double current = nonZeroNodes.get(i).getCouplingValue();
            double next = nonZeroNodes.get(i + 1).getCouplingValue();

            double relativeDrop = (current - next) / current;
            if (relativeDrop > maxRelativeDrop) {
                maxRelativeDrop = relativeDrop;
                elbowIndex = i;
            }
        }

        double elbowThreshold;
        if (elbowIndex >= 0 && maxRelativeDrop > 0.15) {
            elbowThreshold = nonZeroNodes.get(elbowIndex + 1).getCouplingValue();
        } else if (nonZeroNodes.size() >= 10) {
            int percentileIndex = (int)(nonZeroNodes.size() * 0.30);
            elbowThreshold = nonZeroNodes.get(percentileIndex).getCouplingValue();
        } else if (nonZeroNodes.size() >= 5) {
            elbowThreshold = nonZeroNodes.get(nonZeroNodes.size() / 2).getCouplingValue();
        } else {
            elbowThreshold = nonZeroNodes.get(nonZeroNodes.size() - 1).getCouplingValue();
        }

        elbowThreshold = Math.max(elbowThreshold, 0.00001);

        List<ClusterNode> clusters = new ArrayList<>();
        collectClustersByThreshold(root, elbowThreshold, clusters);

        int totalLeaves = countLeaves(root);
        if (clusters.size() < Math.max(3, totalLeaves / 15) && nonZeroNodes.size() > 3) {
            int lowerIndex = Math.min((int)(nonZeroNodes.size() * 0.60), nonZeroNodes.size() - 1);
            elbowThreshold = nonZeroNodes.get(lowerIndex).getCouplingValue();
            elbowThreshold = Math.max(elbowThreshold, 0.00001);
            clusters.clear();
            collectClustersByThreshold(root, elbowThreshold, clusters);
        }

        return clusters;
    }

    private List<ClusterNode> cutFixedCount(ClusterNode root, int targetCount) {
        List<ClusterNode> clusters = new ArrayList<>();
        clusters.add(root);

        while (clusters.size() < targetCount) {

            ClusterNode minCluster = null;
            double minCoupling = Double.MAX_VALUE;

            for (ClusterNode cluster : clusters) {
                if (!cluster.isLeaf() && cluster.getCouplingValue() < minCoupling) {
                    minCoupling = cluster.getCouplingValue();
                    minCluster = cluster;
                }
            }

            if (minCluster == null) {
                break;
            }

            clusters.remove(minCluster);
            clusters.add(minCluster.getLeft());
            clusters.add(minCluster.getRight());
        }

        return clusters;
    }

    private List<ClusterNode> cutByThreshold(ClusterNode root, double threshold) {
        List<ClusterNode> clusters = new ArrayList<>();
        collectClustersByThreshold(root, threshold, clusters);
        return clusters;
    }

    private List<ClusterNode> cutCombined(ClusterNode root, int targetCount, double threshold) {
        List<ClusterNode> clusters = new ArrayList<>();
        clusters.add(root);

        while (clusters.size() < targetCount) {
            ClusterNode minCluster = null;
            double minCoupling = Double.MAX_VALUE;

            for (ClusterNode cluster : clusters) {
                if (!cluster.isLeaf() && cluster.getCouplingValue() < minCoupling) {
                    minCoupling = cluster.getCouplingValue();
                    minCluster = cluster;
                }
            }

            if (minCluster == null || minCluster.getCouplingValue() >= threshold) {
                break;
            }

            clusters.remove(minCluster);
            clusters.add(minCluster.getLeft());
            clusters.add(minCluster.getRight());
        }

        return clusters;
    }

    private int countLeaves(ClusterNode node) {
        if (node == null) {
            return 0;
        }
        if (node.isLeaf()) {
            return 1;
        }
        return countLeaves(node.getLeft()) + countLeaves(node.getRight());
    }

    private void collectInternalNodes(ClusterNode node, List<ClusterNode> result) {
        if (node == null || node.isLeaf()) {
            return;
        }

        result.add(node);
        collectInternalNodes(node.getLeft(), result);
        collectInternalNodes(node.getRight(), result);
    }

    private void collectLeaves(ClusterNode node, List<ClusterNode> result) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            result.add(node);
        } else {
            collectLeaves(node.getLeft(), result);
            collectLeaves(node.getRight(), result);
        }
    }

    private void collectClustersByThreshold(ClusterNode node, double threshold,
                                            List<ClusterNode> clusters) {
        if (node == null) {
            return;
        }

        if (node.isLeaf()) {
            clusters.add(node);
            return;
        }

        if (node.getCouplingValue() < threshold) {
            collectClustersByThreshold(node.getLeft(), threshold, clusters);
            collectClustersByThreshold(node.getRight(), threshold, clusters);
        } else {
            clusters.add(node);
        }
    }

    @Override
    public String getStrategyName() {
        switch (mode) {
            case AUTO:
                return "Automatic (Elbow Method)";
            case FIXED_COUNT:
                return "Fixed Count (" + targetCount + " modules)";
            case THRESHOLD:
                return "Coupling Threshold (" + String.format("%.3f", threshold) + ")";
            case COMBINED:
                return "Combined (max " + targetCount + " modules, threshold " + String.format("%.3f", threshold) + ")";
            default:
                return "Unknown";
        }
    }

    public Mode getMode() {
        return mode;
    }

    public Integer getTargetCount() {
        return targetCount;
    }

    public Double getThreshold() {
        return threshold;
    }
}
