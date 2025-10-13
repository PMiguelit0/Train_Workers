package com.example.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Carrega o arquivo FXML da pasta resources
        FXMLLoader loader = new FXMLLoader(getClass().getResource("InitialScene.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Train and Workers");
        primaryStage.setScene(new Scene(root, 1024, 585));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}