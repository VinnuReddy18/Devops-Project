package com.example.demo.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MathServiceTest {

    private final MathService mathService = new MathService();

    @Test
    public void testIsPrime() {
        assertTrue(mathService.isPrime(7));
        assertFalse(mathService.isPrime(4));
        assertFalse(mathService.isPrime(1));
    }

    @Test
    public void testFactorial() {
        assertEquals(1, mathService.factorial(0));
        assertEquals(1, mathService.factorial(1));
        assertEquals(120, mathService.factorial(5));
    }

    @Test
    public void testFactorialNegative() {
        assertThrows(IllegalArgumentException.class, () -> {
            mathService.factorial(-1);
        });
    }
}
