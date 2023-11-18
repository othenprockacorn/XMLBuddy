package com.acorn.xmlbuddy.controller;

import com.acorn.xmlbuddy.GlobalSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.web.*;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    String link = getClass().getResource(GlobalSettings.MAIN_HTML).toExternalForm();
    //String link = this.getClass().getResource(GlobalSettings.MAIN_HTML).getPath();
    //String link = "https://www.google.com";

    @FXML WebView webView = new WebView(); //access WebView in FXML document


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebEngine engine = webView.getEngine();
        engine.load(link);
    }


}