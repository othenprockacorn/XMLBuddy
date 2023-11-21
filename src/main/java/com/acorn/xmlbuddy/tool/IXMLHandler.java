package com.acorn.xmlbuddy.tool;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.util.Map;

public interface IXMLHandler {


    public void readXMLFromFile(String fileLocation);

    public Integer getXmlIndex();
}
