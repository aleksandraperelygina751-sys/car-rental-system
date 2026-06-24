-- Создание базы данных
CREATE DATABASE IF NOT EXISTS car_rental;
USE car_rental;

-- Таблица автомобилей
CREATE TABLE cars (
  id_car INT PRIMARY KEY,
  brand VARCHAR(100) NOT NULL,
  production_year INT NOT NULL,
  type VARCHAR(100) NOT NULL
);

-- Таблица скидок
CREATE TABLE discounts (
  id_discount INT PRIMARY KEY,
  name VARCHAR(100) UNIQUE NOT NULL,
  percent_size DECIMAL(5,2) NOT NULL,
  min_rentals INT NOT NULL
);

-- Таблица клиентов
CREATE TABLE clients (
  id_client INT PRIMARY KEY,
  full_name VARCHAR(255) NOT NULL,
  phone VARCHAR(20) UNIQUE,
  address VARCHAR(255),
  id_discount INT,
  id_user INT,
  FOREIGN KEY (id_discount) REFERENCES discounts(id_discount) ON DELETE SET NULL
);

-- Таблица договоров
CREATE TABLE contracts (
  id_contract INT PRIMARY KEY,
  issue_date DATE NOT NULL,
  return_date DATE NOT NULL,
  total_amount DECIMAL(10,2) DEFAULT 0.00,
  id_client INT NOT NULL,
  id_discount INT,
  FOREIGN KEY (id_client) REFERENCES clients(id_client),
  FOREIGN KEY (id_discount) REFERENCES discounts(id_discount) ON DELETE SET NULL
);

-- Таблица штрафов
CREATE TABLE fines (
  id_fine INT PRIMARY KEY,
  reason VARCHAR(255) NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  paid TINYINT(1) DEFAULT 0,
  id_contract INT NOT NULL,
  FOREIGN KEY (id_contract) REFERENCES contracts(id_contract) ON DELETE CASCADE
);

-- Таблица ролей
CREATE TABLE roles (
  id_role INT PRIMARY KEY,
  name VARCHAR(50) UNIQUE NOT NULL,
  description VARCHAR(255)
);

-- Таблица пользователей
CREATE TABLE users (
  id_user INT PRIMARY KEY,
  login VARCHAR(50) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  id_role INT NOT NULL,
  FOREIGN KEY (id_role) REFERENCES roles(id_role)
);

-- Связующая таблица договоров и автомобилей
CREATE TABLE contract_car (
  id_contract INT,
  id_car INT,
  cost DECIMAL(10,2) NOT NULL,
  PRIMARY KEY (id_contract, id_car),
  FOREIGN KEY (id_contract) REFERENCES contracts(id_contract) ON DELETE CASCADE,
  FOREIGN KEY (id_car) REFERENCES cars(id_car)
);

-- Роли
INSERT INTO roles (id_role, name, description) VALUES
(1, 'ADMIN', 'Полный доступ ко всем функциям системы'),
(2, 'MANAGER', 'Управление договорами, клиентами и автомобилями'),
(3, 'USER', 'Просмотр своих договоров');

-- Скидки
INSERT INTO discounts (id_discount, name, percent_size, min_rentals) VALUES
(1, 'Новичок', 3.00, 1),
(2, 'Постоянный клиент', 5.00, 3),
(3, 'VIP', 10.00, 10);

-- Автомобили
INSERT INTO cars (id_car, brand, production_year, type) VALUES
(1, 'Audi', 2022, 'Седан'),
(2, 'BMW', 2023, 'Кроссовер'),
(3, 'Toyota', 2021, 'Хэтчбек'),
(4, 'Mercedes', 2024, 'Седан'),
(5, 'Kia', 2022, 'Универсал'),
(6, 'Hyundai', 2023, 'Кроссовер'),
(7, 'Volkswagen', 2021, 'Седан'),
(8, 'Skoda', 2022, 'Лифтбек');

-- Пользователи
INSERT INTO users (id_user, login, password_hash, email, id_role) VALUES
(1, 'admin', 'Admin2024!', 'admin@carrental.ru', 1),
(2, 'manager', 'Manager2024!', 'manager@carrental.ru', 2),
(3, 'alexandra', 'Alexandra2024!', 'alexandra@mail.ru', 3),
(4, 'darya', 'Darya2024!', 'darya@mail.ru', 3),
(5, 'varvara', 'Varvara2024!', 'varvara@mail.ru', 3),
(6, 'sergey', 'Sergey2024!', 'sergey@mail.ru', 3),
(7, 'fedor', 'Fedor2024!', 'fedor@mail.ru', 3);

-- Клиенты
INSERT INTO clients (id_client, full_name, phone, address, id_discount, id_user) VALUES
(1, 'Перелыгина Александра Дмитриевна', '+79464788566', 'Москва', 1, 3),
(2, 'Мантула Дарья Ивановна', '+79993675437', 'Санкт-Петербург', 2, 4),
(3, 'Хоркина Варвара Андреевна', '+79342758493', 'Волгоград', 2, 5),
(4, 'Колпаков Сергей Александрович', '+79671647382', 'Казань', 3, 6),
(5, 'Колпаков Федор Антонович', '+79469847635', 'Ухта', NULL, 7);

-- Договоры
INSERT INTO contracts (id_contract, issue_date, return_date, total_amount, id_client) VALUES
(1, '2026-05-01', '2026-05-05', 12000.00, 1),
(2, '2026-05-10', '2026-05-15', 18000.00, 2),
(3, '2026-05-20', '2026-05-25', 15000.00, 4);

-- Штрафы
INSERT INTO fines (id_fine, reason, amount, paid, id_contract) VALUES
(1, 'Просрочка возврата', 3000.00, 0, 1),
(2, 'Повреждение кузова', 7000.00, 1, 2);

-- Связь договоров и автомобилей
INSERT INTO contract_car (id_contract, id_car, cost) VALUES
(1, 1, 12000.00),
(2, 2, 18000.00),
(3, 4, 15000.00);
