package com.acorn.xmlsnap.controller;

import com.acorn.xmlsnap.tool.XMLHandler;
import com.acorn.xmlsnap.model.XmlNode;

import com.acorn.xmlsnap.tool.XMLImporter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;


import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    private static final String USER_ELEMENT = "bookstore";
    private static final String ROW_ELEMENT = "book";

    private  int currentIndex = 1;

    private XMLHandler xmlHandler;
    private XMLImporter xmlImporter;

    private final ObservableList<XmlNode> observableList = FXCollections.observableArrayList();

    @FXML
    private TableView<XmlNode> tableView;
    @FXML
    private final TableColumn<XmlNode, String> nameColumn = new TableColumn<>("Name");
    @FXML
    private final TableColumn<XmlNode, String> valueColumn = new TableColumn<>("Value");
    @FXML
    private final TableColumn<XmlNode, String> attributesColumn = new TableColumn<>("Attributes");

    @FXML
    private TextField tfMainNode;
    @FXML
    private Label msgLabel;
    @FXML
    private Label viewingLabel;

    @FXML
    private Button butXmlSelector;

    @FXML
    private Button butBack;

    @FXML
    private Button butNext;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xmlImporter = new XMLImporter();


        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getValue());
        attributesColumn.setCellValueFactory(cellData -> cellData.getValue().getAttributes());

        nameColumn.setMinWidth(150.0);
        valueColumn.setMinWidth(150.0);
        attributesColumn.setMinWidth(300.0);

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(valueColumn);
        tableView.getColumns().add(attributesColumn);



        Platform.runLater(() ->  butXmlSelector.requestFocus() );

    }

    @FXML
    public void selectXmlFile(ActionEvent event){

        observableList.clear();
        tableView.refresh();

        if(!tfMainNode.getText().isEmpty()) {

            String filepath = xmlImporter.getXmlPath();
            xmlHandler = new XMLHandler(tfMainNode.getText());

            if(!filepath.isEmpty()){
                msgLabel.setText("Reading " + filepath);
                xmlHandler.readXMLFromFile(filepath);
                setViewing();

            }

        }
        else{
            msgLabel.setText("The root node (container node) must not be empty");
        }

    }

    private void setViewing(){

        if(xmlHandler.getXmlIndex() > 0) {
            List<XmlNode> xmlRow = xmlHandler.getElement(currentIndex);
            viewingLabel.setText("Viewing  " + currentIndex + " of " + (xmlHandler.getXmlIndex()) );
            observableList.setAll(xmlRow);
            tableView.setItems(observableList);
        }

    }

    @FXML
    public void moveNext(){

        if(currentIndex < xmlHandler.getXmlIndex()) {
            currentIndex++;
            setViewing();
        }
    }
    @FXML
    public void moveBack(){

        if(currentIndex > 1) {
            currentIndex--;
            setViewing();
        }

    }
}