package com.acorn.xmlbuddy.controller;

import com.acorn.xmlbuddy.tool.XMLHandler;
import com.acorn.xmlbuddy.model.XmlNode;

import com.acorn.xmlbuddy.tool.XMLImporter;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;




import java.net.URL;
import java.util.*;

public class MainController implements Initializable {


    private static final String USER_ELEMENT = "bookstore";
    private static final String ROW_ELEMENT = "book";

    private XMLHandler xmlHandler;
    private XMLImporter xmlImporter;

    private final ObservableList<XmlNode> observableList = FXCollections.observableArrayList();

    @FXML
    private TableView<XmlNode> tableView;
    @FXML private final TableColumn<XmlNode, String> nameColumn = new TableColumn<>("Name");
    @FXML private final TableColumn<XmlNode, String> valueColumn = new TableColumn<>("Value");

    @FXML
    private Label msgLabel;

    @FXML
    private Button butXmlSelector;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xmlHandler = new XMLHandler(USER_ELEMENT,ROW_ELEMENT);
        xmlImporter = new XMLImporter();


        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getName());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getValue());

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(valueColumn);


    }

    @FXML
    public void runQuery(ActionEvent event) {

       System.out.println(xmlHandler.getXmlIndex());
    }

    @FXML
    public void selectXmlFile(ActionEvent event){

        String filepath = xmlImporter.getXmlPath();

        if(!filepath.isEmpty()){
            msgLabel.setText("Reading " + filepath);
            xmlHandler.readXMLFromFile(filepath);


            if(xmlHandler.getXmlIndex() > 0) {


                List<XmlNode> xmlRow = xmlHandler.getElement(1);

                observableList.setAll(xmlRow);
                tableView.setItems(observableList);
            }

        }

    }

}