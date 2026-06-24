package com.example.carrentalsystem.controller;

import com.example.carrentalsystem.dao.ClientDAO;
import com.example.carrentalsystem.dao.UserDAO;
import com.example.carrentalsystem.model.Client;
import com.example.carrentalsystem.model.Role;
import com.example.carrentalsystem.model.User;
import com.example.carrentalsystem.utils.PasswordValidator;
import com.example.carrentalsystem.view.LoginView;
import com.example.carrentalsystem.view.RegisterView;
import javafx.stage.Stage;

public class RegisterController {
    private RegisterView view;
    private UserDAO userDAO;
    private ClientDAO clientDAO;

    public RegisterController(RegisterView view) {
        this.view = view;
        this.userDAO = new UserDAO();
        this.clientDAO = new ClientDAO();

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
        String fullName = view.getFullName();
        String phone = view.getPhone();
        String address = view.getAddress();
        String login = view.getLogin();
        String email = view.getEmail();
        String password = view.getPassword();
        String confirm = view.getConfirmPassword();

        if (fullName.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                login.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            view.setMessage("Заполните все обязательные поля!");
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
            int userId = newUser.getId();

            Client newClient = new Client();
            newClient.setFullName(fullName);
            newClient.setPhone(phone);
            newClient.setAddress(address);
            newClient.setIdUser(userId);

            if (clientDAO.addClient(newClient)) {
                view.setSuccess("Регистрация успешна! Теперь войдите в систему.");
                view.getRegisterButton().setDisable(true);
            } else {
                view.setMessage("Ошибка при создании клиента!");
            }
        } else {
            view.setMessage("Ошибка при регистрации!");
        }
    }
}