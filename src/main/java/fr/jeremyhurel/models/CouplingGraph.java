package fr.jeremyhurel.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CouplingGraph {

    private final Map<String, CouplingNode> nodes;

    private final Map<String, Map<String, Integer>> callCountMatrix;

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

    public void addMethodCall(String from, String to) {
        callCountMatrix.putIfAbsent(from, new HashMap<>());
        Map<String, Integer> row = callCountMatrix.get(from);
        row.put(to, row.getOrDefault(to, 0) + 1);
        totalMethodCalls++;
    }

    public void calculateNormalizedCoupling() {
        couplingMatrix.clear();

        if (totalMethodCalls == 0) {
            return;
        }

        for (Map.Entry<String, Map<String, Integer>> entry : callCountMatrix.entrySet()) {
            String fromClass = entry.getKey();
            couplingMatrix.putIfAbsent(fromClass, new HashMap<>());

            float totalOutgoingCoupling = 0.0f;

            for (Map.Entry<String, Integer> callEntry : entry.getValue().entrySet()) {
                String toClass = callEntry.getKey();
                int callCount = callEntry.getValue();
                double normalizedCoupling = (double) callCount / totalMethodCalls;
                couplingMatrix.get(fromClass).put(toClass, normalizedCoupling);
                totalOutgoingCoupling += (float) normalizedCoupling;
            }

            CouplingNode node = nodes.get(fromClass);
            if (node != null) {
                node.setCouplingValue(totalOutgoingCoupling);
            }
        }
    }

    @Deprecated
    public void addCoupling(String from, String to, double weight) {

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

    public Map<String, Map<String, Integer>> getCallCountMatrix() {
        return callCountMatrix;
    }

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

    public int getTotalMethodCalls() {
        return totalMethodCalls;
    }

    public int getCallCount(String from, String to) {
        if (callCountMatrix.containsKey(from) && callCountMatrix.get(from).containsKey(to)) {
            return callCountMatrix.get(from).get(to);
        }
        return 0;
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    public void removeOrphanedNodes() {

        nodes.keySet().removeIf(className -> !callCountMatrix.containsKey(className));
    }

    public CouplingNode mergeNodes(String node1, String node2) {
        CouplingNode n1 = nodes.get(node1);
        CouplingNode n2 = nodes.get(node2);

        if (n1 == null || n2 == null) {
            throw new IllegalArgumentException("Both nodes must exist in the graph");
        }

        return n1.merge(n2);
    }

    public float getNodeCouplingValue(String className) {
        CouplingNode node = nodes.get(className);
        return node != null ? node.getCouplingValue() : 0.0f;
    }

    public Map<String, Float> getNodeCouplingValues() {
        Map<String, Float> result = new HashMap<>();
        for (Map.Entry<String, CouplingNode> entry : nodes.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getCouplingValue());
        }
        return result;
    }
}
