package com.example.ksiegarnia;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
public class LoggedInController extends UserData {
    @FXML
    private Label label_welcome;
    @FXML
    public ImageView Cactus;
    @FXML
    Button button_zobacz1;
    @FXML
    Button button_zobacz2;
    @FXML
    Button button_zobacz3;
    @FXML
    Button button_zobacz4;
    @FXML
    Button button_zobacz5;
    @FXML
    Button button_zobacz6;
    private Stage stage;
    private Scene scene;
    private Parent root;
    private String scena;
    DBUtils dbDane = new DBUtils();
    public void switchToStart(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public void switchToKoszyk(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("koszyk.fxml"));
        root = loader.load();
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

        KoszykController koszykController = loader.getController();
        koszykController.pokazKoszyk(event);
    }
    public void setGreeting(String username)
    {
        label_welcome.setText("Witaj " + super.username);
    }


    public void switchToScene(ActionEvent event) throws Exception {
        dodajDoArrayListy();
        Object source = event.getSource();
        if (source instanceof Button) {
            Button clickedButton = (Button) source;
            int buttonIndex = listaFXML.indexOf(clickedButton);
            String fxmlFile = "";
            switch (buttonIndex) {
                case 0:
                    fxmlFile = "1984.fxml";
                    break;
                case 1:
                    fxmlFile = "Wh40k.fxml";
                    break;
                case 2:
                    fxmlFile = "Jordan.fxml";
                    break;
                case 3:
                    fxmlFile = "Misery.fxml";
                    break;
                case 4:
                    fxmlFile = "Proces.fxml";
                    break;
                case 5:
                    fxmlFile = "Tadeusz.fxml";
                    break;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            //pokazanie ilosci ksiazek
            BookController ustawZmiennaKsiazka = loader.getController();
            ustawZmiennaKsiazka.ustawKsiazke(super.book);
            int liczbaKsiazekInt =ustawZmiennaKsiazka.sprawdzIloscKsiazek().getInt("quantity");
            ustawZmiennaKsiazka.ustawNaLabelulIloscKsiazek(liczbaKsiazekInt);
            listaFXML.clear();
        }
    }
}





