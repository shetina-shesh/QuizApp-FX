package controllers;

import Server.Connection;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.Question;
import model.QuestionLoader;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class JavaQuizController implements Initializable {

    LoginController loginController;

    String username;
    String address;
    int port;

    Socket socket;
    Connection connection;

    public static String errorCode;

    private double xOffset = 0;
    private double yOffset = 0;

    private long start;

    @FXML
    private ScrollPane parent;

    @FXML
    private GridPane testPane, finishedPane, resultPane;


    @FXML
    private ProgressIndicator progress;

    @FXML
    private TextArea questionLbl;

    @FXML
    private RadioButton response1, response2, response3, response4;
    // This list contain the question
    private List<Question> questions;
    // Save instance (Number Question => Response)
    private Map<Integer, Integer> instanceQuestion;
    // Max number of question
    private static int MAX_NUMBER_QUESTION;
    // Index currant question
    private static int currentQuestionIndex = 0;

    @FXML
    private Label finalScore, finalPercentageScore;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        makeStageDraggable();
        username = LoginController.name;
        address = LoginController.serverAddress;
        port = LoginController.serverPort;
        testPane.setVisible(true);
        loadQuestions();
        start = System.currentTimeMillis();
    }

    // main part
    private void makeStageDraggable() {
        parent.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        parent.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                App.stage.setX(event.getScreenX() - xOffset);
                App.stage.setY(event.getScreenY() - yOffset);
                App.stage.setOpacity(0.7f);
            }
        });
        parent.setOnDragDone((e) -> {
            App.stage.setOpacity(1.0f);
        });
        parent.setOnMouseReleased((e) -> {
            App.stage.setOpacity(1.0f);
        });

    }

    private void loadQuestions() {
        try {
            socket = new Socket(address, port);
            connection = new Connection(socket);
            connection.sendString(username);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String answer = connection.receiveString();
            if (answer.equals("/e")){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                alert.setTitle("Ошибка");
                alert.setHeaderText(null);
                alert.setContentText("Время доступа к файлу истекло");

                alert.showAndWait();
            }
            else {
                questions = new QuestionLoader().getQuestion(connection.receiveFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        MAX_NUMBER_QUESTION = questions.size();
        instanceQuestion = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            instanceQuestion.put(i, 0);
        }
        showQuestion(0);
    }

    @FXML
    private void btnPrevious() {
        saveInstance();
        if (currentQuestionIndex == 0) {
            return;
        }
        currentQuestionIndex--;
        showQuestion(currentQuestionIndex);
    }

    @FXML
    private void btnNext() {
        saveInstance();
        if (currentQuestionIndex == MAX_NUMBER_QUESTION - 1) {
            testPane.setVisible(false);
            finishedPane.setVisible(true);
            return;
        }
        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);

    }

    private void showQuestion(int index) {
        progress.setProgress(currentQuestionIndex / (float) MAX_NUMBER_QUESTION);

        questionLbl.setText(questions.get(index).getQuestion());
        response1.setText(questions.get(index).getAnswers()[0]);
        response2.setText(questions.get(index).getAnswers()[1]);
        response3.setText(questions.get(index).getAnswers()[2]);
        response4.setText(questions.get(index).getAnswers()[3]);

        switch (instanceQuestion.get(index)) {
            case 0: {
                response1.setSelected(false);
                response2.setSelected(false);
                response3.setSelected(false);
                response4.setSelected(false);
            }
            break;
            case 1: {
                response1.setSelected(true);
                response2.setSelected(false);
                response3.setSelected(false);
                response4.setSelected(false);
            }
            break;
            case 2: {
                response1.setSelected(false);
                response2.setSelected(true);
                response3.setSelected(false);
                response4.setSelected(false);
            }
            break;
            case 3: {
                response1.setSelected(false);
                response2.setSelected(false);
                response3.setSelected(true);
                response4.setSelected(false);
            }
            break;
            case 4: {
                response1.setSelected(false);
                response2.setSelected(false);
                response3.setSelected(false);
                response4.setSelected(true);
            }
            break;
        }

    }

    private void saveInstance() {
        if (response1.isSelected()) {
            instanceQuestion.replace(currentQuestionIndex, 1);
        } else if (response2.isSelected()) {
            instanceQuestion.replace(currentQuestionIndex, 2);
        } else if (response3.isSelected()) {
            instanceQuestion.replace(currentQuestionIndex, 3);
        } else if (response4.isSelected()) {
            instanceQuestion.replace(currentQuestionIndex, 4);
        }
    }

    @FXML
    private void radioClicked(ActionEvent e) {
        response1.setSelected(false);
        response2.setSelected(false);
        response3.setSelected(false);
        response4.setSelected(false);
        ((RadioButton) e.getSource()).setSelected(true);
    }

    @FXML
    void btnBackTest() {
        finishedPane.setVisible(false);
        testPane.setVisible(true);
    }

    @FXML
    private void btnViewResult() {
        int score = 0;
        for (int i = 0; i < MAX_NUMBER_QUESTION; i++) {
            int v = instanceQuestion.get(i);
            if (v == 0) {
                continue;
            }
            if (questions.get(i).getAnswers()[v - 1].equals(questions.get(i).getSolution())) {
                score++;
            }
        }

        // Load result to resultPane
        finalScore.setText(score + "/" + MAX_NUMBER_QUESTION);
        if (score >= (MAX_NUMBER_QUESTION / 2.0)) {
            finalScore.setStyle(finalScore.getStyle() + "; -fx-text-fill: #0B0");
        } else {
            finalScore.setStyle(finalScore.getStyle() + "; -fx-text-fill: #E00");
        }

        finalPercentageScore.setText(String.valueOf(score * 100.0 / MAX_NUMBER_QUESTION).concat("  ").substring(0, 5) + "%");

        try {
            long time = (System.currentTimeMillis() - start) / 1000;
            long min = time / 60;
            long sec = time % 60;
            connection.sendString(username + "-" + score + "-" + MAX_NUMBER_QUESTION + "-" + min + " min " + sec +" sec"); // собираем нужную инфу в один стринг и отправляем на сервер!
        } catch (IOException e) {
            e.printStackTrace();
        }

        finishedPane.setVisible(false);
        resultPane.setVisible(true);
    }
}
