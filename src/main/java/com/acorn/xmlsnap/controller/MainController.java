package com.acorn.xmlsnap.controller;

import com.acorn.xmlsnap.model.NodeFilter;
import com.acorn.xmlsnap.tool.XMLHandler;
import com.acorn.xmlsnap.model.XmlNode;

import com.acorn.xmlsnap.tool.XMLImporter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
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
    private final ObservableList<NodeFilter> observableFilterList = FXCollections.observableArrayList();
    private final ObservableList<String> nodeNameList = FXCollections.observableArrayList();

    @FXML private TableView< NodeFilter> tableViewFilter = new TableView<>();
    @FXML private final TableColumn<NodeFilter, String> typeCol = new TableColumn<>("Filter Type");
    @FXML private final TableColumn<NodeFilter, String> nameCol = new TableColumn<>("Node");
    @FXML private final TableColumn<NodeFilter, String> evalCol = new TableColumn<>("Evaluation");
    @FXML private final TableColumn<NodeFilter, String> valueCol = new TableColumn<>("Value");
    @FXML private final TableColumn<NodeFilter, Boolean> deleteCol = new TableColumn<NodeFilter, Boolean>("Delete");

    @FXML private TextField tfMainNode;
    @FXML private Label msgLabel;
    @FXML private Label viewingLabel;
    @FXML private GridPane mainGridPane;

    @FXML private Button butXmlSelector;
    @FXML private Button applyFilter;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xmlImporter = new XMLImporter();

        tableViewFilter.setEditable(true);
        tableViewFilter.setFixedCellSize(30);

        tableViewFilter.setPlaceholder(new Label("No filters"));
        tableViewFilter.getStyleClass().add("filter-table-view");
        tableViewFilter.setItems(observableFilterList);


        //Filter table
        typeCol.setMaxWidth(100.0);
        typeCol.setMinWidth(100.0);
        typeCol.setCellValueFactory(cellData -> cellData.getValue().getTypeFilter());
        typeCol.setCellFactory(tc -> {
            ComboBox<String> combo = new ComboBox<>();
            combo.getItems().addAll("And","And Or","Or");
            TableCell<NodeFilter, String> cell = new TableCell<NodeFilter, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                  if (empty || item.isEmpty()) {
                        setGraphic(null);
                    } else {
                        combo.setValue(item);
                        setGraphic(combo);
                    }
                }
            };
            combo.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!cell.isEmpty() && newValue != null) {
                    NodeFilter item = tableViewFilter.getItems().get(cell.getIndex()) ;
                    item.setTypeFilter(newValue);
                }
            });
            cell.itemProperty().addListener((obs, oldItem, newItem) -> combo.setValue(newItem));

            return cell ;
        });

        typeCol.setOnEditCommit(e -> {
            e.getRowValue().setTypeFilter(e.getNewValue());
            tableViewFilter.requestFocus();
        });
        tableViewFilter.getColumns().add(typeCol);

        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameFilter());
        nameCol.setMinWidth(200.0);
        nameCol.setCellFactory(tc -> {
            ComboBox<String> combo = new ComboBox<>();
            combo.setMaxWidth(Double.MAX_VALUE);
            combo.getItems().addAll(nodeNameList);
            TableCell<NodeFilter, String> cell = new TableCell<NodeFilter, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        combo.setValue(item);
                        combo.setTooltip(new Tooltip("Select a node"));
                        combo.setPromptText("Select a node");
                        setGraphic(combo);
                    }
                }
            };
            combo.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!cell.isEmpty() && newValue != null) {
                    NodeFilter item = tableViewFilter.getItems().get(cell.getIndex()) ;
                    item.setNameFilter(newValue);
                }
            });
            cell.itemProperty().addListener((obs, oldItem, newItem) -> combo.setValue(newItem));

            return cell ;
        });


        tableViewFilter.getColumns().add(nameCol);

        evalCol.setCellValueFactory(cellData -> cellData.getValue().getEvalFilter());
        evalCol.setMaxWidth(120.0);
        evalCol.setMinWidth(120.0);
        evalCol.setCellFactory(tc -> {
            ComboBox<String> combo = new ComboBox<>();
            combo.setMaxWidth(Double.MAX_VALUE);
            combo.getItems().addAll("Equals","Starts with","Ends with","Contains","Not equal to");
            TableCell<NodeFilter, String> cell = new TableCell<NodeFilter, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item.isEmpty()) {
                        setGraphic(null);
                    } else {
                        combo.setValue(item);
                        setGraphic(combo);
                    }
                }
            };
            combo.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (!cell.isEmpty() && newValue != null) {
                    NodeFilter item = tableViewFilter.getItems().get(cell.getIndex()) ;
                    item.setEvalFilter(newValue);
                }
            });
            cell.itemProperty().addListener((obs, oldItem, newItem) -> combo.setValue(newItem));

            return cell ;
        });


        tableViewFilter.getColumns().add(evalCol);

        valueCol.setCellValueFactory(cellData -> cellData.getValue().getValueFilter());
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setCellFactory(new Callback<TableColumn<NodeFilter, String>, TableCell<NodeFilter, String>>() {

            @Override
            public TableCell<NodeFilter, String> call(TableColumn<NodeFilter, String> param) {

                final TextField textField = new TextField();

                TableCell<NodeFilter, String> cell = new TableCell<NodeFilter, String>() {

                    @Override
                    protected void updateItem(String value, boolean empty){
                        super.updateItem(value, empty);
                        if (empty){
                            setGraphic(null);
                        } else {
                            setGraphic(textField);
                            textField.setTooltip(new Tooltip("Enter a value"));
                            textField.setPromptText("Enter a value");
                            textField.setText(value);
                        }
                    }
                };

//                textField.onKeyPressedProperty().set(e -> {
//                    if (e.getCode() == KeyCode.ENTER) {
//
//                        NodeFilter item = tableViewFilter.getItems().get(cell.getIndex()) ;
//                        item.setValueFilter(textField.getText());
//
//                    }
//                });

                textField.focusedProperty().addListener((arg0, oldPropertyValue, newPropertyValue) -> {
                    if (!newPropertyValue) {
                        NodeFilter item = tableViewFilter.getItems().get(cell.getIndex()) ;
                        item.setValueFilter(textField.getText());
                    }
                });

               cell.itemProperty().addListener((obs, oldItem, newItem) -> textField.setText(newItem));

                return cell;
            }
        });

        valueCol.setSortable(false);
        valueCol.setMinWidth(200.0);


        tableViewFilter.getColumns().add(valueCol);

        deleteCol.setMaxWidth(50.0);
        deleteCol.setMinWidth(50.0);

        deleteCol.setCellValueFactory(
            new Callback<TableColumn.CellDataFeatures<NodeFilter, Boolean>,
                    ObservableValue<Boolean>>() {

                @Override
                public ObservableValue<Boolean> call(TableColumn.CellDataFeatures<NodeFilter, Boolean> p) {
                    return new SimpleBooleanProperty(p.getValue() != null);
                }
            }
        );

        //Adding the Button to the cell
        deleteCol.setCellFactory(
            new Callback<TableColumn<NodeFilter, Boolean>, TableCell<NodeFilter, Boolean>>() {

                @Override
                public TableCell<NodeFilter, Boolean> call(TableColumn<NodeFilter, Boolean> p) {

                    return new ButtonCell();
                }

            }
        );

        tableViewFilter.getColumns().add(deleteCol);


        //Main Table
        tableView.autosize();
        tableView.prefHeightProperty().bind(mainGridPane.heightProperty().subtract(tableViewFilter.heightProperty()).subtract(50.0));
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
        resizeFilterView();


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

            nodeNameList.setAll(xmlHandler.getNoUsIdeNameList());

            List<XmlNode> xmlRow = xmlHandler.getElement(currentIndex);
            String msgFiltered = xmlHandler.getXmlFilteredIndex() > 0 ?
                    " (filtered " + xmlHandler.getXmlFilteredIndex() + ") of " + xmlHandler.getXmlIndex()
                    : xmlHandler.getXmlIndex().toString();
            viewingLabel.setText("Viewing " + currentIndex + " of " + msgFiltered);
            observableList.setAll(xmlRow);
            tableView.setItems(observableList);
        }

    }

    public void applyFilter(ActionEvent event){

        List<NodeFilter> nodeFilterList = new ArrayList<>(observableFilterList);

        if (nodeFilterList.isEmpty()
                || xmlHandler.filterXmlData(nodeFilterList) == 0) {
            xmlHandler.removeFilterXmlData();
            Alert alert = new Alert(Alert.AlertType.NONE);
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            alert.setTitle("No match found");
            alert.setContentText("Did not find any results");
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

    public void addFilter(ActionEvent event){

        if (nodeNameList.isEmpty() || observableFilterList.size() >=5)return;

        String filterType = (!observableFilterList.isEmpty())? "And": "";

        observableFilterList.add(
                new NodeFilter(
                        filterType,
                        null,
                        "",
                        "Equals",
                        ""

        ));

        resizeFilterView();

    }


    private void resizeFilterView(){
        tableViewFilter.refresh();
        tableView.refresh();
        tableViewFilter.prefHeightProperty().bind(tableViewFilter.fixedCellSizeProperty().multiply(Bindings.size(tableViewFilter.getItems()).add(1.05)));
        tableViewFilter.minHeightProperty().bind(tableViewFilter.prefHeightProperty());
        tableViewFilter.maxHeightProperty().bind(tableViewFilter.prefHeightProperty());
    }

    //Define the button cell
    private class ButtonCell extends TableCell<NodeFilter, Boolean> {
        final Button cellButton = new Button("❌");

        ButtonCell(){
            //Action when the button is pressed
            cellButton.setOnAction(new EventHandler<ActionEvent>(){

                @Override
                public void handle(ActionEvent t) {
                    // get Selected Item
                    NodeFilter currentNode = (NodeFilter) ButtonCell.this.getTableView().getItems().get(ButtonCell.this.getIndex());
                    //remove selected item from the table list
                    observableFilterList.remove(currentNode);
                    resizeFilterView();
                    tableViewFilter.requestFocus();
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                cellButton.setTooltip(new Tooltip("Remove filter"));
                setGraphic(cellButton);
            }
        }
    }

}