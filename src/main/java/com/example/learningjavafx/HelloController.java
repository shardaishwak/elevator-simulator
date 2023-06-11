package com.example.learningjavafx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    public Label e1floor;
    public Label e1direction;
    public Label e1upqueue;
    public Label e1downqueue;
    @FXML
    private VBox content;
    @FXML
    private Button button;

    public HelloController() {
    }

    @FXML
    protected void onHelloButtonClick() {
    }

    @FXML
    protected void onClick() {
        System.out.println("Done~~");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}