package com.example.ksiegarnia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class SignUpController extends UserData{

    @FXML
    TextField tf_username;
    @FXML
    TextField tf_password;
    @FXML
    RadioButton rb_kobieta;
    @FXML
    RadioButton rb_mezczyzna;

    @FXML
    Label label_welcome;

    private Stage stage;
    private Scene scene;
    private Parent root;

    DBUtils dbDane = new DBUtils();


    public void switchToStart(ActionEvent event) throws Exception {
        root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    public void signUpUser(ActionEvent event) {
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;
        super.username =tf_username.getText();
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(),dbDane.getDbUserName(),dbDane.getDbPassword());
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, super.username);
            resultSet = psCheckUserExists.executeQuery();

            //sprawdza czy w bazie istnieje juz taka nazwa uzytkownika
            if (resultSet.isBeforeFirst()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ta nazwa uzytkownika juz istnieje.");
                alert.show();
            } else {

                //zapisuje do bazy dane wpisane w pola tekstowe
                super.password =tf_password.getText();

                //zapisuje do bazy dane plec
                if(rb_kobieta.isSelected()) {
                    super.gender = rb_kobieta.getText();
                }
                else if(rb_mezczyzna.isSelected()) {
                    super.gender = rb_mezczyzna.getText();
                }

                //informuje o blednym uzupelnieniu danych
                if(super.username.isEmpty() || super.password.isEmpty() || super.gender==null)
                {
                    Alert alertOgolny = new Alert(Alert.AlertType.ERROR);
                    alertOgolny.setContentText("Nie uzupelniono wszystkich danych.");
                    alertOgolny.show();
                } else {
                    psInsert = connection.prepareStatement("INSERT INTO users (username, password, gender) VALUES (?, ?, ?)");
                    psInsert.setString(1, super.username);
                    psInsert.setString(2, super.password);
                    psInsert.setString(3, super.gender);
                    psInsert.executeUpdate();


                    FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-in.fxml"));
                    root = loader.load();
                    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    scene = new Scene(root);
                    stage.setScene(scene);
                    stage.show();

                    LoggedInController powitanie = loader.getController();
                    powitanie.setGreeting(super.username);
                }

            }

        } catch (SQLException e) {
            Alert alertOgolny = new Alert(Alert.AlertType.ERROR);
            alertOgolny.setContentText("BLAD POLACZENIA Z SERWEREM");
            alertOgolny.show();
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }}

            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}