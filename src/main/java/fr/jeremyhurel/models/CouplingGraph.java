package fr.jeremyhurel.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CouplingGraph {

    private final Map<String, CouplingNode> nodes;
    // Raw count matrix: from class -> to class -> number of method calls
    private final Map<String, Map<String, Integer>> callCountMatrix;
    // Normalized coupling matrix: from class -> to class -> coupling value [0,1]
    private final Map<String, Map<String, Double>> couplingMatrix;
    private int totalMethodCalls = 0;

    public CouplingGraph() {
        this.nodes = new HashMap<>();
        this.callCountMatrix = new HashMap<>();
        this.couplingMatrix = new HashMap<>();
    }

    public void addNode(CouplingNode node) {
        nodes.put(node.getClassName(), node);
    }

    public CouplingNode getNode(String className) {
        return nodes.get(className);
    }

    public CouplingNode getOrCreateNode(String className) {
        CouplingNode node = nodes.get(className);
        if (node == null) {
            node = new CouplingNode(className);
            nodes.put(className, node);
        }
        return node;
    }

    /**
     * Adds a method call relation between two classes.
     * This increments the count of method calls from class 'from' to class 'to'.
     * 
     * @param from the calling class
     * @param to the called class
     */
    public void addMethodCall(String from, String to) {
        callCountMatrix.putIfAbsent(from, new HashMap<>());
        Map<String, Integer> row = callCountMatrix.get(from);
        row.put(to, row.getOrDefault(to, 0) + 1);
        totalMethodCalls++;
    }

    /**
     * Calculates normalized coupling values based on the formula:
     * Couplage(A,B) = Number of method calls between A and B / Total method calls in application
     */
    public void calculateNormalizedCoupling() {
        couplingMatrix.clear();
        
        if (totalMethodCalls == 0) {
            return; // Avoid division by zero
        }

        for (Map.Entry<String, Map<String, Integer>> entry : callCountMatrix.entrySet()) {
            String fromClass = entry.getKey();
            couplingMatrix.putIfAbsent(fromClass, new HashMap<>());
            
            for (Map.Entry<String, Integer> callEntry : entry.getValue().entrySet()) {
                String toClass = callEntry.getKey();
                int callCount = callEntry.getValue();
                double normalizedCoupling = (double) callCount / totalMethodCalls;
                couplingMatrix.get(fromClass).put(toClass, normalizedCoupling);
            }
        }
    }

    /**
     * @deprecated Use addMethodCall() instead
     */
    @Deprecated
    public void addCoupling(String from, String to, double weight) {
        // For backward compatibility, convert weight to call count
        int callCount = (int) Math.round(weight * 10);
        for (int i = 0; i < callCount; i++) {
            addMethodCall(from, to);
        }
    }

    public double getCouplingWeight(String from, String to) {
        if (couplingMatrix.containsKey(from) && couplingMatrix.get(from).containsKey(to)) {
            return couplingMatrix.get(from).get(to);
        }
        return 0.0;
    }

    public Set<String> getAllNodeNames() {
        return nodes.keySet();
    }

    public Map<String, CouplingNode> getNodes() {
        return nodes;
    }

    /**
     * Returns the raw call count matrix (before normalization)
     */
    public Map<String, Map<String, Integer>> getCallCountMatrix() {
        return callCountMatrix;
    }

    /**
     * Returns the normalized coupling matrix (after calling calculateNormalizedCoupling())
     */
    public Map<String, Map<String, Double>> getCouplingMatrix() {
        return couplingMatrix;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public int getCouplingCount() {
        int count = 0;
        for (Map<String, Integer> row : callCountMatrix.values()) {
            count += row.size();
        }
        return count;
    }

    /**
     * Returns the total number of method calls in the application
     */
    public int getTotalMethodCalls() {
        return totalMethodCalls;
    }

    /**
     * Returns the raw call count between two classes
     */
    public int getCallCount(String from, String to) {
        if (callCountMatrix.containsKey(from) && callCountMatrix.get(from).containsKey(to)) {
            return callCountMatrix.get(from).get(to);
        }
        return 0;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Removes nodes that are not part of the analyzed codebase.
     * This is useful for cleaning up external library classes that were referenced
     * but are not in the main coupling matrix as source classes.
     */
    public void removeOrphanedNodes() {
        // Remove nodes that don't have any outgoing couplings
        // (they are only referenced but not part of the analyzed codebase)
        nodes.keySet().removeIf(className -> !callCountMatrix.containsKey(className));
    }
}
