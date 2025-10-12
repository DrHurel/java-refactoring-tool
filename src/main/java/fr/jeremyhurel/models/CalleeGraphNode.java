package fr.jeremyhurel.models;

import java.util.ArrayList;
import java.util.List;

public class CalleeGraphNode extends Node {

    private String className;
    private String methodName;
    private String signature;
    private List<CalleeGraphNode> callees;
    private int lineNumber;

    public CalleeGraphNode(String className, String methodName, String signature, int lineNumber) {
        this.className = className;
        this.methodName = methodName;
        this.signature = signature;
        this.lineNumber = lineNumber;
        this.callees = new ArrayList<>();
    }

    public void addCallee(CalleeGraphNode callee) {
        if (!callees.contains(callee)) {
            callees.add(callee);
        }
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getSignature() {
        return signature;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public List<CalleeGraphNode> getCallees() {
        return callees;
    }

    public String getFullName() {
        return className + "." + methodName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        CalleeGraphNode that = (CalleeGraphNode) obj;
        return className.equals(that.className) &&
                methodName.equals(that.methodName) &&
                signature.equals(that.signature);
    }

    @Override
    public int hashCode() {
        return (className + methodName + signature).hashCode();
    }

    @Override
    public String toString() {
        return getFullName() + signature;
    }
}
