package com.example.learningjavafx.Elevator;

import com.example.learningjavafx.Helpers.ScannerRunnable;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ElevatorRunner {
    /**
     * It is efficient to declare the data fields that do no expect to be changed as final in the Runnable Inheritance
     * An object reference that is declared as final ensures that the object it refers to will be fully constructed
     * and initialized before the usage in the system.
     */
    private final ElevatorController controller;

    public ElevatorRunner(ElevatorController controller) {
        this.controller = controller;
    }

    public void run(Scanner scanner) {
        // Create a runnable thread for move
        Runnable elevatorRunner = new Runnable() {
            @Override
            public void run() {
                controller.move();
            }
        };

        ScannerRunnable runner = new ScannerRunnable(scanner, controller);


        // Generating a new multi-thread
        // Creating a total of two parallel threads

        // Thread 1: Listen for user Input
        // Thread 2: Process the movement
        // Thread 1
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(runner, 0, 1, TimeUnit.SECONDS);
        // Thread 2
        ScheduledExecutorService executor2 = Executors.newScheduledThreadPool(1);
        executor2.scheduleAtFixedRate(elevatorRunner, 0, 4, TimeUnit.SECONDS);
    }
}
