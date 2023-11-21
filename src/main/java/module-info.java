module com.acorn.xmlbuddy {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    opens com.acorn.xmlbuddy to javafx.fxml;
    exports com.acorn.xmlbuddy;

    exports com.acorn.xmlbuddy.controller;
    opens com.acorn.xmlbuddy.controller to javafx.fxml;

    exports com.acorn.xmlbuddy.model;
    opens com.acorn.xmlbuddy.model to javafx.fxml;
}
