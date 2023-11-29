package com.acorn.xmlsnap.tool;

import com.acorn.xmlsnap.model.NodeFilter;
import com.acorn.xmlsnap.model.XmlNode;

import java.util.List;

public interface IXMLHandler {


    public void readXMLFromFile(String fileLocation);

    public Integer filterXmlData(List<NodeFilter> nodeFilterList, String filterType);

    public Integer getXmlIndex();

    public List<XmlNode> getElement(Integer index);

    public Integer getXmlFilteredIndex();

}
