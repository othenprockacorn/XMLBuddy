package com.acorn.xmlsnap.controller;

import com.acorn.xmlsnap.model.NodeFilter;
import com.acorn.xmlsnap.tool.XMLHandler;
import com.acorn.xmlsnap.model.XmlNode;

import com.acorn.xmlsnap.tool.XMLImporter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.util.Callback;


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

    @FXML private TableView<XmlNode> tableView = new TableView<>();
    @FXML private final TableColumn<XmlNode, String> nameColumn = new TableColumn<>("Name");
    @FXML private final TableColumn<XmlNode, String> valueColumn = new TableColumn<>("Value");
    @FXML private final TableColumn<XmlNode, String> parentColumn = new TableColumn<>("Parent");
    @FXML private final TableColumn<XmlNode, String> attributesColumn = new TableColumn<>("Attributes");


    @FXML private TableView<NodeFilter> tableViewFilter = new TableView<>();
    @FXML private final TableColumn<NodeFilter, String> typeCol = new TableColumn<>("Filter Type");
    @FXML private final TableColumn<NodeFilter, String> nameCol = new TableColumn<>("Node");
    @FXML private final TableColumn<NodeFilter, String> evalCol = new TableColumn<>("Evaluation");
    @FXML private final TableColumn<NodeFilter, String> valueCol = new TableColumn<>("Value");

    @FXML private TextField tfMainNode;
    @FXML private Label msgLabel;
    @FXML private Label viewingLabel;

    @FXML private Button butXmlSelector;

    @FXML private TextField tfFilter;
    @FXML private ComboBox<String> cbFilterType;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xmlImporter = new XMLImporter();

        //Filter table
        tableViewFilter.setEditable(true);

        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameFilter());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        tableViewFilter.getColumns().add(nameCol);


        //Main Table
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().getNodeName());
        nameColumn.setSortable(false);
        nameColumn.setMinWidth(200.0);
        tableView.getColumns().add(nameColumn);
        valueColumn.setCellValueFactory(cellData -> cellData.getValue().getNodeValue());
        valueColumn.setSortable(false);
        valueColumn.setMinWidth(200.0);
        tableView.getColumns().add(valueColumn);
        parentColumn.setCellValueFactory(cellData -> cellData.getValue().getNodeParentName());
        parentColumn.setSortable(false);
        parentColumn.setMinWidth(200.0);
        tableView.getColumns().add(parentColumn);
        attributesColumn.setCellValueFactory(cellData -> cellData.getValue().getAttributes());
        attributesColumn.setSortable(false);
        attributesColumn.setMinWidth(300.0);
        tableView.getColumns().add(attributesColumn);
        tableView.getSelectionModel().setCellSelectionEnabled(true);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        tableView.setRowFactory(new Callback<TableView<XmlNode>, TableRow<XmlNode>>() {
            @Override
            public TableRow<XmlNode> call(TableView<XmlNode> param) {
                return new TableRow<XmlNode>() {
                    @Override
                    protected void updateItem(XmlNode item, boolean empty) {
                        super.updateItem(item, empty);

                        if(item!=null && item.isFilter()) {
                            setStyle("-fx-background-color:#22bad9;-fx-font-weight: bold");
                        }
                        else if (item!=null && item.getHasChildren()) {
                            setStyle("-fx-font-weight: bold");
                        }
                        else{
                            setStyle("");
                        }
                    }
                };
            }
        });


        //Right-Click menu
        MenuItem item = getMenuItem();
        ContextMenu menu = new ContextMenu();
        menu.getItems().add(item);
        tableView.setContextMenu(menu);

        //Filter
        tfFilter.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                applyFilter();
            }
        } );

        //Set focus on open file button
        Platform.runLater(() ->  butXmlSelector.requestFocus() );

    }

    private MenuItem getMenuItem() {
        MenuItem item = new MenuItem("Copy");
        item.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            @SuppressWarnings("rawtypes")
            public void handle(ActionEvent event) {
                ObservableList<TablePosition> posList = tableView.getSelectionModel().getSelectedCells();
                int old_r = -1;
                StringBuilder clipboardString = new StringBuilder();
                for (TablePosition p : posList) {
                    int r = p.getRow();
                    int c = p.getColumn();
                    Object cell = tableView.getColumns().get(c).getCellData(r);
                    if (cell == null)
                        cell = "";
                    if (old_r == r)
                        clipboardString.append('\t');
                    else if (old_r != -1)
                        clipboardString.append('\n');
                    clipboardString.append(cell);
                    old_r = r;
                }
                final ClipboardContent content = new ClipboardContent();
                content.putString(clipboardString.toString());
                Clipboard.getSystemClipboard().setContent(content);
            }
        });
        return item;
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

        if ( xmlHandler.getXmlFilteredIndex() > 0 || xmlHandler.getXmlIndex() > 0) {
            List<XmlNode> xmlRow = xmlHandler.getElement(currentIndex);
            String msgFiltered = xmlHandler.getXmlFilteredIndex() > 0 ?
                    " (filtered " + xmlHandler.getXmlFilteredIndex() + ") of " + xmlHandler.getXmlIndex()
                    : xmlHandler.getXmlIndex().toString();
            viewingLabel.setText("Viewing " + currentIndex + " of " + msgFiltered);
            observableList.setAll(xmlRow);
            tableView.setItems(observableList);
        }

    }


    private void applyFilter(){

        List<NodeFilter> nodeFilterList = getNodeFilterList(tfFilter.getText());

        if (tfFilter.getText().isEmpty() ) {
            xmlHandler.removeFilterXmlData();
        }else if (nodeFilterList == null
                || nodeFilterList.isEmpty()
                || xmlHandler.filterXmlData(nodeFilterList, cbFilterType.getValue()) == 0) {
            xmlHandler.removeFilterXmlData();
            Alert alert = new Alert(Alert.AlertType.NONE);
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            alert.setTitle("No match found");
            alert.setContentText("Did not find any results for " + tfFilter.getText());
            alert.getDialogPane().getButtonTypes().add(type);
            alert.showAndWait();
        }

        currentIndex = 1;
        setViewing();
    }

    @FXML
    public void moveNext(){

        if(xmlHandler!=null){

            if( xmlHandler.getXmlFilteredIndex() > 0 &&  currentIndex >= xmlHandler.getXmlFilteredIndex() ) {
                return;
            }
            else  if( xmlHandler.getXmlIndex() > 0 &&  currentIndex >= xmlHandler.getXmlIndex() ) {
                return;
            }

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
            String[] filterNodeParts = f.split("=");
            if (filterNodeParts.length < 2) return null;

            String[] filterNameAndAttribute = filterNodeParts[0].replace("!","").split("@");

            NodeFilter nf = new NodeFilter(
                    filterNameAndAttribute[0],
                    filterNameAndAttribute.length > 1 ? filterNameAndAttribute[1]: "",
                    filterNodeParts[1],
                    filterNodeParts[0].charAt(0) == '!');

            nodeFilterList.add(nf);
        }
        return nodeFilterList;
    }

}