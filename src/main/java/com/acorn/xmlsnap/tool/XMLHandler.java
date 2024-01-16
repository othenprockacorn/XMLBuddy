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

    private final HashSet<NodeAttribute> nodeAttributeList = new HashSet<>();
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
                        nodeAttributeList.addAll(attributeList);
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

        String nodeParent = "";
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

                        nodeParent = !nodeParentList.isEmpty()? nodeParentList.get(nodeParentList.size() - 1) : "";

                        if (nodeIgnore.contains(currentNode)){

                            if(!nodeParentList.isEmpty()) nodeParentList.remove(nodeParentList.size() - 1);

                            nodeParent = !nodeParentList.isEmpty()? nodeParentList.get(nodeParentList.size() - 1) : "";

                            xn.add(new XmlNode(
                                    currentNode,
                                    currentText,
                                    nodeParent,
                                   true,
                                    true,
                                    Collections.frequency(nodeCountList, nodeParent+currentNode),
                                    new ArrayList<>()
                                    )
                            );
                        }
                        else{
                            nodeNameList.add(currentNode);
                            xn.add(new XmlNode(
                                    currentNode,
                                    currentText,
                                    nodeParent,
                                    false,
                                    false,
                                    Collections.frequency(nodeCountList, nodeParent+currentNode),
                                    attributeList
                                    )
                            );
                            nodeAttributeList.addAll(attributeList);

                        }

                    }

                    break;
                case  XMLStreamConstants.START_ELEMENT:

                    if(!hadEnded){
                        nodeIgnore.add(currentNode);
                        xn.add(new XmlNode(
                                currentNode,
                                "",
                                nodeParent,
                                true,
                                false,
                                Collections.frequency(nodeCountList, nodeParent+currentNode),
                                attributeList));

                        nodeParentList.add(currentNode);
                    }

                    currentText ="";
                    currentNode = xmlStreamReader.getLocalName();

                    nodeCountList.add(nodeParent+currentNode);

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

        String filterType;
        removeFilterXmlData();

        for (Map.Entry<Integer,List<XmlNode>> xnList : xmlData.entrySet()){

            for (NodeFilter nf : nodeFilterList){
                filterType = nf.getTypeFilter().get().toLowerCase();
                nf.setHitCount(0);

                for(XmlNode xn : xnList.getValue()) {

                    if ( nf.getNameFilter().get() != null
                        && !nf.getNameFilter().get().isEmpty()
                        && nf.getNameFilter().get().equalsIgnoreCase(xn.getNodeName().get())
                        && (
                            (  ( nf.getValueFilter().get() == null || nf.getValueFilter().get().equals("\"\"") )  && xn.getNodeValue().get().isEmpty() )
                                ||  nf.getValueFilter().get().equalsIgnoreCase(xn.getNodeValue().get())  )
                            ) {
                        xn.setFilter(true);
                        nf.setHitCount(nf.getHitCount() + 1);
                    }
                }
            }

            if(addToResults(nodeFilterList)){
                xmlFilteredData.put(++nodeFilteredIndex, xnList.getValue());
            }

        }

        return nodeFilteredIndex;
    }

    private  boolean addToResults(List<NodeFilter> nodeFilterList) {

        String type ="";
        boolean orHit = false;

        for (int i=0; i < nodeFilterList.size(); i++) {
            type = nodeFilterList.get(i).getTypeFilter().get();

            if (nodeFilterList.get(i).getTypeFilter().get().equalsIgnoreCase("and")
                    || nodeFilterList.get(i).getTypeFilter().get().equalsIgnoreCase("and or")){
                orHit = false;
            }

            //else if this is the only one
           if(nodeFilterList.size()==1 && nodeFilterList.get(i).getHitCount()==0){
                return false;
            }
            //this one is (AND or AND OR) and the last one is OR and (this one is 0 or orHit is false) then return false
           else if( i>0
                && (type.equalsIgnoreCase("and") || type.equalsIgnoreCase("and or") )
                &&  nodeFilterList.get(i-1).getTypeFilter().get().equalsIgnoreCase("or")
                &&  (nodeFilterList.get(i).getHitCount()==0 || !orHit ) ) {
                return false;
           }
           //this one is OR or AND OR and this one is 1+, then orHit is true
           else if( (type.equalsIgnoreCase("or") || type.equalsIgnoreCase("and or") )
                && nodeFilterList.get(i).getHitCount()>0){
               orHit = true;
           }
          //this one is AND and this one is 0, then return false
           else if(type.equalsIgnoreCase("and") && nodeFilterList.get(i).getHitCount()==0){
               return false;
           }
           //this one is Blank and the next one is AND and this one is 0, then return false
           else if(nodeFilterList.size() > 1
                && i == 0
                && (nodeFilterList.get(i+1).getTypeFilter().get().equalsIgnoreCase("and")
                    || nodeFilterList.get(i+1).getTypeFilter().get().equalsIgnoreCase("and or"))
                && nodeFilterList.get(i).getHitCount()==0) {
               return false;
           }
           //else If (this one is Blank and the next one is OR and this one is 0, then orHit is true
           else if(nodeFilterList.size() > 1
                   && i == 0
                   && (nodeFilterList.get(i+1).getTypeFilter().get().equalsIgnoreCase("or")
                   || nodeFilterList.get(i+1).getTypeFilter().get().equalsIgnoreCase("and or"))
                   && (nodeFilterList.get(i).getHitCount()>0 || nodeFilterList.get(i+1).getHitCount()>0)) {
               orHit = true;
           }
            //this one is OR and EOF and orHit is false, then return false
           if (i == nodeFilterList.size()-1
                && (type.equalsIgnoreCase("or") || type.equalsIgnoreCase("and or"))
                && !orHit){
               return false;
           }

        }

        return true;
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

    public HashSet<String> getNameList(){
        return nodeNameList;
    }

    public HashSet<NodeAttribute> getAttributeList(){
        return nodeAttributeList;
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
