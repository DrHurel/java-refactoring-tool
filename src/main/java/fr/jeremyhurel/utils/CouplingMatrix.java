package fr.jeremyhurel.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CouplingMatrix {

    private final Map<String, Map<String, Double>> _matrix;

    public CouplingMatrix() {
        this._matrix = new HashMap<>();
    }

    /**
     * @deprecated Use CouplingMatrix() instead. Size is now dynamic.
     */
    @Deprecated
    public CouplingMatrix(int size) {
        this._matrix = new HashMap<>(size);
    }

    public void addCoupling(String from, String to, double weight) {
        _matrix.putIfAbsent(from, new HashMap<>());
        Map<String, Double> row = _matrix.get(from);
        row.put(to, row.getOrDefault(to, 0.0) + weight);
    }

    public Map<String, Map<String, Double>> get_matrix() {
        return _matrix;
    }

    /**
     * Returns the dynamic size of the matrix (number of source nodes).
     * 
     * @return the number of classes/nodes with outgoing couplings
     */
    public int get_size() {
        return _matrix.size();
    }

    /**
     * Returns the total number of nodes (classes) in the matrix.
     * This includes both source and target nodes.
     * 
     * @return the total number of unique classes in the coupling matrix
     */
    public int getTotalNodeCount() {
        Set<String> allNodes = new java.util.HashSet<>(_matrix.keySet());
        for (Map<String, Double> targets : _matrix.values()) {
            allNodes.addAll(targets.keySet());
        }
        return allNodes.size();
    }

    public boolean isEmpty() {
        return _matrix.isEmpty();
    }

    public void clear() {
        _matrix.clear();
    }

    public double getCouplingWeight(String from, String to) {
        if (_matrix.containsKey(from) && _matrix.get(from).containsKey(to)) {
            return _matrix.get(from).get(to);
        }
        return 0.0;
    }
}