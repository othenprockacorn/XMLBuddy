package com.acorn.xmlsnap.tool;


import com.acorn.xmlsnap.model.NodeAttribute;
import com.acorn.xmlsnap.model.NodeFilter;
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
    private final Map<Integer,List<XmlNode>> xmlFilteredData;
    private String rowElement = "";
    private String userElement = "";
    private boolean foundUserElement = false;
    private Integer nodeIndex = 0;
    private Integer nodeFilteredIndex = 0;


    public XMLHandler(String userElement){

        xmlData = new HashMap<>();
        xmlFilteredData = new HashMap<>();

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
            List<NodeAttribute>  attributeList = new ArrayList<>();

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
                        attributeList = new ArrayList<>();
                        int attributes = xmlStreamReader.getAttributeCount();
                        for(int i=0; i<attributes; i++) {
                            attributeList.add(new NodeAttribute( xmlStreamReader.getAttributeName(i).toString(), xmlStreamReader.getAttributeValue(i)));
                        }

                        if(xmlStreamReader.getLocalName().equalsIgnoreCase(rowElement)) {
                            nodeList = new ArrayList<>();
                            nodeList.add(new XmlNode(currentNode, "",attributeList));
                        }

                        break;
                    case XMLStreamReader.CHARACTERS:
                        currentText = xmlStreamReader.getText().trim();
                        if (!ignoreList.contains(currentNode) && !currentText.isEmpty())
                            nodeList.add(new XmlNode(currentNode, currentText, attributeList) );
                        break;

                }

            }

        } catch (FileNotFoundException | XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }

    public Integer filterXmlData(List<NodeFilter> nodeFilterList, String filterType){

        nodeFilteredIndex=0;
        boolean  addIsToResults;

        for (Map.Entry<Integer,List<XmlNode>> xnList : xmlData.entrySet()){


            for (NodeFilter nf : nodeFilterList){

                nf.setHitCount(0);

                for(XmlNode xn : xnList.getValue()) {

                    if (nf.getAttributeName() == null || nf.getAttributeName().isEmpty()){

                        if ((nf.getNameFilter().equalsIgnoreCase(xn.getNodeName().get()))
                                && (nf.getValueFilter().equalsIgnoreCase(xn.getNodeValue().get()))) {
                            nf.setHitCount(nf.getHitCount() + 1);
                        }
                    }
                    else {

                        for(NodeAttribute nodeAttribute : xn.getAttributesList()){

                            if ( nf.getNameFilter().equalsIgnoreCase(xn.getNodeName().get())
                                    && nf.getAttributeName().equalsIgnoreCase(nodeAttribute.attName())
                                    && nf.getValueFilter().equalsIgnoreCase(nodeAttribute.attValue())) {
                                nf.setHitCount(nf.getHitCount() + 1);
                            }

                        }

                    }

                }

            }

            if(addToResults(nodeFilterList, filterType)){
                xmlFilteredData.put(++nodeFilteredIndex, xnList.getValue());
            }

        }

        return nodeFilteredIndex;
    }

    private static boolean addToResults(List<NodeFilter> nodeFilterList, String filterType) {

        boolean addResult = false;

        if(filterType.equalsIgnoreCase("and")) {
            addResult = true;
            for (NodeFilter nf : nodeFilterList) {
                if ((!nf.getIsNot() && nf.getHitCount() == 0) || (nf.getIsNot() && nf.getHitCount() > 0)) {
                    addResult = false;
                    break;
                }
            }
        }
        else if(filterType.equalsIgnoreCase("or")) {

            for (NodeFilter nf : nodeFilterList) {
                if ((!nf.getIsNot() && nf.getHitCount() > 0) || (nf.getIsNot() && nf.getHitCount() == 0)) {
                    addResult = true;
                    break;
                }
            }
        }
        return addResult;
    }

    public void removeFilterXmlData(){

        xmlFilteredData.clear();
        nodeFilteredIndex = 0;
    }

    public Integer getXmlIndex(){

        return nodeIndex;
    }
    public Integer getXmlFilteredIndex(){

        return nodeFilteredIndex;
    }

    public List<XmlNode> getElement(Integer index){

        if (nodeFilteredIndex > 0){
            if (index <= nodeFilteredIndex) {
                return xmlFilteredData.get(index);
            }
        }
        else{
            if (index <= nodeIndex) {
                return xmlData.get(index);
            }
        }

       return null;
    }


}
