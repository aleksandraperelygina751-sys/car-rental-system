package com.example.carrentalsystem.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView {
    private Stage stage;
    private TextField loginField;
    private PasswordField passwordField;
    private Button loginButton;
    private Label messageLabel;

    public LoginView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Авторизация — Прокат автомобилей");
        stage.setScene(createScene());
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    private Scene createScene() {
        Label titleLabel = new Label("🚗 Вход в систему");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        loginField = new TextField();
        loginField.setPromptText("Введите логин");
        loginField.setPrefWidth(200);

        passwordField = new PasswordField();
        passwordField.setPromptText("Введите пароль");
        passwordField.setPrefWidth(200);

        loginButton = new Button("Войти");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        loginButton.setPrefWidth(150);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setHgap(10);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Логин:"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Пароль:"), 0, 1);
        grid.add(passwordField, 1, 1);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(loginButton, messageLabel);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(titleLabel, grid, buttonBox);

        return new Scene(root, 400, 320);
    }

    public String getLogin() { return loginField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public Button getLoginButton() { return loginButton; }
    public void setMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
    public void close() { stage.close(); }
}