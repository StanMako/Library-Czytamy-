package com.example.ksiegarnia;


import javafx.fxml.FXML;
import javafx.scene.control.Button;


import java.util.ArrayList;

public class UserData {
    public static String username;
    public static String password;
    public  static String gender;
    public static String book;

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

    ArrayList<Button> listaFXML = new ArrayList<>();

    public void dodajDoArrayListy()
    {
        listaFXML.add(button_zobacz1);
        listaFXML.add(button_zobacz2);
        listaFXML.add(button_zobacz3);
        listaFXML.add(button_zobacz4);
        listaFXML.add(button_zobacz5);
        listaFXML.add(button_zobacz6);
    }


    //zmienna okreslajace ile jest ksiazek w bazie


    //zmienna ktora pokazuje ile obecnie chcemy dodac ksiazek do koszyka
    public static int wartosc_spinnera;

    public static void setWartosc_spinnera(int liczba)
    {
        wartosc_spinnera = liczba;
    }

    public static int getWartosc_spinnera()
    {
        return wartosc_spinnera;
    }

}
