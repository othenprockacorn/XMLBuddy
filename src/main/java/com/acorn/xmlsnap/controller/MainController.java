package com.acorn.xmlsnap.controller;

import com.acorn.xmlsnap.model.NodeFilter;
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
import javafx.scene.input.KeyCode;


import java.net.URL;
import java.util.ArrayList;
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
    private TextField tfFilter;

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

        tfFilter.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                setFilteredViewing("AND(book=2,book=4)");
            }
        } );

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
                msgLabel.setText("Reading " + xmlImporter.getXmlFileName());
                xmlHandler.readXMLFromFile(filepath);
                setViewing();

            }

        }
        else{

            Alert alert = new Alert(Alert.AlertType.NONE);
            alert.setTitle("Missing Root Node");
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            alert.setContentText("Please enter the name of the root node. The root node is the single parent of all the child nodes to iterate through");
            alert.getDialogPane().getButtonTypes().add(type);
            alert.showAndWait();
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


    private void setFilteredViewing(String filter){

        List<NodeFilter> nodeFilterList = new ArrayList<>();
        String[] filterList= filter.replace("\"","").split("\\(");
        String filterType = filterList[0];
        String filterOptions = filterList[1].replaceAll("[()]", "");
        String[] filters= filterOptions.split(",");

        for(String f :filters){
            String[] filterParts = f.split("=");
            NodeFilter nf = new NodeFilter(filterParts[0],filterParts[1]);
            nodeFilterList.add(nf);
        }



    }

    @FXML
    public void moveNext(){

        if(xmlHandler!=null && currentIndex < xmlHandler.getXmlIndex()) {
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