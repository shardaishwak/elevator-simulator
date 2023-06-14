package com.example.learningjavafx;

import com.example.learningjavafx.Application.Handler;
import com.example.learningjavafx.Application.Updater;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Main entrypoint for JAVAFX: initializing the components and rendering the elements
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
     * Creating an instance of the Updater Object: update the state of the elevators and movement
     */
    private Updater updater;

    /**
     * Entry point for initial setup
     *
     * We need to load the fxml file and set up the screen with intial width and height
     * We need to initialize the initial setup of the handlers and updates and show the scene
     * Afterwards, we run the initial elevator setup with default configurations
     *
     * In the start method, we need to initialize a timeframe for each second to render the state
     * of the system.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RunnableApplication.class.getResource("elevator.fxml"));
        Scene internalScene = new Scene(fxmlLoader.load(), 650, 600);

        scene = internalScene;
        stage.setTitle("Elevator Simulator!");
        stage.setScene(internalScene);

        this.handler = new Handler();
        this.updater = new Updater();

        stage.show();
        this.setup();

        final Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.seconds(1),
                        actionEvent -> update()
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
        container.setOnMouseClicked(mouseEvent -> container.setText(""));
    }

    /**
     * Add a log to the panel: this is the bottom messages that we can see in the graphics.
     */
    public static void setUpdateSystem(String message) {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setText(message + (message.length() > 0 ? "  x" : ""));
    }

    /**
     * the method will be called inside the timeframe to update the view
     *
     * The update will update the queue state, the elevator position
     * and the call signals
     */
    private void update() {
        this.updater.updateElevatorQueueStatus();
        this.updater.updateElevatorsState();
        this.updater.updateCalledSigns();

        RunnableBuilding.building.scheduler.run();
    }

    /**
     * Initial setup before rendering everything
     *
     * The method simply calls the methods from the handler.
     */
    private void setup() {
        this.handler.handleInternalFloorInput(0);
        this.handler.handleInternalFloorInput(1);
        this.handler.handleInternalFloorInput(2);
        this.handler.handleDownCalls();
        this.handler.handleUpCalls();
        this.handler.handleLock();
        this.handler.handleGroundLock();
        this.handler.handleFireLock();
        this.handler.handleAccessFiremanPanel();
        this.handler.handleExitFiremanPanel();

        this.update();
        handleClearUpdateSystem();
        RunnableBuilding.building.scheduler.sendElevatorsToOptimalPosition();
        setUpdateSystem("Sending elevators to optimal position");
    }

    public static void main(String[] args) {
        launch();
    }
}