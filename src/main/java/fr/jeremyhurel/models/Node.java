package fr.jeremyhurel.models;

public abstract class Node {
    protected Node[] children;

    public void addChild(Node child) {
        if (children == null) {
            children = new Node[1];
            children[0] = child;
        } else {
            Node[] newChildren = new Node[children.length + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            newChildren[children.length] = child;
            children = newChildren;
        }
    }

}
