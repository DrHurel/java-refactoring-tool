package fr.jeremyhurel.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a hierarchical cluster tree built from coupling matrix using
 * agglomerative clustering (bottom-up approach).
 * 
 * Algorithm:
 * 1. Start with each class as a single cluster
 * 2. Find the pair with maximum coupling value
 * 3. Merge them into a new cluster
 * 4. Update coupling matrix using average linkage
 * 5. Repeat until only one cluster remains
 */
public class ClusterTree {
    
    private ClusterNode root;
    private List<ClusterNode> mergeHistory;
    private int nodeIdCounter = 0;
    
    public ClusterTree() {
        this.mergeHistory = new ArrayList<>();
    }
    
    /**
     * Builds the cluster tree from a coupling graph using hierarchical clustering
     */
    public void buildFromCouplingGraph(CouplingGraph couplingGraph) {
        // Get the normalized coupling matrix
        Map<String, Map<String, Double>> couplingMatrix = couplingGraph.getCouplingMatrix();
        
        if (couplingMatrix.isEmpty()) {
            return;
        }
        
        // Create initial clusters (one per class)
        Map<String, ClusterNode> activeClusters = new HashMap<>();
        for (String className : couplingGraph.getAllNodeNames()) {
            ClusterNode node = new ClusterNode(className);
            node.setId(nodeIdCounter++);
            activeClusters.put(className, node);
        }
        
        // Working coupling matrix (will be modified during merging)
        Map<String, Map<String, Double>> workingMatrix = deepCopyCouplingMatrix(couplingMatrix);
        
        // Hierarchical clustering loop
        while (activeClusters.size() > 1) {
            // Find the pair with maximum coupling value
            MaxCouplingPair maxPair = findMaxCoupling(workingMatrix, activeClusters);
            
            if (maxPair == null) {
                // No more couplings found, merge remaining clusters arbitrarily
                List<String> remainingKeys = new ArrayList<>(activeClusters.keySet());
                if (remainingKeys.size() >= 2) {
                    maxPair = new MaxCouplingPair(remainingKeys.get(0), remainingKeys.get(1), 0.0);
                } else {
                    break;
                }
            }
            
            // Create new cluster from the two nodes with max coupling
            ClusterNode node1 = activeClusters.get(maxPair.class1);
            ClusterNode node2 = activeClusters.get(maxPair.class2);
            
            String newClusterName = "Cluster_" + node1.getName() + "_" + node2.getName();
            ClusterNode newCluster = new ClusterNode(newClusterName);
            newCluster.setId(nodeIdCounter++);
            newCluster.setLeft(node1);
            newCluster.setRight(node2);
            newCluster.setCouplingValue(maxPair.coupling);
            
            mergeHistory.add(newCluster);
            
            // Remove the merged nodes from active clusters
            activeClusters.remove(maxPair.class1);
            activeClusters.remove(maxPair.class2);
            
            // Update the coupling matrix
            updateCouplingMatrix(workingMatrix, maxPair.class1, maxPair.class2, newClusterName, activeClusters);
            
            // Add new cluster to active clusters
            activeClusters.put(newClusterName, newCluster);
        }
        
        // The last remaining cluster is the root
        if (!activeClusters.isEmpty()) {
            root = activeClusters.values().iterator().next();
        }
    }
    
    /**
     * Finds the pair of clusters with maximum coupling value
     */
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
        
        // Also check reverse direction to ensure we get symmetric max
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
    
    /**
     * Updates the coupling matrix after merging two clusters.
     * Uses average linkage: coupling(newCluster, X) = (coupling(C1, X) + coupling(C2, X)) / 2
     */
    private void updateCouplingMatrix(Map<String, Map<String, Double>> matrix,
                                     String cluster1, String cluster2, String newClusterName,
                                     Map<String, ClusterNode> activeClusters) {
        // Remove old entries for cluster1 and cluster2
        matrix.remove(cluster1);
        matrix.remove(cluster2);
        
        // Remove references to cluster1 and cluster2 from other rows
        for (Map<String, Double> row : matrix.values()) {
            row.remove(cluster1);
            row.remove(cluster2);
        }
        
        // Create new row for the new cluster
        Map<String, Double> newRow = new HashMap<>();
        
        // Calculate coupling between new cluster and all other active clusters
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
        
        // Also add reverse direction entries
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
    
    /**
     * Gets coupling value from matrix (handles bidirectional lookup)
     */
    private double getCouplingValue(Map<String, Map<String, Double>> matrix, String from, String to) {
        if (matrix.containsKey(from) && matrix.get(from).containsKey(to)) {
            return matrix.get(from).get(to);
        }
        if (matrix.containsKey(to) && matrix.get(to).containsKey(from)) {
            return matrix.get(to).get(from);
        }
        return 0.0;
    }
    
    /**
     * Deep copy of coupling matrix to avoid modifying the original
     */
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
    
    /**
     * Inner class to represent a cluster node in the tree
     */
    public static class ClusterNode {
        private int id;
        private String name;
        private ClusterNode left;
        private ClusterNode right;
        private double couplingValue; // The coupling value that caused this merge
        
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
    
    /**
     * Helper class to store max coupling pair information
     */
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
