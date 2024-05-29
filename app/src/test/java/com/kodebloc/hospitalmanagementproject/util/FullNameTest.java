package com.kodebloc.hospitalmanagementproject.util;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FullNameTest {
    @Test
    public void testValidName() {
        assertTrue(InputValidator.isValidName("John"));
    }

    @Test
    public void testNameWithSpaceInBetween() {
        assertTrue(InputValidator.isValidName("John Doe"));
    }

    @Test
    public void testNameWithNumbers() {
        assertFalse(InputValidator.isValidName("John123"));
    }

    @Test
    public void testNameWithSpecialCharacters() {
        assertFalse(InputValidator.isValidName("John@Doe"));
    }

    @Test
    public void testNameWithLessThan3Characters() {
        assertFalse(InputValidator.isValidName("Jo"));
    }

    @Test
    public void testEmptyName() {
        assertFalse(InputValidator.isValidName(""));
    }

    @Test
    public void testNullName() {
        assertFalse(InputValidator.isValidName(null));
    }

    @Test
    public void testValidNameWithMixedCase() {
        assertTrue(InputValidator.isValidName("JoHn"));
    }
}
