package com.acorn.xmlbuddy.tool;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class XMLImporter {


    private final FileChooser fileChooser = new FileChooser();
    private static final String TITLE = "Select file .xml";


    public String getXmlPath(){

        String pathFile = "";
        fileChooser.setTitle(TITLE);
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("XML File", "*.xml"));

        Stage mainStage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(mainStage);

        if (selectedFile != null) {
            pathFile = selectedFile.getPath();
        }

        return pathFile;
    }


}
