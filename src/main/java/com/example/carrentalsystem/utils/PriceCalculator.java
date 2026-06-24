package com.example.carrentalsystem.utils;

import com.example.carrentalsystem.model.Car;
import com.example.carrentalsystem.model.Discount;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PriceCalculator {

    public static double getBasePrice(String type) {
        switch (type.toLowerCase()) {
            case "седан": return 2000.0;
            case "кроссовер": return 3000.0;
            case "хэтчбек": return 1800.0;
            case "универсал": return 2500.0;
            case "лифтбек": return 2200.0;
            default: return 2000.0;
        }
    }

    public static double getYearCoefficient(int year) {
        int currentYear = LocalDate.now().getYear();
        int age = currentYear - year;

        if (age <= 1) return 1.2;
        if (age <= 3) return 1.0;
        if (age <= 5) return 0.85;
        return 0.7;
    }

    public static double calculateTotal(Car car, LocalDate issueDate, LocalDate returnDate) {
        long days = ChronoUnit.DAYS.between(issueDate, returnDate);
        if (days <= 0) days = 1;

        double basePrice = getBasePrice(car.getType());
        double coefficient = getYearCoefficient(car.getProductionYear());
        double dailyPrice = basePrice * coefficient;

        return dailyPrice * days;
    }

    public static double calculateTotalWithDiscount(Car car, LocalDate issueDate, LocalDate returnDate, Discount discount) {
        double total = calculateTotal(car, issueDate, returnDate);
        if (discount != null) {
            double percent = discount.getPercentSize().doubleValue();
            total = total * (1 - percent / 100);
        }
        return total;
    }

    public static String formatPrice(double price) {
        return String.format("%.2f руб.", price);
    }
}