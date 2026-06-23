package com.example.carrentalsystem.view;

import com.example.carrentalsystem.dao.*;
import com.example.carrentalsystem.model.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class MainView {
    private Stage stage;
    private User currentUser;
    private TabPane tabPane;

    private TableView<Car> carTable;
    private TableView<Client> clientTable;
    private TableView<Contract> contractTable;
    private TableView<Fine> fineTable;

    private CarDAO carDAO;
    private ClientDAO clientDAO;
    private ContractDAO contractDAO;
    private FineDAO fineDAO;

    public MainView(Stage stage, User user) {
        this.stage = stage;
        this.currentUser = user;
        this.stage.setTitle("Прокат автомобилей");

        carDAO = new CarDAO();
        clientDAO = new ClientDAO();
        contractDAO = new ContractDAO();
        fineDAO = new FineDAO();

        this.stage.setScene(createScene());
        this.stage.setWidth(1100);
        this.stage.setHeight(700);
    }

    private Scene createScene() {
        BorderPane root = new BorderPane();

        Label welcomeLabel = new Label("Добро пожаловать, " + currentUser.getLogin() +
                " (Роль: " + currentUser.getRole().getName() + ")");
        welcomeLabel.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; " +
                "-fx-padding: 15px; -fx-font-size: 16px; -fx-font-weight: bold;");
        welcomeLabel.setMaxWidth(Double.MAX_VALUE);
        root.setTop(welcomeLabel);

        tabPane = new TabPane();

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

        if (currentUser.getRole().getName().equals("ADMIN")) {
            Tab usersTab = new Tab("Пользователи", createUsersPanel());
            usersTab.setClosable(false);
            tabPane.getTabs().add(usersTab);
        }

        root.setCenter(tabPane);
        return new Scene(root);
    }

    public void show() {
        stage.show();
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
        Button addBtn = new Button("Добавить");
        Button editBtn = new Button("Изменить");
        Button deleteBtn = new Button("Удалить");
        Button refreshBtn = new Button("Обновить");

        addBtn.setOnAction(e -> showAddCarDialog());
        editBtn.setOnAction(e -> showEditCarDialog());
        deleteBtn.setOnAction(e -> deleteCar());
        refreshBtn.setOnAction(e -> loadCars());

        buttonBox.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);
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
                    showAlert("Ошибка", "Год должен быть числом!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(car -> {
            if (carDAO.addCar(car)) {
                loadCars();
                showAlert("Успех", "Автомобиль добавлен");
            } else {
                showAlert("Ошибка", "Не удалось добавить автомобиль");
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
            } else {
                showAlert("Ошибка", "Не удалось обновить автомобиль");
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
                } else {
                    showAlert("Ошибка", "Не удалось удалить автомобиль");
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

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadClients());

        vbox.getChildren().addAll(clientTable, refreshBtn);
        loadClients();

        return vbox;
    }

    private void loadClients() {
        List<Client> clients = clientDAO.getAllClients();
        ObservableList<Client> data = FXCollections.observableArrayList(clients);
        clientTable.setItems(data);
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

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadContracts());

        vbox.getChildren().addAll(contractTable, refreshBtn);
        loadContracts();

        return vbox;
    }

    private void loadContracts() {
        List<Contract> contracts = contractDAO.getAllContracts();
        ObservableList<Contract> data = FXCollections.observableArrayList(contracts);
        contractTable.setItems(data);
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

        TableColumn<Fine, String> colPaid = new TableColumn<>("Оплачен");
        colPaid.setCellValueFactory(new PropertyValueFactory<>("paid"));
        colPaid.setPrefWidth(80);

        TableColumn<Fine, String> colContract = new TableColumn<>("Договор");
        colContract.setCellValueFactory(new PropertyValueFactory<>("contract"));
        colContract.setPrefWidth(200);

        fineTable.getColumns().addAll(colId, colReason, colAmount, colPaid, colContract);

        Button refreshBtn = new Button("Обновить");
        refreshBtn.setOnAction(e -> loadFines());

        vbox.getChildren().addAll(fineTable, refreshBtn);
        loadFines();

        return vbox;
    }

    private void loadFines() {
        List<Fine> fines = fineDAO.getAllFines();
        ObservableList<Fine> data = FXCollections.observableArrayList(fines);
        fineTable.setItems(data);
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

        UserDAO userDAO = new UserDAO();
        List<User> users = userDAO.getAllUsers();
        ObservableList<User> data = FXCollections.observableArrayList(users);
        userTable.setItems(data);

        vbox.getChildren().add(userTable);
        return vbox;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}