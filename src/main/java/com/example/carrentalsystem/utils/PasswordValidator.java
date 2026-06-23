package com.example.carrentalsystem.utils;

public class PasswordValidator {

    public static boolean isValid(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$";
        return password.matches(regex);
    }

    public static String getRequirements() {
        return "Требования к паролю:\n" +
                "- минимум 8 символов\n" +
                "- хотя бы одна цифра\n" +
                "- хотя бы одна заглавная буква\n" +
                "- хотя бы один спецсимвол (!@#$%^&*)";
    }
}