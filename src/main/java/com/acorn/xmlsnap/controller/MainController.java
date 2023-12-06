package com.acorn.xmlsnap.controller;

import com.acorn.xmlsnap.model.NodeFilter;
import com.acorn.xmlsnap.tool.XMLHandler;
import com.acorn.xmlsnap.model.XmlNode;

import com.acorn.xmlsnap.tool.XMLImporter;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
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
    @FXML private TextField tfFilter;
    @FXML private ComboBox<String> cbFilterType;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        xmlImporter = new XMLImporter();

        tableViewFilter.setEditable(true);

        //Filter table
        typeCol.setMaxWidth(80.0);
        typeCol.setMinWidth(80.0);
        typeCol.setCellValueFactory(cellData -> cellData.getValue().getTypeFilter());
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setCellFactory(ComboBoxTableCell.forTableColumn("Start","And","AndOr","Or"));
        tableViewFilter.getColumns().add(typeCol);

        nameCol.setCellValueFactory(cellData -> cellData.getValue().getNameFilter());
        nameCol.setMinWidth(200.0);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setCellFactory(ComboBoxTableCell.forTableColumn(nodeNameList));
        tableViewFilter.getColumns().add(nameCol);

        evalCol.setCellValueFactory(cellData -> cellData.getValue().getEvalFilter());
        evalCol.setCellFactory(TextFieldTableCell.forTableColumn());
        evalCol.setMaxWidth(120.0);
        evalCol.setMinWidth(120.0);
        evalCol.setCellFactory(ComboBoxTableCell.forTableColumn("Equals","Starts with","Ends with","Contains","Not equal to"));
        tableViewFilter.getColumns().add(evalCol);

        valueCol.setCellValueFactory(cellData -> cellData.getValue().getValueFilter());
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setSortable(false);
        valueCol.setMinWidth(200.0);
        tableViewFilter.getColumns().add(valueCol);

        deleteCol.setMaxWidth(80.0);
        deleteCol.setMinWidth(80.0);


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


        tableViewFilter.setFixedCellSize(30);

        tableViewFilter.setPlaceholder(new Label("No filters"));
        tableViewFilter.getStyleClass().add("noheader");
        tableViewFilter.setItems(observableFilterList);

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
        //Filter
//        tfFilter.setOnKeyPressed( event -> {
//            if( event.getCode() == KeyCode.ENTER ) {
//                applyFilter();
//            }
//        } );

        //Set focus on open file button
      //  Platform.runLater(() ->  butXmlSelector.requestFocus() );

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

    private void applyFilter(){

        List<NodeFilter> nodeFilterList = getNodeFilterList(tfFilter.getText());

        if(nodeFilterList != null) {
            observableFilterList.clear();
            observableFilterList.addAll(nodeFilterList);
            resizeFilterView();
        }


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

    public void addFilter(ActionEvent event){

        if (nodeNameList.isEmpty() || observableFilterList.size() >=5)return;

        String filterType = (!observableFilterList.isEmpty())? "And": "Start";

        observableFilterList.add(
                new NodeFilter(
                        filterType,
                        nodeNameList.get(0),
                        "",
                        "Equals",
                        ""

        ));

        resizeFilterView();

    }
    private List<NodeFilter> getNodeFilterList(String filter) {
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
                    cbFilterType.getValue(),
                    filterNameAndAttribute[0],
                    filterNameAndAttribute.length > 1 ? filterNameAndAttribute[1]: "",
                    "Equals",
                    filterNodeParts[1]);

            nodeFilterList.add(nf);
        }
        return nodeFilterList;
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
        final Button cellButton = new Button("Delete");

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
                }
            });
        }

        //Display button if the row is not empty
        @Override
        protected void updateItem(Boolean t, boolean empty) {
            super.updateItem(t, empty);
            if(!empty){
                setGraphic(cellButton);
            }
        }
    }

}