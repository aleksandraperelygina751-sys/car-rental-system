package com.example.carrentalsystem.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterView {
    private Stage stage;
    private TextField loginField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmField;
    private Button registerButton;
    private Button backButton;
    private Label messageLabel;

    public RegisterView(Stage stage) {
        this.stage = stage;
        stage.setTitle("Регистрация");
        stage.setScene(createScene());
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    private Scene createScene() {
        Label titleLabel = new Label("Регистрация");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        loginField = new TextField();
        loginField.setPromptText("Логин");
        loginField.setPrefWidth(200);

        emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setPrefWidth(200);

        passwordField = new PasswordField();
        passwordField.setPromptText("Пароль (8+ символов, цифра, заглавная, спецсимвол)");
        passwordField.setPrefWidth(200);

        confirmField = new PasswordField();
        confirmField.setPromptText("Подтвердите пароль");
        confirmField.setPrefWidth(200);

        registerButton = new Button("Зарегистрироваться");
        registerButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        registerButton.setPrefWidth(200);

        backButton = new Button("Назад ко входу");
        backButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        backButton.setPrefWidth(200);

        messageLabel = new Label();
        messageLabel.setStyle("-fx-text-fill: red;");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30));
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        grid.add(new Label("Логин:"), 0, 0);
        grid.add(loginField, 1, 0);
        grid.add(new Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new Label("Подтверждение:"), 0, 3);
        grid.add(confirmField, 1, 3);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, backButton, messageLabel);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(titleLabel, grid, buttonBox);

        return new Scene(root, 450, 500);
    }

    public String getLogin() { return loginField.getText(); }
    public String getEmail() { return emailField.getText(); }
    public String getPassword() { return passwordField.getText(); }
    public String getConfirmPassword() { return confirmField.getText(); }
    public Button getRegisterButton() { return registerButton; }
    public Button getBackButton() { return backButton; }
    public void setMessage(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: red;");
    }
    public void setSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: green;");
    }
    public void close() { stage.close(); }
    public Stage getStage() { return stage; }
}