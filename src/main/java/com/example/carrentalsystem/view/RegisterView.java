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
    private TextField fullNameField;
    private TextField phoneField;
    private TextField addressField;
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
        Label titleLabel = new Label("Регистрация нового клиента");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        fullNameField = new TextField();
        fullNameField.setPromptText("ФИО");
        fullNameField.setPrefWidth(250);

        phoneField = new TextField();
        phoneField.setPromptText("Телефон");
        phoneField.setPrefWidth(250);

        addressField = new TextField();
        addressField.setPromptText("Адрес");
        addressField.setPrefWidth(250);

        loginField = new TextField();
        loginField.setPromptText("Логин");
        loginField.setPrefWidth(250);

        emailField = new TextField();
        emailField.setPromptText("Email (необязательно)");
        emailField.setPrefWidth(250);

        passwordField = new PasswordField();
        passwordField.setPromptText("Пароль (8+, цифра, заглавная, спецсимвол)");
        passwordField.setPrefWidth(250);

        confirmField = new PasswordField();
        confirmField.setPromptText("Подтвердите пароль");
        confirmField.setPrefWidth(250);

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
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        int row = 0;
        grid.add(new Label("ФИО:"), 0, row);
        grid.add(fullNameField, 1, row);

        row++;
        grid.add(new Label("Телефон:"), 0, row);
        grid.add(phoneField, 1, row);

        row++;
        grid.add(new Label("Адрес:"), 0, row);
        grid.add(addressField, 1, row);

        row++;
        grid.add(new Label("Логин:"), 0, row);
        grid.add(loginField, 1, row);

        row++;
        grid.add(new Label("Email:"), 0, row);
        grid.add(emailField, 1, row);

        row++;
        grid.add(new Label("Пароль:"), 0, row);
        grid.add(passwordField, 1, row);

        row++;
        grid.add(new Label("Подтверждение:"), 0, row);
        grid.add(confirmField, 1, row);

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, backButton, messageLabel);

        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(titleLabel, grid, buttonBox);

        return new Scene(root, 500, 650);
    }

    public String getFullName() { return fullNameField.getText(); }
    public String getPhone() { return phoneField.getText(); }
    public String getAddress() { return addressField.getText(); }
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