package com.example.ksiegarnia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KoszykController extends UserData {

@FXML
Label book1;
@FXML
Label book2;
@FXML
Label book3;
@FXML
Label book4;
@FXML
Label book5;
@FXML
Label book6;
@FXML
Label label_cenaSumaryczna;
@FXML
Button button_kup;


DBUtils dbDane = new DBUtils();

Stage stage;
Scene scene;
Parent root;



public int cenaSumaryczna;

String nazwaPliku = "Podsumowanie.txt";
String daneDoPliku;

public void cofnijDoLoggedIn(ActionEvent event) throws IOException {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-in.fxml"));
    root = loader.load();
    stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
    scene = new Scene(root);
    stage.setScene(scene);
    stage.show();

    LoggedInController loggedInController = loader.getController();
    loggedInController.setGreeting(super.username);
}

    public void pokazKoszyk(ActionEvent event) throws SQLException {

           Connection connection = null;
           PreparedStatement sprawdzKoszyk = null;
           ResultSet resultSet = null;
           try {
           connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(), dbDane.getDbUserName(), dbDane.getDbPassword());
           String sprawdzKoszykQuote =
                   "SELECT book, COUNT(*) AS book_count\n" +
                           "FROM koszyk\n" +
                           "WHERE username = ?\n" +
                           "GROUP BY book;";
           sprawdzKoszyk = connection.prepareStatement(sprawdzKoszykQuote);
           sprawdzKoszyk.setString(1, super.username);
           resultSet = sprawdzKoszyk.executeQuery();


           Label[] TablicaLabeli = {book1, book2, book3, book4, book5, book6};

           int n = 0;

           while (resultSet.next() && n < TablicaLabeli.length) {
               String book = resultSet.getString("book");
               int bookCount = resultSet.getInt("book_count");
               cenaSumaryczna += bookCount * 30;
               String labelText = book + ": " + bookCount + " szt.";
               TablicaLabeli[n].setText(labelText);
               n++;
           }

           label_cenaSumaryczna.setText("Twoja cena to jedynie " + cenaSumaryczna + " zl!");
           cenaSumaryczna = 0;
       }catch(SQLException e){
           Alert alertOgolny = new Alert(Alert.AlertType.ERROR);
           alertOgolny.setContentText("BLAD POLACZENIA Z SERWEREM");
           alertOgolny.show();
           e.printStackTrace();
       }finally {
           if (resultSet != null) {
               try {
                   resultSet.close();
               } catch (SQLException e) {
                   e.printStackTrace();
               }}

           if (sprawdzKoszyk != null) {
               try {
                   sprawdzKoszyk.close();
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

    public void drukujPodsumowanie(){
        try {
            FileWriter fileWriter = new FileWriter(nazwaPliku);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            Connection connection = null;
            PreparedStatement pobierzZakupione = null;
            ResultSet resultSet = null;

            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(), dbDane.getDbUserName(), dbDane.getDbPassword());

                String pobierzZakupioneQuery = "SELECT * FROM zakupione WHERE username = ?";
                pobierzZakupione = connection.prepareStatement(pobierzZakupioneQuery);
                pobierzZakupione.setString(1, super.username);
                resultSet = pobierzZakupione.executeQuery();

                bufferedWriter.write("PODSUMOWANIE");
                bufferedWriter.newLine();
                bufferedWriter.newLine();

                while (resultSet.next()) {
                    String book = resultSet.getString("book");
                    int quantity = resultSet.getInt("quantity");

                    String line = "\"" + book + "\" - " + quantity + " szt.";
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Zapisano do pliku.");
                alert.show();

            } catch (SQLException e) {
                Alert alertOgolny = new Alert(Alert.AlertType.ERROR);
                alertOgolny.setContentText("BLAD POLACZENIA Z SERWEREM");
                alertOgolny.show();
                throw new RuntimeException(e);
            } finally {
                // Zamykanie ResultSet, PreparedStatement i Connection
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                    if (pobierzZakupione != null) {
                        pobierzZakupione.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            bufferedWriter.close();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Nie udało się zapisać danych do pliku.");
            alert.show();
            throw new RuntimeException(e);
        }

    }

public void kup(ActionEvent event) throws SQLException {
    Connection connection = null;
    PreparedStatement dodajDoZakupionych = null;
    ResultSet ksiazkiUzytkownika = null;
    PreparedStatement sprawdzKoszyk = null;
    ResultSet resultSet = null;
    PreparedStatement usunZKoszyka = null;
    try {
        connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(), dbDane.getDbUserName(), dbDane.getDbPassword());

        String dodajDoZakupionychQuote = "INSERT INTO zakupione (username, book, quantity) " +
                "SELECT username, book, COUNT(*) AS quantity " +
                "FROM koszyk " +
                "WHERE username = ? " +
                "GROUP BY username, book " +
                "ON DUPLICATE KEY UPDATE quantity = quantity + VALUES(quantity)";

        dodajDoZakupionych = connection.prepareStatement(dodajDoZakupionychQuote);
        dodajDoZakupionych.setString(1, super.username);
        dodajDoZakupionych.executeUpdate();

        // Usunięcie rekordów z koszyka
        String usunZKoszykaQuote = "DELETE FROM koszyk WHERE username = ?";
        usunZKoszyka = connection.prepareStatement(usunZKoszykaQuote);
        usunZKoszyka.setString(1, super.username);
        usunZKoszyka.executeUpdate();

        String sprawdzKoszykQuote =
                "SELECT book, COUNT(*) AS book_count\n" +
                        "FROM koszyk\n" +
                        "WHERE username = ?\n" +
                        "GROUP BY book;";
        sprawdzKoszyk = connection.prepareStatement(sprawdzKoszykQuote);
        sprawdzKoszyk.setString(1, super.username);
        resultSet = sprawdzKoszyk.executeQuery();

        Label[] TablicaLabeli = {book1, book2, book3, book4, book5, book6};

        int n = 0;
        cenaSumaryczna = 0;

        while (resultSet.next() && n < TablicaLabeli.length) {
            String book = resultSet.getString("book");
            int bookCount = resultSet.getInt("book_count");
            cenaSumaryczna += bookCount * 30;
            String labelText = book + ": " + bookCount + " szt.";
            TablicaLabeli[n].setText(labelText);
            n++;
        }

        // Ustawienie pustego tekstu na pozostałych labelach
        while (n < TablicaLabeli.length) {
            TablicaLabeli[n].setText("");
            n++;
        }

        // Aktualizacja labela cenaSumaryczna
        label_cenaSumaryczna.setText("Cena Sumaryczna: " + cenaSumaryczna);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Zakupiono.");
        alert.show();
    } catch (SQLException e) {
        e.printStackTrace();
        Alert alertOgolny = new Alert(Alert.AlertType.ERROR);
        alertOgolny.setContentText("BLAD POLACZENIA Z SERWEREM");
        alertOgolny.show();
    } finally {
        if (dodajDoZakupionych != null) {
            try {
                dodajDoZakupionych.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }}

        if (ksiazkiUzytkownika != null) {
            try {
                ksiazkiUzytkownika.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (sprawdzKoszyk != null) {
            try {
                sprawdzKoszyk.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (usunZKoszyka != null) {
            try {
                usunZKoszyka.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


}}
