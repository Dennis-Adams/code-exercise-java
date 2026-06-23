package com.exercise.urlshortener.demo.util;

public class Base62Encoder {
    private Base62Encoder() {
        /* This utility class should not be instantiated */
    }

    // The Base 62 alphabet: 0-9, a-z (lower case), and A-Z (upper case)
    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();

    public static String encode(long number) {
        if (number == 0) {
            return String.valueOf(ALPHABET.charAt(0));
        }

        StringBuilder encodedString = new StringBuilder();

        // Find the largest power of 62 that divides into the given number, then convert
        while (number > 0) {
            int remainder = (int) (number % BASE);
            encodedString.append(ALPHABET.charAt(remainder));
            number /= BASE;
        }

        // The algorithm calculates from lowest place value to highest, so we must reverse the result
        return encodedString.reverse().toString();
    }
}
