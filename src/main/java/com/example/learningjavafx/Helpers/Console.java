package com.example.learningjavafx.Helpers;

public class Console {
    public static void log(String identifier, String message) {
        System.out.printf("[%-80s %s\n", identifier+"]", message);
    }
}
