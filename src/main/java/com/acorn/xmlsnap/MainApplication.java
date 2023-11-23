package com.acorn.xmlsnap;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(GlobalSettings.MAIN_VIEW));
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setMinHeight(480);
        stage.setMinWidth(640);
        stage.setTitle(GlobalSettings.APP_TITLE);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args){
        launch();
    }
}