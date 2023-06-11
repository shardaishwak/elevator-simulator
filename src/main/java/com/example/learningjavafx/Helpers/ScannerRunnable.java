package com.example.learningjavafx.Helpers;

import com.example.learningjavafx.Elevator.ElevatorController;

import java.util.Scanner;

/**
 * Joint with the Elevator Runner, the class uses multi-thread capabilities
 * we can enter the input for the request as the state update the elevators.
 */
public class ScannerRunnable implements Runnable {
    private Scanner scanner;
    private ElevatorController controller;
    private ScannerRunnable() {}

    public ScannerRunnable(Scanner scanner, ElevatorController controller) {
        this.scanner = scanner;
        this.controller = controller;
    }

    @Override
    public void run() {
        int floor = 0;

        while(floor != -1) {

            floor = scanner.nextInt();
            this.controller.addRequest(floor);
        }
    }
}