package com.example.ksiegarnia;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class BookController extends UserData implements Initializable {

    Stage stage;
    Scene scene;
    Parent root;

    @FXML
    Label label_title;
    @FXML
    Spinner <Integer> spinner_quantity;

    @FXML
    Label label_ileKsiazek;
    DBUtils dbDane = new DBUtils();

    public void ustawKsiazke(String book)
    {
        super.book = label_title.getText();
    }

    //funkcja ustawiajaca liczbe danej ksiazki w polu na ilosc ksiazek
    public void ustawNaLabelulIloscKsiazek(int ile)
    {
        label_ileKsiazek.setText("Na stanie: " + ile);
    }



    //metoda cofajaca do glownego menu
    public void backToStartController(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-in.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        LoggedInController ustawLabel = loader.getController();
        ustawLabel.setGreeting(super.username);
    }
    public void dodajDoKoszyka(ActionEvent event) throws SQLException {
        //super.book = label_title.getText();

        //Deklaracja obiektow connection oraz obiektow potrzebnych do usuwania/dodawania do bazy danych
        Connection connection = null;
        PreparedStatement dodajDoKoszyka = null;
        PreparedStatement zmniejszIloscKsiazek = null;
        ResultSet resultSet = null;
        PreparedStatement sprawdzIloscKsiazek = null;
        ResultSet ile = null;
        try {
            //Warunek sprawdzajacy czy liczba ksiazek w bazie jest > 0


            //zwraca ilosc (quantity) danej ksiazki z bazy - zmniejsza sie o n razy gdy dodamy ja n razy do koszyka
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(), dbDane.getDbUserName(), dbDane.getDbPassword());
            String queryIleJestKsiazek = "SELECT quantity FROM books WHERE book = ?";
            sprawdzIloscKsiazek = connection.prepareStatement(queryIleJestKsiazek);
            sprawdzIloscKsiazek.setString(1, super.book);
            ile = sprawdzIloscKsiazek.executeQuery();
            ile.next();

            //Tutaj nie dzialalo, teraz dziala; Na podstawie ilosci ksiazek w bazie oraz wartosci spinnera ustala czy mozna dodac do koszyka
            if (ile.getInt("quantity") > 0 && getWartosc_spinnera() <= (ile.getInt("quantity"))) {

                //Statement ktory dodaje do koszyka produkt
                connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(), dbDane.getDbUserName(), dbDane.getDbPassword());
                String queryDodajDoKoszyka = "INSERT INTO koszyk (username, book) VALUES (?, ?)";
                dodajDoKoszyka = connection.prepareStatement(queryDodajDoKoszyka);
                dodajDoKoszyka.setString(1, super.username);
                dodajDoKoszyka.setString(2, super.book);

                //Statement ktory aktualizuje ilosc ksiazek w bazie
                String queryUsun = "UPDATE books SET quantity = quantity - ? WHERE book = ?";
                zmniejszIloscKsiazek = connection.prepareStatement(queryUsun);
                zmniejszIloscKsiazek.setInt(1, 1);
                zmniejszIloscKsiazek.setString(2, super.book);

                //dodaje do koszyka w bazie danych tyle egzemplarzy ksiazki ile jest w zmiennej "wartosc_spinnera"
                for (int i = 0; i < getWartosc_spinnera(); i++) {
                    if (ile.getInt("quantity") > 0)
                        dodajDoKoszyka.executeUpdate();
                    zmniejszIloscKsiazek.executeUpdate();
                }

                int liczbaKsiazekInt = sprawdzIloscKsiazek().getInt("quantity");
                ustawNaLabelulIloscKsiazek(liczbaKsiazekInt);


                //sprawdza czy dodano >1 ksiazek i informuje o tym uzytkownika
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                if (super.wartosc_spinnera == 1) {
                    alert.setContentText("Dodano do koszyka " + super.wartosc_spinnera + " egzemplarz.");
                } else if (super.wartosc_spinnera > 1) {
                    alert.setContentText("Dodano do koszyka " + super.wartosc_spinnera + " egzemplarze.");
                }
                alert.show();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Niewystarczjaca liczba egzmemplarzy na stanie.");
                alert.show();
                connection.close();
            }
        }catch(SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("BLAD POLACZENIA Z SERWEREM");
            alert.show();
        }finally {
            if (dodajDoKoszyka != null) {
                try {
                    dodajDoKoszyka.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }}

            if (zmniejszIloscKsiazek != null) {
                try {
                    zmniejszIloscKsiazek.close();
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
            if (sprawdzIloscKsiazek != null) {
                try {
                    sprawdzIloscKsiazek.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ile != null) {
                try {
                    ile.close();
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



    public ResultSet sprawdzIloscKsiazek() throws SQLException {
        Connection connection = null;
        PreparedStatement sprawdzIloscKsiazek = null;
        ResultSet ile = null;
            //zwraca ilosc (quantity) danej ksiazki z bazy - zmniejsza sie o n razy gdy dodamy ja n razy do koszyka
            connection = DriverManager.getConnection("jdbc:mysql://localhost/" + dbDane.getDbName(), dbDane.getDbUserName(), dbDane.getDbPassword());
            String queryIleJestKsiazek = "SELECT quantity FROM books WHERE book = ?";
            sprawdzIloscKsiazek = connection.prepareStatement(queryIleJestKsiazek);
            sprawdzIloscKsiazek.setString(1, super.book);
            ile = sprawdzIloscKsiazek.executeQuery();
            ile.next();
            return ile;
        }






    //Obsluga Spinnera
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 15);
        valueFactory.setValue(1);
        spinner_quantity.setValueFactory(valueFactory);
        super.wartosc_spinnera = spinner_quantity.getValue();
        spinner_quantity.valueProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observableValue, Integer integer, Integer t1) {
                //setter zmieniajacy wartosc current_value
                setWartosc_spinnera(spinner_quantity.getValue());
            }
        });
    }


}