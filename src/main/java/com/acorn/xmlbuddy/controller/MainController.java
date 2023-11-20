package com.acorn.xmlbuddy.controller;

import com.acorn.xmlbuddy.tool.XMLHandler;

import com.acorn.xmlbuddy.GlobalSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    String link = getClass().getResource(GlobalSettings.MAIN_HTML).toExternalForm();

    String xmlPath = "xml/books.xml";

    XMLHandler xmlHandler = new XMLHandler();


    @FXML Button butRunQuery;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }


    @FXML public void runQuery(ActionEvent event) {
        xmlHandler.readXMLFromFile(xmlPath);
    }


}