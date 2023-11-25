package com.acorn.xmlsnap.model;

import javafx.beans.property.SimpleStringProperty;

public class XmlNode {

    private final SimpleStringProperty nodeName = new SimpleStringProperty("");
    private final SimpleStringProperty nodeValue = new SimpleStringProperty("");
    private final SimpleStringProperty nodeAttributes = new SimpleStringProperty("");

    public XmlNode(String nodeName, String nodeValue, String nodeAttributes){
        this.nodeName.set(nodeName);
        this.nodeValue.set(nodeValue);
        this.nodeAttributes.set(nodeAttributes);
    }

    public SimpleStringProperty getNodeName() {
        return nodeName;
    }

    public SimpleStringProperty getNodeValue() {return nodeValue;}

    public SimpleStringProperty getAttributes() {return nodeAttributes;}


}
