package com.acorn.xmlbuddy.tool;


import com.acorn.xmlbuddy.model.XmlNode;
import javafx.beans.property.SimpleStringProperty;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class XMLHandler implements  IXMLHandler{

    private Map<Integer,List<XmlNode>> xmlData;
    private String userElement = null;
    private String rowElement = null;
    private Integer nodeIndex = 1;
    public XMLHandler(){}


    public XMLHandler(String userElement, String rowElement){

        xmlData = new HashMap<>();
        this.userElement = userElement;
        this.rowElement = rowElement;

    }
    
    
    @Override
    public void readXMLFromFile(String fileLocation) {

        FileInputStream fileInputStream;
        XMLStreamReader xmlStreamReader;

       List<XmlNode> nodeList = new ArrayList<>();

        try {
            fileInputStream = new FileInputStream(fileLocation);
            xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
            int eventCode = 0;

            String currentNode = "";
            String currentText = "";

            while(xmlStreamReader.hasNext() ) {

                eventCode = xmlStreamReader.next();

                switch (eventCode) {
                    case XMLStreamReader.END_ELEMENT:
                        currentNode = xmlStreamReader.getLocalName();
                        if(currentNode.equalsIgnoreCase(rowElement)) {
                            xmlData.put(nodeIndex++,nodeList);
                            nodeList = new ArrayList<>();
                        }

                        break;
                    case XMLStreamReader.START_ELEMENT:
                        currentNode = xmlStreamReader.getLocalName();
                        break;
                    case XMLStreamReader.CHARACTERS:
                        currentText = xmlStreamReader.getText().trim();
                        if (currentNode != null && !currentText.isEmpty())
                            nodeList.add(new XmlNode(currentNode, currentText, "") );
                        break;

                }

                if (eventCode == XMLStreamReader.END_ELEMENT && currentNode.equalsIgnoreCase(userElement)) {
                    break;
                }

            }

        } catch (FileNotFoundException | XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }


    public Integer getXmlIndex(){
        return nodeIndex;
    }


    public List<XmlNode> getElement(Integer index){

        if (index <= nodeIndex)
            return xmlData.get(index);

       return null;
    }


}
