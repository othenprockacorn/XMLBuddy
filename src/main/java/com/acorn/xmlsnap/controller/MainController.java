package com.acorn.xmlsnap.controller;

import com.acorn.xmlsnap.model.NodeFilter;
import com.acorn.xmlsnap.tool.XMLHandler;
import com.acorn.xmlsnap.model.XmlNode;

import com.acorn.xmlsnap.tool.XMLImporter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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


        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNodeName());
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getNodeValue());
        attributesColumn.setCellValueFactory(cellData -> cellData.getValue().getAttributes());

        nameColumn.setMinWidth(150.0);
        valueColumn.setMinWidth(150.0);
        attributesColumn.setMinWidth(300.0);

        tableView.getColumns().add(nameColumn);
        tableView.getColumns().add(valueColumn);
        tableView.getColumns().add(attributesColumn);

        tfFilter.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                setFilteredViewing();
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


    private void setFilteredViewing(){

        List<NodeFilter> nodeFilterList = getNodeFilterList(tfFilter.getText());

        if (nodeFilterList == null || nodeFilterList.isEmpty() ) {
            tableView.setItems(observableList);
            return;
        }


        FilteredList<XmlNode> filteredList = new FilteredList<>(FXCollections.observableList(observableList));

        List<Predicate<XmlNode>> allPredicates = new ArrayList<>();

        for(NodeFilter x : nodeFilterList){
            allPredicates.add(obj -> {
                return searchNode(obj, x.getValueFilter(),x.getValueFilter());});

        }

      //  filteredList.setPredicate(allPredicates.stream().reduce(x->true, Predicate::and));
      //  tableView.setItems(filteredList);

    }

    private boolean searchNode(XmlNode xn, String node, String value){
        return (xn.getNodeName().toString().equalsIgnoreCase(node) &&
                xn.getNodeValue().toString().equalsIgnoreCase(value));
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

    private static List<NodeFilter> getNodeFilterList(String filter) {
        if(filter.isEmpty()){
            return null;
        }

        List<NodeFilter> nodeFilterList = new ArrayList<>();
        String filterOptions = filter.trim();
        String[] filters= filterOptions.split(",");


        for(String f :filters){
            String[] filterParts = f.split("=");
            if (filterParts.length < 2) return null;
            NodeFilter nf = new NodeFilter(filterParts[0], filterParts[1]);
            nodeFilterList.add(nf);
        }
        return nodeFilterList;
    }

}