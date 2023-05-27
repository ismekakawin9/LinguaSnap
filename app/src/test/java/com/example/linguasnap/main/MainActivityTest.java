package com.example.linguasnap.main;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class MainActivityTest {

    private MainActivity mainActivity;

    @Before
    public void setUp() throws Exception {
        mainActivity = new MainActivity();
    }

    /**
     * Should return correct word count for a given string
     */
    @Test
    public void wordCountForGivenString() {
        String testString = "This is a test string";
        int expectedWordCount = 5;

        int actualWordCount = mainActivity.wordCount(testString);

        assertEquals(expectedWordCount, actualWordCount);
    }

    /**
     * Should return correct word count for a string with multiple spaces
     */
    @Test
    public void wordCountForStringWithMultipleSpaces() {
        String testString = "This is a test string with multiple spaces";
        int expectedWordCount = 7;

        int actualWordCount = mainActivity.wordCount(testString);

        assertEquals(expectedWordCount, actualWordCount);
    }
}