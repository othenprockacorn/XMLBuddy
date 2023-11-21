package com.acorn.xmlbuddy.model;

import javafx.beans.property.SimpleStringProperty;

public class XmlNode {

    private final SimpleStringProperty nodeName = new SimpleStringProperty("");
    private final SimpleStringProperty nodeValue = new SimpleStringProperty("");


    public XmlNode(String nodeName, String nodeValue){
        this.nodeName.set(nodeName);
        this.nodeValue.set(nodeValue);
    }

    public SimpleStringProperty getName() {
        return nodeName;
    }

    public SimpleStringProperty getValue() {

        return nodeValue;
    }




}
