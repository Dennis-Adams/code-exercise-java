package com.exercise.urlshortener.demo.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {


    @Test
    void shouldEncodeNumberToBase62String() {
        // Red Stage: This test will fail because Base62Encoder doesn't exist yet!
        // According to Base 62 rules, the decimal number 100 should become "1C"
        String encoded = Base62Encoder.encode(100L);
        assertEquals("1C", encoded);
    }
}
