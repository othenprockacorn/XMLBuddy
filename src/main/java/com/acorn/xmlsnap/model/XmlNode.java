package com.acorn.xmlsnap.model;

import javafx.beans.property.SimpleStringProperty;

import java.util.ArrayList;
import java.util.List;

public class XmlNode {

    private final SimpleStringProperty nodeName = new SimpleStringProperty("");
    private final SimpleStringProperty nodeValue = new SimpleStringProperty("");


    List<NodeAttribute> nodeAttributeList = new ArrayList<>();

    public XmlNode(String nodeName, String nodeValue, List<NodeAttribute> nodeAttributeList){
        this.nodeName.set(nodeName);
        this.nodeValue.set(nodeValue);
        this.nodeAttributeList = new ArrayList<>(nodeAttributeList);
    }

    public SimpleStringProperty getNodeName() {return nodeName;}
    public SimpleStringProperty getNodeValue() {return nodeValue;}
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
