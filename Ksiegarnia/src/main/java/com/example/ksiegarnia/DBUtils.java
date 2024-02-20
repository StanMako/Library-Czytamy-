package com.example.ksiegarnia;

import com.example.ksiegarnia.LoggedInController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
public class DBUtils {

    private final String dbName;
    private final String dbUserName;
    private final String dbPassword;



    DBUtils(){
        this.dbName = "ksiegarnia";
        this.dbUserName = "root";
        this.dbPassword = "";
    }

    public String getDbName()
    {
        return dbName;
    }

    public String getDbUserName()
    {
        return dbUserName;
    }

    public String getDbPassword()
    {
        return dbPassword;
    }

}
