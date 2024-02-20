package com.example.ksiegarnia;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;



public class StartController extends UserData {

    private Stage stage;
    private Scene scene;
    private Parent root;

    DBUtils dbDane = new DBUtils();

    @FXML
    PasswordField pf_password;
    @FXML
    TextField tf_username;

    public void switchToSignUp(ActionEvent event) throws Exception {
        root = FXMLLoader.load(getClass().getResource("sign-up.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene= new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void logInUser(ActionEvent event) {
        super.username = tf_username.getText();
        super.password = pf_password.getText();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(),dbDane.getDbUserName(),dbDane.getDbPassword());
            preparedStatement = connection.prepareStatement("SELECT password, gender FROM users WHERE username = ?");
            preparedStatement.setString(1,super.username);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.isBeforeFirst()){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Nie znaleziono uzytkownika. Wprowadz poprawny USERNAME.");
                alert.show();
            } else {
                while(resultSet.next()){
                    String retrievedPassword = resultSet.getString("password");
                    String retrievedGender = resultSet.getString("gender");
                    if(retrievedPassword.equals(super.password)){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-in.fxml"));
                        root = loader.load();
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        scene = new Scene(root);
                        stage.setScene(scene);
                        stage.show();

                        LoggedInController powitanie = loader.getController();
                        powitanie.setGreeting(super.username);
                    }else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setContentText("Bledne haslo. Wprowadz poprawne haslo.");
                        alert.show();
                    }
                }
            }
        } catch (SQLException e) {
            Alert alertOgolny = new Alert(Alert.AlertType.ERROR);
            alertOgolny.setContentText("BLAD POLACZENIA Z SERWEREM");
            alertOgolny.show();
            //e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }}

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
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
