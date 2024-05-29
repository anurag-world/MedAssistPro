package com.kodebloc.hospitalmanagementproject.util;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PhoneNumberTest {
    @Test
    public void testValidPhoneNumber() {
        assertTrue(InputValidator.isValidPhoneNumber("1234567890"));
    }

    @Test
    public void testPhoneNumberWithLetters() {
        assertFalse(InputValidator.isValidPhoneNumber("12345abcde"));
    }

    @Test
    public void testPhoneNumberWithSpecialCharacters() {
        assertFalse(InputValidator.isValidPhoneNumber("12345-6789"));
    }

    @Test
    public void testPhoneNumberWithMoreThan10Digits() {
        assertFalse(InputValidator.isValidPhoneNumber("12345678901"));
    }

    @Test
    public void testPhoneNumberWithLessThan10Digits() {
        assertFalse(InputValidator.isValidPhoneNumber("123456789"));
    }

    @Test
    public void testPhoneNumberWithSpaces() {
        assertFalse(InputValidator.isValidPhoneNumber("123 456 7890"));
    }

    @Test
    public void testEmptyPhoneNumber() {
        assertFalse(InputValidator.isValidPhoneNumber(""));
    }

    @Test
    public void testNullPhoneNumber() {
        assertFalse(InputValidator.isValidPhoneNumber(null));
    }

    @Test
    public void testPhoneNumberWithValidFormatButExtraCharacters() {
        assertFalse(InputValidator.isValidPhoneNumber("(123) 456-7890"));
    }

    @Test
    public void testPhoneNumberWithLeadingOrTrailingSpaces() {
        assertFalse(InputValidator.isValidPhoneNumber(" 1234567890 "));
    }
}
