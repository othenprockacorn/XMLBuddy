package com.acorn.xmlsnap.tool;


import com.acorn.xmlsnap.model.NodeAttribute;
import com.acorn.xmlsnap.model.NodeFilter;
import com.acorn.xmlsnap.model.XmlNode;




import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class XMLHandler implements  IXMLHandler{

    private final Map<Integer,List<XmlNode>> xmlData;
    private final Map<Integer,List<XmlNode>> xmlFilteredData;
    private String rowElement = "";
    private final String userElement;
    private boolean foundUserElement = false;
    private Integer nodeIndex = 0;
    private Integer nodeFilteredIndex = 0;

    private List<String> ignoreList ;


    public XMLHandler(String userElement){

        xmlData = new HashMap<>();
        xmlFilteredData = new HashMap<>();
        ignoreList = new ArrayList<>();

        this.userElement = userElement;

    }
    
    
    @Override
    public void readXMLFromFile(String fileLocation) {

        FileInputStream fileInputStream;
        XMLStreamReader xmlStreamReader;

       List<XmlNode> nodeList;
       List<NodeAttribute> attributeList;

        try {
            fileInputStream = new FileInputStream(fileLocation);
            xmlStreamReader = XMLInputFactory.newInstance().createXMLStreamReader(fileInputStream);
            int eventCode = 0;

            String currentNode = "";


            while(xmlStreamReader.hasNext() ) {

                int xmlEvent = xmlStreamReader.next();

                if (xmlEvent == XMLStreamConstants.START_ELEMENT) {

                    currentNode = xmlStreamReader.getLocalName();

                    if (rowElement.isEmpty()) {
                        if (currentNode.equalsIgnoreCase(userElement)) {
                            foundUserElement = true;
                        }
                        if (!foundUserElement) {
                            ignoreList.add(currentNode);
                        }
                        if (!currentNode.equalsIgnoreCase(userElement)
                                && foundUserElement
                                && rowElement.isEmpty()) {
                            rowElement = currentNode;
                        }
                    }

                    attributeList = new ArrayList<>();
                    int attributes = xmlStreamReader.getAttributeCount();

                    for(int i=0; i<attributes; i++) {
                        attributeList.add(new NodeAttribute( xmlStreamReader.getAttributeName(i).toString(), xmlStreamReader.getAttributeValue(i)));
                    }

                    if(xmlStreamReader.getLocalName().equalsIgnoreCase(rowElement)) {
                        nodeList = new ArrayList<>();
                        nodeList.add(new XmlNode("["+xmlStreamReader.getLocalName()+"]", "", attributeList));
                        nodeList = new ArrayList<>(getNodeDetails(xmlStreamReader, nodeList));
                        nodeList.add(new XmlNode("[end "+xmlStreamReader.getLocalName()+"]", "", new ArrayList<>()));
                        xmlData.put(++nodeIndex,nodeList);
                    }

                }

            }

            xmlStreamReader.close();

        } catch (FileNotFoundException | XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }

    private  List<XmlNode> getNodeDetails(XMLStreamReader xmlStreamReader, List<XmlNode> nodeList) throws XMLStreamException{


        List<XmlNode> xn = new ArrayList<>(nodeList);
        List<String> nodeIgnore = new ArrayList<>();

        List<NodeAttribute>  attributeList = new ArrayList<>();

        String currentNode = "";
        String currentText = "";
        boolean hadEnded = true;

        while(xmlStreamReader.hasNext()) {

            int xmlEvent = xmlStreamReader.next();

            switch(xmlEvent){
                case XMLStreamConstants.END_ELEMENT:
                    hadEnded = true;
                    currentNode = xmlStreamReader.getLocalName();
                    if( currentNode.equalsIgnoreCase(rowElement)) {
                        return xn;
                    }
                    else{
                        if (nodeIgnore.contains(currentNode)){
                            xn.add(new XmlNode("[end "+currentNode+"]", currentText, attributeList));
                            nodeIgnore.remove(currentNode);
                        }
                        else {
                            xn.add(new XmlNode(currentNode, currentText, attributeList));
                        }
                    }
                    break;
                case  XMLStreamConstants.START_ELEMENT:

                    if(!hadEnded){
                        nodeIgnore.add(currentNode);
                        xn.add(new XmlNode("["+currentNode+"]", "", attributeList));
                    }

                    currentText ="";
                    currentNode = xmlStreamReader.getLocalName();
                    attributeList = new ArrayList<>();
                    int attributes = xmlStreamReader.getAttributeCount();

                    for(int i=0; i<attributes; i++) {
                        attributeList.add(new NodeAttribute( xmlStreamReader.getAttributeName(i).toString(), xmlStreamReader.getAttributeValue(i)));
                    }
                    hadEnded = false;
                    break;
                case  XMLStreamConstants.CHARACTERS:
                if (!ignoreList.contains(currentNode)) {
                    currentText = xmlStreamReader.getText().trim();

                }
            }
        }

        return xn;

    }


    public Integer filterXmlData(List<NodeFilter> nodeFilterList, String filterType){

        nodeFilteredIndex=0;
        boolean  addIsToResults;

        for (Map.Entry<Integer,List<XmlNode>> xnList : xmlData.entrySet()){


            for (NodeFilter nf : nodeFilterList){

                nf.setHitCount(0);

                for(XmlNode xn : xnList.getValue()) {

                    String nodeName = xn.getNodeName().get().replaceAll("[\\[\\](){}]","");

                    if (nf.getAttributeName() == null || nf.getAttributeName().isEmpty()){

                        if (nf.getNameFilter().equalsIgnoreCase(nodeName)
                                && (nf.getValueFilter().equalsIgnoreCase(xn.getNodeValue().get())
                                || (nf.getValueFilter().equals("\"\"") && xn.getNodeValue().get().isEmpty())
                        )) {
                            nf.setHitCount(nf.getHitCount() + 1);
                        }
                    }
                    else {

                        for(NodeAttribute nodeAttribute : xn.getAttributesList()){

                            if ( nf.getNameFilter().equalsIgnoreCase(nodeName)
                                    && nf.getAttributeName().equalsIgnoreCase(nodeAttribute.attName())
                                    &&
                                    (nf.getValueFilter().equalsIgnoreCase(nodeAttribute.attValue())
                                    || nf.getValueFilter().equals("\"\"") && nodeAttribute.attValue().isEmpty())
                                    ) {
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

    private  boolean addToResults(List<NodeFilter> nodeFilterList, String filterType) {

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
