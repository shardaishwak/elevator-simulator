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

public class RunnableApplication extends javafx.application.Application {
    public static Scene scene;
    private Handler handler;
    private Updater updater;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(RunnableApplication.class.getResource("elevator.fxml"));
        Scene internalScene = new Scene(fxmlLoader.load(), 320, 540);

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

    public static void handleClearUpdateSystem() {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                container.setText("");
            }
        });
    }
    public static void setUpdateSystem(String message) {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setText(message + (message.length() > 0 ? "  x" : ""));
    }

    private void update() {
        // NOTE: call is floor reached before calling move
        // Elevator 1
        this.updater.updateElevatorQueueStatus();
        this.updater.updateElevatorsState();
        this.updater.updateCalledSigns();

        RunnableBuilding.building.scheduler.run();
    }

    private void sendElevatorToOptimalPosition() {
        RunnableBuilding.building.scheduler.sendElevatorsToOptimalPosition();
    }

    private void setup() {
        // All the features in the elevator.
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

        // Initial position of the elevators
        RunnableBuilding.building.scheduler.sendElevatorsToOptimalPosition();
        setUpdateSystem("Sending elevators to optimal position");

    }

    public static void main(String[] args) {
        launch();
    }
}