package com.example.learningjavafx;

import com.example.learningjavafx.Application.Handler;
import com.example.learningjavafx.Application.Updater;
import com.example.learningjavafx.Helpers.PrintStack;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Main entrypoing for JAVAFX: initializing the components and rendering the elements
 * using a timeframe of 1 second and loop infinitely.
 */
public class RunnableApplication extends javafx.application.Application {
    /**
     * After loading the scene, it will be accessible by each component in the system
     * Specifically: Handler and Updater
     */
    public static Scene scene;
    /**
     * Creating an instance of the HAndler Object: handle all the button clicks, values and inputs
     */
    private Handler handler;
    /**
     * Creating an instanece of the Updater Object: update the state of the elevators and movement
     */
    private Updater updater;

    /**
     * Entry point for initial setup
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Get the FXML file to render inside the scene.
        FXMLLoader fxmlLoader = new FXMLLoader(RunnableApplication.class.getResource("elevator.fxml"));
        // Setup the internal frame
        Scene internalScene = new Scene(fxmlLoader.load(), 650, 600);

        // Adding the value to the scene
        scene = internalScene;
        stage.setTitle("Elevator Simulator!");
        stage.setScene(internalScene);

        // Creating an instance of the Handler class
        this.handler = new Handler();
        // Creating an instance of the Updater class
        this.updater = new Updater();

        // Showing each component
        stage.show();
        // Rendering initial setup: handlers and initial update the elevator
        this.setup();

        // Create a new instance of the Timeline animation
        // The timeline will animate each second and will update the
        // view by calling the update method.
        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent actionEvent) {
                                update();
                            }
                        }
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Clear all the logs to the panel
     */
    public static void handleClearUpdateSystem() {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            // if the user clicks on the message, clear the message.
            @Override
            public void handle(MouseEvent mouseEvent) {
                container.setText("");
            }
        });
    }

    /**
     * Add a log to the panel: this is the bottom messages that we can see in the graphics.
     * @param message
     */
    public static void setUpdateSystem(String message) {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setText(message + (message.length() > 0 ? "  x" : ""));
    }

    /**
     * the method will be called inside the timeframe to update the view
     */
    private void update() {
        // NOTE: call is floor reached before calling move
        // Elevator 1
        // Updat the queue status
        this.updater.updateElevatorQueueStatus();
        // Update the elevator color floor status
        this.updater.updateElevatorsState();
        // Update the called tiles color.
        this.updater.updateCalledSigns();

        // Make the elevator move to the next destination.
        RunnableBuilding.building.scheduler.run();
    }

    /**
     * Initial setup before rendering everything
     */
    private void setup() {
        // All the features in the elevator.
        // Add the internal floor input events for each elevator
        this.handler.handleInternalFloorInput(0);
        this.handler.handleInternalFloorInput(1);
        this.handler.handleInternalFloorInput(2);
        // Add the down button events
        this.handler.handleDownCalls();
        // Add the up calls event
        this.handler.handleUpCalls();
        // Add the lock triggering events
        this.handler.handleLock();
        this.handler.handleGroundLock();
        this.handler.handleFireLock();
        // Handle the access to the panel
        this.handler.handleAccessFiremanPanel();
        // Handle the logout from the panel
        this.handler.handleExitFiremanPanel();

        // Set the elevator to the initial update
        this.update();

        // Clear all the initial logs from the system.
        handleClearUpdateSystem();

        // Initial position of the elevators
        RunnableBuilding.building.scheduler.sendElevatorsToOptimalPosition();
        setUpdateSystem("Sending elevators to optimal position");

    }

    public static void main(String[] args) {
        launch();
    }
}