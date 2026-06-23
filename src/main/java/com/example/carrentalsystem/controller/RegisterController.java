package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dao.UserDAO;
import com.example.carrentalsystem.model.Role;
import com.example.carrentalsystem.model.User;
import com.example.carrentalsystem.utils.PasswordValidator;
import com.example.carrentalsystem.view.LoginView;
import com.example.carrentalsystem.view.RegisterView;
import javafx.stage.Stage;

public class RegisterController {
    private RegisterView view;
    private UserDAO userDAO;

    public RegisterController(RegisterView view) {
        this.view = view;
        this.userDAO = new UserDAO();

        view.getRegisterButton().setOnAction(e -> handleRegister());
        view.getBackButton().setOnAction(e -> {
            view.close();
            Stage loginStage = new Stage();
            LoginView loginView = new LoginView(loginStage);
            new LoginController(loginView);
            loginStage.show();
        });
    }

    private void handleRegister() {
        String login = view.getLogin();
        String email = view.getEmail();
        String password = view.getPassword();
        String confirm = view.getConfirmPassword();

        if (login.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            view.setMessage("Заполните все поля!");
            return;
        }

        if (!PasswordValidator.isValid(password)) {
            view.setMessage(PasswordValidator.getRequirements());
            return;
        }

        if (!password.equals(confirm)) {
            view.setMessage("Пароли не совпадают!");
            return;
        }

        if (userDAO.getUserByLogin(login) != null) {
            view.setMessage("Пользователь с таким логином уже существует!");
            return;
        }

        Role userRole = new Role(3, "USER", "Обычный пользователь");
        User newUser = new User(0, login, password, email, userRole);

        if (userDAO.addUser(newUser)) {
            view.setSuccess("Регистрация успешна! Теперь войдите в систему.");
            view.getRegisterButton().setDisable(true);
        } else {
            view.setMessage("Ошибка при регистрации!");
        }
    }
}