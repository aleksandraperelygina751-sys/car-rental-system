package com.example.carrentalsystem;

import com.example.carrentalsystem.controller.LoginController;
import com.example.carrentalsystem.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApplication extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        new LoginController(loginView);
        primaryStage.show();
    }
}