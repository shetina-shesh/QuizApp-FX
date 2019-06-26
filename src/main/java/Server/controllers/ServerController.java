package Server.controllers;

import Server.Time;
import Server.Connection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import model.BestResult;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.*;

public class ServerController implements Initializable, Runnable{

    public Label fileName;
    //public TextArea console;

    public TextField startHours;
    public TextField startMins;
    public TextField endMins;
    public TextField endHours;

    @FXML
    private TableView<BestResult> tableResults;
    @FXML
    private TableColumn<BestResult, String> columnUserName;
    @FXML
    private TableColumn<BestResult, Integer> columnCountAll;
    @FXML
    private TableColumn<BestResult, Integer> columnCountCorrect;
    @FXML
    private TableColumn<BestResult, String> columnTime;

    public ScrollPane pane;

    public static Map<Time, File> fileByTimeMap = new HashMap<>();
    File file;

    Thread run, listen;
    public void clearConsole(ActionEvent actionEvent) {
        //console.clear();
    }

    public void showFileSelectionDialog(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        file = fileChooser.showOpenDialog(pane.getScene().getWindow());
        if (file != null) {
           fileName.setText(file.getName());
        }
    }

    public void initTableView(){
        columnUserName.setCellValueFactory(new PropertyValueFactory<BestResult, String>("userName"));
        columnCountCorrect.setCellValueFactory(new PropertyValueFactory<BestResult, Integer>("countCorrect"));
        columnCountAll.setCellValueFactory(new PropertyValueFactory<BestResult, Integer>("countAll"));
        columnTime.setCellValueFactory(new PropertyValueFactory<BestResult, String>("time"));
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initDebugVars();
        initTableView();
        run = new Thread(this, "Server");
        run.start();
    }

    // debug
    public void initDebugVars(){
        startHours.setText("15");
        startMins.setText("0");
        endHours.setText("23");
        endMins.setText("59");
    }

    @Override
    public void run() {
        int port =  8184;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            //console.appendText("Сервер запущен !\n");

            while (true) {
                Socket socket = serverSocket.accept(); // связались с клиентом и начали обработку запросов
                listen = new Thread("Listen"){
                    public void run() { // многопоточка, чтобы обрабатывать несколько клиентов
                        try {
                            Connection connection = new Connection(socket);
                            String message = serverHandshake(connection);
                            serverGetResultsFromClient(connection);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    }
                };
                listen.start();
            }
        } catch (Exception e) {
            System.out.println("Произошла ошибка.");
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addFileToMap(ActionEvent actionEvent) {
        fileByTimeMap.put(new Time(
                Integer.parseInt(startHours.getText()),
                Integer.parseInt(endHours.getText()),
                Integer.parseInt(startMins.getText()),
                Integer.parseInt(endMins.getText())), file);

        //console.appendText("Вы добавили новый файл! \n");
    }

    private File getFileByTime() { // получение файла по времени
        for (Time key : fileByTimeMap.keySet()){
            if (key.currentTimeInRange()){
                return fileByTimeMap.get(key);
            }
        }
        return null;
    }

    private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException { // просто вывели имя и отправили файл
        String answer;
        answer = connection.receiveString();
        if (getFileByTime() == null){
            connection.sendString("/e");
            connection.sendString("Файл не доступен. Повторите позже");
        }
        else {
            connection.sendString("/f");
            connection.sendFile(getFileByTime());
        }


        //console.appendText(answer + " connected\n");
        return answer;
    }

    private void serverGetResultsFromClient(Connection connection) throws IOException, ClassNotFoundException { //получили результаты и вывели в коонсоль Логин - сколько верных - время прохождения
        String result = connection.receiveString();
        System.out.println(result);
        String[] string = result.split("-");
        tableResults.getItems().add(new BestResult(string[0], Integer.parseInt(string[1]), Integer.parseInt(string[2]), string[3]));
        //console.appendText(string[0]+ " " + string[1] + " " + string[2] + "\n");
    }
}
