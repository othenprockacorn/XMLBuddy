package com.acorn.xmlsnap.model;

public class NodeResult {

    private final String nodeText;
    private final String parent;
    private final boolean hasChild;

    public NodeResult() {
        this.nodeText = "";
        this.parent = "";
        this.hasChild = false;

    }

    public NodeResult(String nodeText, String parent, boolean hasChild) {
        this.nodeText = nodeText;
        this.parent = parent;
        this.hasChild = hasChild;
    }

    public String getNodeText() {
        return nodeText;
    }

    public String getParent() {
        return parent;
    }

    public boolean isHasChild() {
        return hasChild;
    }

}
