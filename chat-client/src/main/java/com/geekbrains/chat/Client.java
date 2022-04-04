package com.geekbrains.chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Client extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Chat");
        primaryStage.setScene(new Scene(root, 400, 600));
        primaryStage.show();
    }
}
