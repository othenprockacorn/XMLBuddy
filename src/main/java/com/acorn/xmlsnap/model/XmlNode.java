package com.acorn.xmlsnap.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class XmlNode {

    private final SimpleStringProperty nodeName = new SimpleStringProperty("");
    private final SimpleStringProperty nodeValue = new SimpleStringProperty("");
    private final SimpleStringProperty nodeParentName = new SimpleStringProperty("");
    private final boolean hasChildren;
    private final boolean endNode;
    private final Integer nodePosition;
    private boolean isFilter;
    List<NodeAttribute> nodeAttributeList;

    public XmlNode(String nodeName, String nodeValue, String nodeParentName, boolean hasChildren,
                   boolean endNode, Integer nodePosition, List<NodeAttribute> nodeAttributeList){
        this.nodeName.set(nodeName);
        this.nodeValue.set(nodeValue);
        this.nodeParentName.set(nodeParentName);
        this.hasChildren = hasChildren;
        this.nodePosition = nodePosition;
        this.endNode = endNode;
        this.isFilter = false;
        this.nodeAttributeList = new ArrayList<>(nodeAttributeList);
    }

    public SimpleStringProperty getNodeName() {

        SimpleStringProperty returnProperty = new SimpleStringProperty();

        String countDisplay = hasChildren && nodePosition > 1 ? " (" + nodePosition + ")" : "";

        String rtnValue = nodeName.getValue() + countDisplay;

        if (hasChildren && endNode){
            rtnValue =   "⟵" + rtnValue;
        }
        else if (hasChildren && !endNode){
            rtnValue =   "⟶" + rtnValue;
        }



        returnProperty.set(rtnValue);

        return returnProperty;

    }

    public SimpleStringProperty getSearchNodeName() {return nodeName;}
    public SimpleStringProperty getNodeValue() {return nodeValue;}
    public SimpleStringProperty getNodeParentName() {return nodeParentName;}
    public boolean getHasChildren(){ return hasChildren;}
    public boolean getEndNode(){ return endNode;}
    public Integer getNodePosition(){ return nodePosition;}

    public boolean isFilter() {
        return isFilter;
    }

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public SimpleStringProperty getAttributes() {

        StringBuilder nodeAttributes = new StringBuilder();

        for(NodeAttribute na : nodeAttributeList){
            String tmpAtt = na.attName() + "=" + na.attValue();
            nodeAttributes.append(nodeAttributes.isEmpty() ? tmpAtt : ", " + tmpAtt);
        }

        return new SimpleStringProperty(nodeAttributes.toString());

    }

    public List<NodeAttribute> getAttributesList() {

        return nodeAttributeList;

    }

}
