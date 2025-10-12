package fr.jeremyhurel.models;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CallGraph {

    private Map<String, CalleeGraphNode> nodes;
    private CalleeGraphNode rootNode;

    public CallGraph() {
        this.nodes = new HashMap<>();
    }

    public void addNode(CalleeGraphNode node) {
        String key = node.getClassName() + "." + node.getMethodName() + node.getSignature();
        nodes.put(key, node);
    }

    public CalleeGraphNode getNode(String className, String methodName, String signature) {
        String key = className + "." + methodName + signature;
        return nodes.get(key);
    }

    public CalleeGraphNode getOrCreateNode(String className, String methodName, String signature, int lineNumber) {
        String key = className + "." + methodName + signature;
        CalleeGraphNode node = nodes.get(key);
        if (node == null) {
            node = new CalleeGraphNode(className, methodName, signature, lineNumber);
            nodes.put(key, node);
        }
        return node;
    }

    public void setRootNode(CalleeGraphNode rootNode) {
        this.rootNode = rootNode;
    }

    public CalleeGraphNode getRootNode() {
        return rootNode;
    }

    public Set<String> getAllNodeKeys() {
        return nodes.keySet();
    }

    public Map<String, CalleeGraphNode> getNodes() {
        return nodes;
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public boolean isEmpty() {
        return nodes.isEmpty();
    }
}