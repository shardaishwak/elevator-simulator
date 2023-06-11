package com.example.learningjavafx.Helpers;

/**
 * Simple calss for printing on the console using a particular format
 */
public class Console {
    public static void log(String identifier, String message) {
        System.out.printf("[%-80s %s\n", identifier+"]", message);
    }
}
