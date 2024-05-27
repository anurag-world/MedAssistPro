package com.kodebloc.hospitalmanagementproject.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class InputValidatorTest {

    @Test
    public void emailValidator_CorrectEmail_ReturnsTrue() {
        assertTrue(InputValidator.isValidEmail("name@example.com"));
    }

    @Test
    public void emailValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(InputValidator.isValidEmail("name@example"));
    }

    @Test
    public void emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(InputValidator.isValidEmail("@example.com"));
    }

    @Test
    public void emailValidator_InvalidEmailSpecialChars_ReturnsFalse() {
        assertFalse(InputValidator.isValidEmail("name@exam!ple.com"));
    }

    @Test
    public void emailValidator_ValidEmailSubdomain_ReturnsTrue() {
        assertTrue(InputValidator.isValidEmail("name@sub.example.com"));
    }

    @Test
    public void emailValidator_ValidEmailWithNumbers_ReturnsTrue() {
        assertTrue(InputValidator.isValidEmail("name123@example.com"));
    }

    @Test
    public void passwordValidator_CorrectPassword_ReturnsTrue() {
        assertTrue(InputValidator.isValidPassword("password123"));
    }

    @Test
    public void passwordValidator_ShortPassword_ReturnsFalse() {
        assertFalse(InputValidator.isValidPassword("123"));
    }

    @Test
    public void passwordValidator_EmptyPassword_ReturnsFalse() {
        assertFalse(InputValidator.isValidPassword(""));
    }

    @Test
    public void passwordValidator_NullPassword_ReturnsFalse() {
        assertFalse(InputValidator.isValidPassword(null));
    }
}
