package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dao.UserDAO;
import com.example.carrentalsystem.model.User;
import com.example.carrentalsystem.view.LoginView;
import com.example.carrentalsystem.view.MainView;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class LoginController {
    private LoginView view;
    private UserDAO userDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        view.getLoginButton().setOnAction(e -> handleLogin());
    }

    private void handleLogin() {
        String login = view.getLogin();
        String password = view.getPassword();

        if (login.isEmpty() || password.isEmpty()) {
            view.setMessage("Введите логин и пароль!");
            return;
        }

        User user = userDAO.getUserByLogin(login);

        if (user == null) {
            view.setMessage("Пользователь не найден!");
            return;
        }

        if (!password.equals(user.getPasswordHash())) {
            view.setMessage("Неверный пароль!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех!");
        alert.setHeaderText("Добро пожаловать, " + user.getLogin() + "!");
        alert.setContentText("Роль: " + user.getRole().getName());
        alert.showAndWait();

        view.close();

        Stage mainStage = new Stage();
        MainView mainView = new MainView(mainStage, user);
        mainView.show();
    }
}