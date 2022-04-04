package com.geekbrains.chat;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    public TextField msgField;
    @FXML
    public TextArea mainArea;

    private Network network;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        network = new Network((args) -> mainArea.appendText((String)args[0]));
    }

    public void sendMessage(ActionEvent actionEvent) {
        network.sendMessage(msgField.getText()); //отправляем сообщение
        msgField.clear(); //очищаем поле
        msgField.requestFocus();//переводим фокус, чтобы пользователь дальше мог писать
    }

    public void exitAct(ActionEvent actionEvent) {
        network.close();
        Platform.exit();
    }
}
