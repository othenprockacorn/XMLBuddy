module com.acorn.xmlbuddy {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.acorn.xmlsnap to javafx.fxml;
    exports com.acorn.xmlsnap;

    exports com.acorn.xmlsnap.controller;
    opens com.acorn.xmlsnap.controller to javafx.fxml;

    exports com.acorn.xmlsnap.model;
    opens com.acorn.xmlsnap.model to javafx.fxml;
}
