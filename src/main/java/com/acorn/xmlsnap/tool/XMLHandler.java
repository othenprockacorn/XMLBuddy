package com.acorn.xmlsnap.tool;


import com.acorn.xmlsnap.model.XmlNode;


import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class XMLHandler implements  IXMLHandler{

    private final Map<Integer,List<XmlNode>> xmlData;
    private String rowElement = "";
    private String userElement = "";
    private boolean foundUserElement = false;
    private Integer nodeIndex = 0;


    public XMLHandler(String userElement){

        xmlData = new HashMap<>();
        this.userElement = userElement;

    }
    
    
    @Override
    public void readXMLFromFile(String fileLocation) {

        FileInputStream fileInputStream;
        XMLStreamReader xmlStreamReader;

       List<XmlNode> nodeList = new ArrayList<>();

        List<String> ignoreList = new ArrayList<>();

        try {
            fileInputStream = new FileInputStream(fileLocation);
            xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
            int eventCode = 0;

            String currentNode = "";
            String currentText = "";
            StringBuilder nodeAttributes = new StringBuilder();
            while(xmlStreamReader.hasNext() ) {

                eventCode = xmlStreamReader.next();

                switch (eventCode) {
                    case XMLStreamReader.END_ELEMENT:

                        if(xmlStreamReader.getLocalName().equalsIgnoreCase(rowElement)) {
                            xmlData.put(++nodeIndex, nodeList);
                        }
                        break;
                    case XMLStreamReader.START_ELEMENT:
                        currentNode = xmlStreamReader.getLocalName();

                        if (currentNode.equalsIgnoreCase(userElement)) {
                            foundUserElement = true;
                        }

                        if(!foundUserElement){
                            ignoreList.add(currentNode);
                        }

                        if (!currentNode.equalsIgnoreCase(userElement)
                                && foundUserElement
                                && rowElement.isEmpty()){
                            rowElement = currentNode;
                        }


                        nodeAttributes = new StringBuilder();
                        int attributes = xmlStreamReader.getAttributeCount();
                        for(int i=0; i<attributes; i++) {
                            String tmpAtt = xmlStreamReader.getAttributeName(i).toString()+"="+xmlStreamReader.getAttributeValue(i);
                            nodeAttributes.append(nodeAttributes.isEmpty() ? tmpAtt : ", " + tmpAtt);
                        }

                        if(xmlStreamReader.getLocalName().equalsIgnoreCase(rowElement)) {
                            nodeList = new ArrayList<>();
                            nodeList.add(new XmlNode(currentNode + "->", "", nodeAttributes.toString()));
                        }

                        break;
                    case XMLStreamReader.CHARACTERS:
                        currentText = xmlStreamReader.getText().trim();
                        if (!ignoreList.contains(currentNode) && !currentText.isEmpty())
                            nodeList.add(new XmlNode(currentNode, currentText, nodeAttributes.toString()) );
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
