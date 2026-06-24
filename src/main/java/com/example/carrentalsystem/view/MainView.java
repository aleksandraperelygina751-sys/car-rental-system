package com.example.carrentalsystem.view;

import com.example.carrentalsystem.controller.LoginController;
import com.example.carrentalsystem.dao.*;
import com.example.carrentalsystem.model.*;
import com.example.carrentalsystem.utils.PasswordValidator;
import com.example.carrentalsystem.utils.PriceCalculator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MainView {
    private Stage stage;
    private User currentUser;
    private String role;

    private TableView<Car> carTable;
    private TableView<Client> clientTable;
    private TableView<Contract> contractTable;
    private TableView<Fine> fineTable;

    private CarDAO carDAO;
    private ClientDAO clientDAO;
    private ContractDAO contractDAO;
    private FineDAO fineDAO;
    private UserDAO userDAO;

    public MainView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.role = user.getRole().getName();
        this.stage.setTitle("Прокат автомобилей");

        carDAO = new CarDAO();
        clientDAO = new ClientDAO();
        contractDAO = new ContractDAO();
        fineDAO = new FineDAO();
        userDAO = new UserDAO();

        this.stage.setScene(createScene());
        this.stage.setWidth(1100);
        this.stage.setHeight(700);
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();

        HBox topBox = new HBox(20);
        topBox.setStyle("-fx-background-color: #2E7D32; -fx-padding: 10px;");
        topBox.setAlignment(Pos.CENTER_LEFT);

        Label welcomeLabel = new Label("Добро пожаловать, " + currentUser.getLogin() +
                " (Роль: " + role + ")");
        welcomeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        Button changePassBtn = new Button("Сменить пароль");
        changePassBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-padding: 8px 15px; -fx-font-weight: bold;");
        changePassBtn.setOnAction(e -> showChangePasswordDialog());

        Button logoutBtn = new Button("Выйти");
        logoutBtn.setStyle("-fx-background-color: #d32f2f; -fx-text-fill: white; -fx-padding: 8px 20px; -fx-font-weight: bold;");
        logoutBtn.setOnAction(e -> {
            stage.close();
            Stage loginStage = new Stage();
            LoginView loginView = new LoginView(loginStage);
            new LoginController(loginView);
            loginStage.show();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        topBox.getChildren().addAll(welcomeLabel, spacer, changePassBtn, logoutBtn);
        root.setTop(topBox);

        TabPane tabPane = new TabPane();

        if (role.equals("USER")) {
            Tab orderTab = new Tab("Заказать аренду", createOrderPanel());
            orderTab.setClosable(false);
            tabPane.getTabs().add(orderTab);

            Tab contractsTab = new Tab("Мои договоры", createContractsPanel());
            contractsTab.setClosable(false);
            tabPane.getTabs().add(contractsTab);

            Tab finesTab = new Tab("Мои штрафы", createFinesPanel());
            finesTab.setClosable(false);
            tabPane.getTabs().add(finesTab);

            Tab profileTab = new Tab("Профиль", createProfilePanel());
            profileTab.setClosable(false);
            tabPane.getTabs().add(profileTab);
        } else {
            Tab carsTab = new Tab("Автомобили", createCarsPanel());
            carsTab.setClosable(false);
            tabPane.getTabs().add(carsTab);

            Tab clientsTab = new Tab("Клиенты", createClientsPanel());
            clientsTab.setClosable(false);
            tabPane.getTabs().add(clientsTab);

            Tab contractsTab = new Tab("Договоры", createContractsPanel());
            contractsTab.setClosable(false);
            tabPane.getTabs().add(contractsTab);

            Tab finesTab = new Tab("Штрафы", createFinesPanel());
            finesTab.setClosable(false);
            tabPane.getTabs().add(finesTab);

            Tab profileTab = new Tab("Профиль", createProfilePanel());
            profileTab.setClosable(false);
            tabPane.getTabs().add(profileTab);

            if (role.equals("ADMIN")) {
                Tab usersTab = new Tab("Пользователи", createUsersPanel());
                usersTab.setClosable(false);
                tabPane.getTabs().add(usersTab);
            }
        }

        root.setCenter(tabPane);
        return new Scene(root);
    }

    public void show() {
        stage.show();
    }

    private boolean isAdmin() {
        return role.equals("ADMIN");
    }

    private boolean isManager() {
        return role.equals("MANAGER") || role.equals("ADMIN");
    }

    private int getClientIdByUser(User user) {
        String query = "SELECT id_client FROM clients WHERE id_user = ?";
        try (java.sql.PreparedStatement stmt = DBConnection.getInstance().getConnection().prepareStatement(query)) {
            stmt.setInt(1, user.getId());
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id_client");
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private VBox createOrderPanel() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f9f9f9;");

        Label titleLabel = new Label("Заказать аренду автомобиля");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10));

        ComboBox<Car> carCombo = new ComboBox<>();
        carCombo.setPromptText("Выберите автомобиль");
        carCombo.setPrefWidth(250);
        List<Car> cars = carDAO.getAllCars();
        carCombo.getItems().addAll(cars);

        DatePicker issueDatePicker = new DatePicker(LocalDate.now());
        DatePicker returnDatePicker = new DatePicker(LocalDate.now().plusDays(3));

        TextField priceField = new TextField();
        priceField.setEditable(false);
        priceField.setPromptText("Нажмите 'Рассчитать'");
        priceField.setPrefWidth(200);

        Button calcButton = new Button("Рассчитать стоимость");
        calcButton.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-font-weight: bold;");

        Label carInfoLabel = new Label("Выберите автомобиль");
        carInfoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");

        carCombo.setOnAction(e -> {
            Car selected = carCombo.getValue();
            if (selected != null) {
                double base = PriceCalculator.getBasePrice(selected.getType());
                double coeff = PriceCalculator.getYearCoefficient(selected.getProductionYear());
                carInfoLabel.setText(selected.getBrand() + " | Тип: " + selected.getType() +
                        " | База: " + base + " руб./день x " + String.format("%.2f", coeff));
                priceField.clear();
            }
        });

        calcButton.setOnAction(e -> {
            Car car = carCombo.getValue();
            LocalDate issue = issueDatePicker.getValue();
            LocalDate returnD = returnDatePicker.getValue();

            if (car == null || issue == null || returnD == null) {
                showAlert("Ошибка", "Заполните все поля!");
                return;
            }
            if (returnD.isBefore(issue) || returnD.isEqual(issue)) {
                showAlert("Ошибка", "Дата возврата должна быть позже!");
                return;
            }

            double total = PriceCalculator.calculateTotal(car, issue, returnD);
            long days = java.time.temporal.ChronoUnit.DAYS.between(issue, returnD);
            if (days == 0) days = 1;

            priceField.setText(String.format("%.2f руб. (%.2f руб./день x %d дней)",
                    total, PriceCalculator.getBasePrice(car.getType()) *
                            PriceCalculator.getYearCoefficient(car.getProductionYear()), days));
        });

        Button orderBtn = new Button("Отправить заявку");
        orderBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold;");
        orderBtn.setPrefWidth(200);

        orderBtn.setOnAction(e -> {
            Car car = carCombo.getValue();
            LocalDate issue = issueDatePicker.getValue();
            LocalDate returnD = returnDatePicker.getValue();

            if (car == null || issue == null || returnD == null) {
                showAlert("Ошибка", "Заполните все поля!");
                return;
            }

            if (returnD.isBefore(issue) || returnD.isEqual(issue)) {
                showAlert("Ошибка", "Дата возврата должна быть позже!");
                return;
            }

            int clientId = getClientIdByUser(currentUser);
            if (clientId == -1) {
                showAlert("Ошибка", "Клиент не найден!");
                return;
            }

            Client client = clientDAO.getAllClients().stream()
                    .filter(c -> c.getId() == clientId)
                    .findFirst().orElse(null);

            if (client == null) {
                showAlert("Ошибка", "Клиент не найден!");
                return;
            }

            double total = PriceCalculator.calculateTotal(car, issue, returnD);
            Contract contract = new Contract();
            contract.setClient(client);
            contract.setIssueDate(issue);
            contract.setReturnDate(returnD);
            contract.setTotalAmount(BigDecimal.valueOf(total));

            if (contractDAO.addContract(contract)) {
                showAlert("Успех!", "Заявка отправлена! Ожидайте подтверждения менеджера.");
                priceField.clear();
                carCombo.setValue(null);
                carInfoLabel.setText("Выберите автомобиль");
                issueDatePicker.setValue(LocalDate.now());
                returnDatePicker.setValue(LocalDate.now().plusDays(3));
                loadContracts();
            } else {
                showAlert("Ошибка", "Не удалось отправить заявку!");
            }
        });

        int row = 0;
        grid.add(new Label("Автомобиль:"), 0, row);
        grid.add(carCombo, 1, row);

        row++;
        grid.add(carInfoLabel, 1, row);

        row++;
        grid.add(new Label("Дата выдачи:"), 0, row);
        grid.add(issueDatePicker, 1, row);

        row++;
        grid.add(new Label("Дата возврата:"), 0, row);
        grid.add(returnDatePicker, 1, row);

        row++;
        grid.add(new Label("Стоимость:"), 0, row);
        HBox priceBox = new HBox(10);
        priceBox.getChildren().addAll(priceField, calcButton);
        grid.add(priceBox, 1, row);

        vbox.getChildren().addAll(titleLabel, grid, orderBtn);
        return vbox;
    }

    private VBox createProfilePanel() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));
        vbox.setStyle("-fx-background-color: #f5f5f5;");

        Label titleLabel = new Label("Мой профиль");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);

        grid.add(new Label("Логин:"), 0, 0);
        grid.add(new Label(currentUser.getLogin()), 1, 0);

        grid.add(new Label("Роль:"), 0, 1);
        grid.add(new Label(currentUser.getRole().getName()), 1, 1);

        int clientId = getClientIdByUser(currentUser);
        if (clientId != -1) {
            List<Client> clients = clientDAO.getAllClients();
            for (Client client : clients) {
                if (client.getId() == clientId) {
                    grid.add(new Label("ФИО:"), 0, 2);
                    grid.add(new Label(client.getFullName()), 1, 2);

                    grid.add(new Label("Телефон:"), 0, 3);
                    grid.add(new Label(client.getPhone() != null ? client.getPhone() : "Не указан"), 1, 3);

                    grid.add(new Label("Адрес:"), 0, 4);
                    grid.add(new Label(client.getAddress() != null ? client.getAddress() : "Не указан"), 1, 4);

                    if (client.getDiscount() != null) {
                        grid.add(new Label("Скидка:"), 0, 5);
                        grid.add(new Label(client.getDiscount().getName() + " (" +
                                client.getDiscount().getPercentSize() + "%)"), 1, 5);
                    }
                    break;
                }
            }
        }

        Button changePassBtn = new Button("Сменить пароль");
        changePassBtn.setStyle("-fx-background-color: #1976D2; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-font-weight: bold;");
        changePassBtn.setOnAction(e -> showChangePasswordDialog());

        vbox.getChildren().addAll(titleLabel, grid, changePassBtn);
        return vbox;
    }

    private VBox createCarsPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        carTable = new TableView<>();

        TableColumn<Car, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Car, String> colBrand = new TableColumn<>("Марка");
        colBrand.setCellValueFactory(new PropertyValueFactory<>("brand"));
        colBrand.setPrefWidth(200);

        TableColumn<Car, Integer> colYear = new TableColumn<>("Год");
        colYear.setCellValueFactory(new PropertyValueFactory<>("productionYear"));
        colYear.setPrefWidth(100);

        TableColumn<Car, String> colType = new TableColumn<>("Тип");
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colType.setPrefWidth(150);

        carTable.getColumns().addAll(colId, colBrand, colYear, colType);

        HBox buttonBox = new HBox(10);

        if (isManager()) {
            Button addBtn = new Button("Добавить");
            Button editBtn = new Button("Изменить");
            addBtn.setOnAction(e -> showAddCarDialog());
            editBtn.setOnAction(e -> showEditCarDialog());
            buttonBox.getChildren().addAll(addBtn, editBtn);
        }

        if (isAdmin()) {
            Button deleteBtn = new Button("Удалить");
            deleteBtn.setOnAction(e -> deleteCar());
            buttonBox.getChildren().add(deleteBtn);
        }

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadCars());
        buttonBox.getChildren().add(refreshBtn);

        vbox.getChildren().addAll(carTable, buttonBox);
        loadCars();
        return vbox;
    }

    private void loadCars() {
        List<Car> cars = carDAO.getAllCars();
        ObservableList<Car> data = FXCollections.observableArrayList(cars);
        carTable.setItems(data);
    }

    private void showAddCarDialog() {
        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Добавление автомобиля");
        dialog.setHeaderText(null);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField brandField = new TextField();
        brandField.setPromptText("Марка");
        TextField yearField = new TextField();
        yearField.setPromptText("Год выпуска");
        TextField typeField = new TextField();
        typeField.setPromptText("Тип");

        grid.add(new Label("Марка:"), 0, 0);
        grid.add(brandField, 1, 0);
        grid.add(new Label("Год выпуска:"), 0, 1);
        grid.add(yearField, 1, 1);
        grid.add(new Label("Тип:"), 0, 2);
        grid.add(typeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtn) {
                try {
                    String brand = brandField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    String type = typeField.getText();
                    return new Car(0, brand, year, type);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Год должен быть числом");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(car -> {
            if (carDAO.addCar(car)) {
                loadCars();
                showAlert("Успех", "Автомобиль добавлен");
            }
        });
    }

    private void showEditCarDialog() {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите автомобиль");
            return;
        }

        Dialog<Car> dialog = new Dialog<>();
        dialog.setTitle("Редактирование автомобиля");
        dialog.setHeaderText(null);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField brandField = new TextField(selected.getBrand());
        TextField yearField = new TextField(String.valueOf(selected.getProductionYear()));
        TextField typeField = new TextField(selected.getType());

        grid.add(new Label("Марка:"), 0, 0);
        grid.add(brandField, 1, 0);
        grid.add(new Label("Год выпуска:"), 0, 1);
        grid.add(yearField, 1, 1);
        grid.add(new Label("Тип:"), 0, 2);
        grid.add(typeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtn) {
                try {
                    String brand = brandField.getText();
                    int year = Integer.parseInt(yearField.getText());
                    String type = typeField.getText();
                    return new Car(selected.getId(), brand, year, type);
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Год должен быть числом");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(car -> {
            if (carDAO.updateCar(car)) {
                loadCars();
                showAlert("Успех", "Автомобиль обновлен");
            }
        });
    }

    private void deleteCar() {
        Car selected = carTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите автомобиль");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Удалить автомобиль?");
        alert.setContentText("Вы уверены, что хотите удалить " + selected.getBrand() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (carDAO.deleteCar(selected.getId())) {
                    loadCars();
                    showAlert("Успех", "Автомобиль удален");
                }
            }
        });
    }

    private VBox createClientsPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        clientTable = new TableView<>();

        TableColumn<Client, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Client, String> colName = new TableColumn<>("ФИО");
        colName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colName.setPrefWidth(250);

        TableColumn<Client, String> colPhone = new TableColumn<>("Телефон");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPhone.setPrefWidth(150);

        TableColumn<Client, String> colAddress = new TableColumn<>("Адрес");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colAddress.setPrefWidth(200);

        TableColumn<Client, String> colDiscount = new TableColumn<>("Скидка");
        colDiscount.setCellValueFactory(new PropertyValueFactory<>("discount"));
        colDiscount.setPrefWidth(150);

        clientTable.getColumns().addAll(colId, colName, colPhone, colAddress, colDiscount);

        HBox buttonBox = new HBox(10);

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadClients());
        buttonBox.getChildren().add(refreshBtn);

        if (isAdmin()) {
            Button deleteBtn = new Button("Удалить");
            deleteBtn.setOnAction(e -> deleteClient());
            buttonBox.getChildren().add(deleteBtn);
        }

        vbox.getChildren().addAll(clientTable, buttonBox);
        loadClients();
        return vbox;
    }

    private void loadClients() {
        List<Client> clients = clientDAO.getAllClients();
        ObservableList<Client> data = FXCollections.observableArrayList(clients);
        clientTable.setItems(data);
    }

    private void deleteClient() {
        Client selected = clientTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Удалить клиента?");
        alert.setContentText("Вы уверены, что хотите удалить " + selected.getFullName() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (clientDAO.deleteClient(selected.getId())) {
                    loadClients();
                    showAlert("Успех", "Клиент удалён!");
                } else {
                    showAlert("Ошибка", "Не удалось удалить клиента!");
                }
            }
        });
    }

    private VBox createContractsPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        contractTable = new TableView<>();

        TableColumn<Contract, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Contract, String> colClient = new TableColumn<>("Клиент");
        colClient.setCellValueFactory(new PropertyValueFactory<>("client"));
        colClient.setPrefWidth(200);

        TableColumn<Contract, String> colIssue = new TableColumn<>("Дата выдачи");
        colIssue.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colIssue.setPrefWidth(120);

        TableColumn<Contract, String> colReturn = new TableColumn<>("Дата возврата");
        colReturn.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colReturn.setPrefWidth(120);

        TableColumn<Contract, String> colAmount = new TableColumn<>("Сумма");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        colAmount.setPrefWidth(100);

        contractTable.getColumns().addAll(colId, colClient, colIssue, colReturn, colAmount);

        HBox buttonBox = new HBox(10);

        if (isManager()) {
            Button addBtn = new Button("Новый договор");
            addBtn.setOnAction(e -> showAddContractDialog());
            buttonBox.getChildren().add(addBtn);
        }

        if (isAdmin()) {
            Button deleteBtn = new Button("Удалить");
            deleteBtn.setOnAction(e -> deleteContract());
            buttonBox.getChildren().add(deleteBtn);
        }

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadContracts());
        buttonBox.getChildren().add(refreshBtn);

        vbox.getChildren().addAll(contractTable, buttonBox);
        loadContracts();
        return vbox;
    }

    private void loadContracts() {
        List<Contract> contracts;

        if (role.equals("USER")) {
            int clientId = getClientIdByUser(currentUser);
            if (clientId != -1) {
                contracts = contractDAO.getContractsByClientId(clientId);
            } else {
                contracts = new ArrayList<>();
            }
        } else {
            contracts = contractDAO.getAllContracts();
        }

        ObservableList<Contract> data = FXCollections.observableArrayList(contracts);
        contractTable.setItems(data);
    }

    private void deleteContract() {
        Contract selected = contractTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите договор!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Удалить договор?");
        alert.setContentText("Вы уверены, что хотите удалить договор #" + selected.getId() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (contractDAO.deleteContract(selected.getId())) {
                    loadContracts();
                    showAlert("Успех", "Договор удалён!");
                } else {
                    showAlert("Ошибка", "Не удалось удалить договор!");
                }
            }
        });
    }

    private void showAddContractDialog() {
        Dialog<Contract> dialog = new Dialog<>();
        dialog.setTitle("Новый договор аренды");
        dialog.setHeaderText("Заполните данные для аренды");

        ButtonType createBtn = new ButtonType("Создать договор", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20));

        ComboBox<Client> clientCombo = new ComboBox<>();
        clientCombo.getItems().addAll(clientDAO.getAllClients());

        ComboBox<Car> carCombo = new ComboBox<>();
        carCombo.getItems().addAll(carDAO.getAllCars());

        DatePicker issueDatePicker = new DatePicker(LocalDate.now());
        DatePicker returnDatePicker = new DatePicker(LocalDate.now().plusDays(3));

        TextField priceField = new TextField();
        priceField.setEditable(false);
        priceField.setPromptText("Нажмите 'Рассчитать'");

        Button calcButton = new Button("Рассчитать стоимость");

        Label carInfoLabel = new Label("Выберите автомобиль");
        carInfoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #666;");

        carCombo.setOnAction(e -> {
            Car selected = carCombo.getValue();
            if (selected != null) {
                double base = PriceCalculator.getBasePrice(selected.getType());
                double coeff = PriceCalculator.getYearCoefficient(selected.getProductionYear());
                carInfoLabel.setText(selected.getBrand() + " | Тип: " + selected.getType() +
                        " | База: " + base + " руб./день x " + String.format("%.2f", coeff));
                priceField.clear();
            }
        });

        calcButton.setOnAction(e -> {
            Car car = carCombo.getValue();
            LocalDate issue = issueDatePicker.getValue();
            LocalDate returnD = returnDatePicker.getValue();

            if (car == null || issue == null || returnD == null) {
                showAlert("Ошибка", "Заполните все поля!");
                return;
            }
            if (returnD.isBefore(issue) || returnD.isEqual(issue)) {
                showAlert("Ошибка", "Дата возврата должна быть позже!");
                return;
            }

            double total = PriceCalculator.calculateTotal(car, issue, returnD);
            long days = java.time.temporal.ChronoUnit.DAYS.between(issue, returnD);
            if (days == 0) days = 1;

            priceField.setText(String.format("%.2f руб. (%.2f руб./день x %d дней)",
                    total, PriceCalculator.getBasePrice(car.getType()) *
                            PriceCalculator.getYearCoefficient(car.getProductionYear()), days));

            showAlert("Расчёт", "Итоговая стоимость: " + String.format("%.2f руб.", total));
        });

        int row = 0;
        grid.add(new Label("Клиент:"), 0, row);
        grid.add(clientCombo, 1, row);
        row++;
        grid.add(new Label("Автомобиль:"), 0, row);
        grid.add(carCombo, 1, row);
        row++;
        grid.add(carInfoLabel, 1, row);
        row++;
        grid.add(new Label("Дата выдачи:"), 0, row);
        grid.add(issueDatePicker, 1, row);
        row++;
        grid.add(new Label("Дата возврата:"), 0, row);
        grid.add(returnDatePicker, 1, row);
        row++;
        grid.add(new Label("Стоимость:"), 0, row);
        HBox priceBox = new HBox(10);
        priceBox.getChildren().addAll(priceField, calcButton);
        grid.add(priceBox, 1, row);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createBtn) {
                Client client = clientCombo.getValue();
                Car car = carCombo.getValue();
                LocalDate issue = issueDatePicker.getValue();
                LocalDate returnD = returnDatePicker.getValue();

                if (client == null || car == null || issue == null || returnD == null) {
                    showAlert("Ошибка", "Заполните все поля!");
                    return null;
                }

                double total = PriceCalculator.calculateTotal(car, issue, returnD);
                Contract contract = new Contract();
                contract.setClient(client);
                contract.setIssueDate(issue);
                contract.setReturnDate(returnD);
                contract.setTotalAmount(BigDecimal.valueOf(total));
                return contract;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(contract -> {
            if (contractDAO.addContract(contract)) {
                loadContracts();
                showAlert("Успех", "Договор создан! Стоимость: " + contract.getTotalAmount() + " руб.");
            } else {
                showAlert("Ошибка", "Не удалось создать договор");
            }
        });
    }

    private VBox createFinesPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        fineTable = new TableView<>();

        TableColumn<Fine, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Fine, String> colReason = new TableColumn<>("Причина");
        colReason.setCellValueFactory(new PropertyValueFactory<>("reason"));
        colReason.setPrefWidth(200);

        TableColumn<Fine, String> colAmount = new TableColumn<>("Сумма");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colAmount.setPrefWidth(100);

        TableColumn<Fine, Boolean> colPaid = new TableColumn<>("Оплачен");
        colPaid.setCellValueFactory(new PropertyValueFactory<>("paid"));
        colPaid.setPrefWidth(80);

        TableColumn<Fine, String> colContract = new TableColumn<>("Договор");
        colContract.setCellValueFactory(new PropertyValueFactory<>("contract"));
        colContract.setPrefWidth(200);

        fineTable.getColumns().addAll(colId, colReason, colAmount, colPaid, colContract);

        HBox buttonBox = new HBox(10);

        if (isManager()) {
            Button addBtn = new Button("Добавить штраф");
            addBtn.setOnAction(e -> showAddFineDialog());
            buttonBox.getChildren().add(addBtn);
        }

        if (isAdmin()) {
            Button deleteBtn = new Button("Удалить");
            deleteBtn.setOnAction(e -> deleteFine());
            buttonBox.getChildren().add(deleteBtn);
        }

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadFines());
        buttonBox.getChildren().add(refreshBtn);

        vbox.getChildren().addAll(fineTable, buttonBox);
        loadFines();
        return vbox;
    }

    private void loadFines() {
        List<Fine> fines;

        if (role.equals("USER")) {
            int clientId = getClientIdByUser(currentUser);
            if (clientId != -1) {
                fines = fineDAO.getFinesByClientId(clientId);
            } else {
                fines = new ArrayList<>();
            }
        } else {
            fines = fineDAO.getAllFines();
        }

        ObservableList<Fine> data = FXCollections.observableArrayList(fines);
        fineTable.setItems(data);
    }

    private void deleteFine() {
        Fine selected = fineTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите штраф!");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Удалить штраф?");
        alert.setContentText("Вы уверены, что хотите удалить штраф #" + selected.getId() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if (fineDAO.deleteFine(selected.getId())) {
                    loadFines();
                    showAlert("Успех", "Штраф удалён!");
                } else {
                    showAlert("Ошибка", "Не удалось удалить штраф!");
                }
            }
        });
    }

    private void showAddFineDialog() {
        Dialog<Fine> dialog = new Dialog<>();
        dialog.setTitle("Добавление штрафа");
        dialog.setHeaderText(null);

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField reasonField = new TextField();
        reasonField.setPromptText("Причина");

        TextField amountField = new TextField();
        amountField.setPromptText("Сумма");

        ComboBox<Contract> contractCombo = new ComboBox<>();
        contractCombo.setPromptText("Выберите договор");
        contractCombo.getItems().addAll(contractDAO.getAllContracts());

        CheckBox paidCheck = new CheckBox("Оплачен");

        grid.add(new Label("Причина:"), 0, 0);
        grid.add(reasonField, 1, 0);
        grid.add(new Label("Сумма:"), 0, 1);
        grid.add(amountField, 1, 1);
        grid.add(new Label("Договор:"), 0, 2);
        grid.add(contractCombo, 1, 2);
        grid.add(paidCheck, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtn) {
                try {
                    String reason = reasonField.getText();
                    BigDecimal amount = new BigDecimal(amountField.getText());
                    boolean paid = paidCheck.isSelected();
                    Contract contract = contractCombo.getValue();

                    if (contract == null) {
                        showAlert("Ошибка", "Выберите договор!");
                        return null;
                    }

                    Fine fine = new Fine();
                    fine.setReason(reason);
                    fine.setAmount(amount);
                    fine.setPaid(paid);
                    fine.setContract(contract);
                    return fine;
                } catch (NumberFormatException e) {
                    showAlert("Ошибка", "Сумма должна быть числом!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(fine -> {
            if (fineDAO.addFine(fine)) {
                loadFines();
                showAlert("Успех", "Штраф добавлен!");
            } else {
                showAlert("Ошибка", "Не удалось добавить штраф!");
            }
        });
    }

    private VBox createUsersPanel() {
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(15));

        TableView<User> userTable = new TableView<>();

        TableColumn<User, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<User, String> colLogin = new TableColumn<>("Логин");
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colLogin.setPrefWidth(150);

        TableColumn<User, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        TableColumn<User, String> colRole = new TableColumn<>("Роль");
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setPrefWidth(150);

        userTable.getColumns().addAll(colId, colLogin, colEmail, colRole);

        List<User> users = userDAO.getAllUsers();
        ObservableList<User> data = FXCollections.observableArrayList(users);
        userTable.setItems(data);

        HBox buttonBox = new HBox(10);

        if (isAdmin()) {
            Button deleteBtn = new Button("Удалить");
            deleteBtn.setOnAction(e -> {
                User selected = userTable.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    showAlert("Ошибка", "Выберите пользователя!");
                    return;
                }
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Подтверждение");
                alert.setHeaderText("Удалить пользователя?");
                alert.setContentText("Вы уверены, что хотите удалить " + selected.getLogin() + "?");
                alert.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.OK) {
                        if (userDAO.deleteUser(selected.getId())) {
                            userTable.getItems().remove(selected);
                            showAlert("Успех", "Пользователь удалён!");
                        }
                    }
                });
            });
            buttonBox.getChildren().add(deleteBtn);
        }

        vbox.getChildren().addAll(userTable, buttonBox);
        return vbox;
    }

    private void showChangePasswordDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Смена пароля");
        dialog.setHeaderText("Изменить пароль для " + currentUser.getLogin());

        ButtonType saveBtn = new ButtonType("Сохранить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        PasswordField oldPass = new PasswordField();
        oldPass.setPromptText("Старый пароль");
        PasswordField newPass = new PasswordField();
        newPass.setPromptText("Новый пароль");
        PasswordField confirmPass = new PasswordField();
        confirmPass.setPromptText("Подтвердите пароль");
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        grid.add(new Label("Старый пароль:"), 0, 0);
        grid.add(oldPass, 1, 0);
        grid.add(new Label("Новый пароль:"), 0, 1);
        grid.add(newPass, 1, 1);
        grid.add(new Label("Подтверждение:"), 0, 2);
        grid.add(confirmPass, 1, 2);
        grid.add(errorLabel, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveBtn) {
                String old = oldPass.getText();
                String newP = newPass.getText();
                String confirm = confirmPass.getText();

                if (!newP.equals(confirm)) {
                    errorLabel.setText("Пароли не совпадают!");
                    return null;
                }

                if (!PasswordValidator.isValid(newP)) {
                    errorLabel.setText(PasswordValidator.getRequirements());
                    return null;
                }

                if (!old.equals(currentUser.getPasswordHash())) {
                    errorLabel.setText("Неверный старый пароль!");
                    return null;
                }

                return newP;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newPassword -> {
            if (userDAO.updatePassword(currentUser.getId(), newPassword)) {
                showAlert("Успех", "Пароль изменён!");
                currentUser.setPasswordHash(newPassword);
            } else {
                showAlert("Ошибка", "Не удалось изменить пароль");
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}