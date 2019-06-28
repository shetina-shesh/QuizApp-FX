package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public TextField username;
    public TextField address;
    public TextField port;

    public Label error;
    public static String name;
    public static String serverAddress;
    public static int serverPort;

    public static Stage quizStage = null;

    @FXML
    public void initialize(){

    }

    public void login(ActionEvent actionEvent) {
        name = username.getText();
        serverAddress = address.getText();
        serverPort = Integer.parseInt(port.getText());
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/JavaQuiz.fxml"));
            Parent root = (Parent) loader.load();
            Stage stage = new Stage();
            quizStage = stage;
            stage.setScene(new Scene(root));
            //stage.setResizable(false);
            stage.show();
            App.stage.close();
            if (JavaQuizController.errorCode != null){
                stage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
