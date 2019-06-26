package controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ErrorController implements Initializable {
    public Label label;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        label.setText(JavaQuizController.errorCode);
    }
}
