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

    private final HashSet<String> nodeNameList = new HashSet<>();
    private String rowElement = "";
    private final String userElement;
    private boolean foundUserElement = false;
    private Integer nodeIndex = 0;
    private Integer nodeFilteredIndex = 0;

    private final List<String> ignoreList ;


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
                        nodeList.add(new XmlNode(xmlStreamReader.getLocalName(), "", userElement,
                                true, false, 1, attributeList));
                        nodeList = new ArrayList<>(getNodeDetails(xmlStreamReader, nodeList));
                        nodeList.add(new XmlNode(xmlStreamReader.getLocalName(), "", userElement,
                                true, true, 1, new ArrayList<>()));
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
        List<NodeAttribute>  attributeList = new ArrayList<>();
        List<String> nodeIgnore = new ArrayList<>();
        List<String> nodeParentList = new ArrayList<>();
        nodeParentList.add(rowElement);

        List<String> nodeCountList = new ArrayList<>();
        nodeParentList.add(rowElement);


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

                            nodeParentList.remove(nodeParentList.size() - 1);

                            xn.add(new XmlNode(
                                    currentNode,
                                    currentText,
                                    nodeParentList.get(nodeParentList.size() - 1),
                                   true,
                                    true,
                                    Collections.frequency(nodeCountList, nodeParentList.get(nodeParentList.size() - 1)+currentNode),
                                    new ArrayList<>()
                                    )
                            );
                        }
                        else{
                            nodeNameList.add(currentNode);
                            xn.add(new XmlNode(
                                    currentNode,
                                    currentText,
                                    nodeParentList.get(nodeParentList.size() - 1),
                                    false,
                                    false,
                                    Collections.frequency(nodeCountList, nodeParentList.get(nodeParentList.size() - 1)+currentNode),
                                    attributeList
                                    )
                            );

                        }

                    }

                    break;
                case  XMLStreamConstants.START_ELEMENT:

                    if(!hadEnded){
                        nodeIgnore.add(currentNode);
                        xn.add(new XmlNode(
                                currentNode,
                                "",
                                nodeParentList.get(nodeParentList.size() - 1),
                                true,
                                false,
                                Collections.frequency(nodeCountList, nodeParentList.get(nodeParentList.size() - 1)+currentNode),
                                attributeList));

                        nodeParentList.add(currentNode);
                    }

                    currentText ="";
                    currentNode = xmlStreamReader.getLocalName();

                    nodeCountList.add(nodeParentList.get(nodeParentList.size() - 1)+currentNode);

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

    public Integer filterXmlData(List<NodeFilter> nodeFilterList){

        nodeFilteredIndex=0;
        boolean  addIsToResults;
        String filterType = "";
        removeFilterXmlData();


        for (Map.Entry<Integer,List<XmlNode>> xnList : xmlData.entrySet()){


            for (NodeFilter nf : nodeFilterList){
                filterType = nf.getTypeFilter().get().toLowerCase();
                nf.setHitCount(0);

                for(XmlNode xn : xnList.getValue()) {

                    String nodeName = xn.getSearchNodeName().get();

                    if (nf.getAttributeName().get() == null || nf.getAttributeName().get().isEmpty()){

                        if (nf.getNameFilter().get().equalsIgnoreCase(nodeName)
                                && (nf.getValueFilter().get().equalsIgnoreCase(xn.getNodeValue().get())
                                || (nf.getValueFilter().get().equals("\"\"") && xn.getNodeValue().get().isEmpty())
                        )) {
                            xn.setFilter(true);
                            nf.setHitCount(nf.getHitCount() + 1);
                        }
                    }
                    else {

                        for(NodeAttribute nodeAttribute : xn.getAttributesList()){

                            if ( nf.getNameFilter().get().equalsIgnoreCase(nodeName)
                                    && nf.getAttributeName().get().equalsIgnoreCase(nodeAttribute.attName())
                                    &&
                                    (nf.getValueFilter().get().equalsIgnoreCase(nodeAttribute.attValue())
                                    || nf.getValueFilter().get().equals("\"\"") && nodeAttribute.attValue().isEmpty())
                                    ) {
                                xn.setFilter(true);
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

    public HashSet<String> getNoUsIdeNameList(){
        return nodeNameList;
    }

    private  boolean addToResults(List<NodeFilter> nodeFilterList, String filterType) {

        boolean addResult = false;

        if(filterType.equalsIgnoreCase("and") || filterType.isEmpty()) {
            addResult = true;
            for (NodeFilter nf : nodeFilterList) {
                if ((nf.getEvalFilter().get().equals("Equals") && nf.getHitCount() == 0)
                        || (nf.getEvalFilter().get().equals("Not equal to") && nf.getHitCount() > 0)) {
                    addResult = false;
                    break;
                }
            }
        }
        else if(filterType.equalsIgnoreCase("or")) {

            for (NodeFilter nf : nodeFilterList) {
                if (
                        (nf.getEvalFilter().get().equals("Equals") && nf.getHitCount() > 0)
                        ||
                        (nf.getEvalFilter().get().equals("Not equal to") && nf.getHitCount() == 0)
                ) {
                    addResult = true;
                    break;
                }
            }
        }
        return addResult;
    }

    public void removeFilterXmlData(){

        for (Map.Entry<Integer,List<XmlNode>> xnList : xmlData.entrySet()){
            for(XmlNode xn : xnList.getValue()) {
                xn.setFilter(false);
            }
        }

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
