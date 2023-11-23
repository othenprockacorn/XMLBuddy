package com.acorn.xmlsnap.tool;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Arrays;

public class XMLImporter {


    private final FileChooser fileChooser = new FileChooser();
    private static final String TITLE = "Select file .xml";

    private String filePath = "";

    public String getXmlPath(){

        fileChooser.setTitle(TITLE);
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("XML File", "*.xml"));

        Stage mainStage = new Stage();
        File selectedFile = fileChooser.showOpenDialog(mainStage);

        if (selectedFile != null) {
            filePath = selectedFile.getPath();
        }

        return filePath;
    }


    public String getXmlFileName(){

        return filePath.substring(filePath.lastIndexOf('\\') + 1);

    }


}
