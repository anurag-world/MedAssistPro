package com.kodebloc.hospitalmanagementproject.util;

import java.util.regex.Pattern;

public class InputValidator {

    // Updated email pattern to handle common valid formats
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{8,15}$");

    public static boolean isValidEmail(String email) {
        return email != null && !email.isEmpty() && EMAIL_PATTERN.matcher(email).matches();
    }

    // Check if password is valid
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    // Check if phone number is valid
    public static boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && phoneNumber.length() == 10 && PHONE_PATTERN.matcher(phoneNumber).matches();
    }

    // Check if name is valid
    public static boolean isValidName(String name) {
        return name != null && name.length() >= 3 && name.matches("^[a-zA-Z ]+$");
    }
}
