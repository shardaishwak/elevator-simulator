package com.example.learningjavafx;

import com.example.learningjavafx.Components.Elevator;
import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.Enumerations.ElevatorDirection;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;

public class HelloApplication extends Application {
    private Scene scene;
    private Integer counter = 0;
    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("elevator.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 320, 540);
        this.scene = scene;
        stage.setTitle("Elevator Simulator!");
        stage.setScene(scene);

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

    private void updateElevatorsState() {
        for (int j = 0; j < RunnableBuilding.elevators; j++) {
            ElevatorController elevator = RunnableBuilding.building.elevators.get(j);
            int currentFloor = elevator.getCurrentFloor();
            for (int i = 0; i < RunnableBuilding.floors; i++) {
                Rectangle floorTime = (Rectangle) scene.lookup("#e"+j+i);
                if (i == currentFloor && !elevator.isLocked()) {
                    floorTime.setFill(Color.rgb(0,0,0));
                } /*else if (elevator.isGroundLock()) {
                    floorTime.setFill(Color.rgb(155,0,0));
                } else if (elevator.isFireLock()) {
                    floorTime.setFill(Color.rgb(255,0,0));
                } */else if (elevator.isLocked()) {
                    floorTime.setFill(Color.rgb (105,0,0));
                }  else {
                    floorTime.setFill(Color.rgb(255,255,255));
                }
            }

            if (elevator.hasToWaitUserInput()) {
                //System.out.println("Show the user the options for floors keypad");
                // we will have an input field
                // we will show the input field
                // on enter: call internalRequest from the elevator
                TextField internalInput = (TextField) scene.lookup("#internal"+j);
                internalInput.setDisable(false);
                internalInput.textProperty().addListener((observableValue, oldValue, newValue) -> {
                    elevator.internalRequest(Integer.parseInt(newValue));
                    internalInput.setText("");
                    internalInput.setDisable(true);
                });


            }
        }
    }

    private void updateElevatorQueueStatus() {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Label label = (Label) scene.lookup("#state"+i);
            ElevatorController controller = RunnableBuilding.building.elevators.get(i);
            label.setText(controller.getCurrentQueue().toString()+"\n"+controller.getUpQueue()+"\n"+controller.getDownQueue());
        }
    }

    /**
     * these are the small square items of 12px to indicate if the floor elevator has been called or not.
     */
    private void updateCalledSigns() {
        ((Rectangle) scene.lookup("#called1")).setFill(Color.rgb(0, 255, 0));
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Rectangle calledContainer = (Rectangle) scene.lookup("#called"+i);
            for (int j = 0; j < RunnableBuilding.elevators; j++) {
                ElevatorController controller = RunnableBuilding.building.elevators.get(j);

                if (controller.getUpQueue().contains(i) || controller.getDownQueue().contains(i)) {
                    calledContainer.setFill(Color.rgb(0, 255, 0));
                    break;
                } else {
                    calledContainer.setFill(Color.rgb(255, 255, 255));
                }
            }

        }
    }
    private void update() {
        // Elevator 1
        this.updateElevatorQueueStatus();
        this.updateElevatorsState();
        this.updateCalledSigns();

        RunnableBuilding.building.scheduler.run();
    }

    private void handleUpCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Button button = (Button) scene.lookup("#callup"+i);
            if (i == RunnableBuilding.floors-1) {
                button.setVisible(false);
                continue;
            }
            int finalI = i;
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    RunnableBuilding.building.scheduler.acceptRequestAndProcess(ElevatorDirection.UP, finalI);
                }
            });
        }
    }

    private void handleDownCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Button button = (Button) scene.lookup("#calldown"+i);
            if (i == 0) {
                button.setVisible(false);
                continue;
            }
            int finalI = i;
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    RunnableBuilding.building.scheduler.acceptRequestAndProcess(ElevatorDirection.UP, finalI);
                }
            });
        }
    }

    public void handleLock() {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Button button = (Button) scene.lookup("#lock"+i);

            int finalI = i;
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    ElevatorController elevatorController = RunnableBuilding.building.elevators.get(finalI);
                    boolean locked = elevatorController.isLocked();
                    if (locked) {
                        // we have to unlock it
                        elevatorController.unlock();
                        button.setText("LOCK");
                    } else {
                        elevatorController.lock();
                        button.setText("UNLOCK");
                    }
                }
            });
        }
    }

    private void handleGroundLock() {
        Button button = (Button) scene.lookup("#groundLock");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (RunnableBuilding.building.scheduler.isGroundLocked()) {
                    button.setText("GROUND LOCK");
                    RunnableBuilding.building.scheduler.disableGroundLock();
                } else {
                    button.setText("GROUND UNLOCK");
                    RunnableBuilding.building.scheduler.enableGroundLock();
                }
            }
        });
    }



    private void setup() {
        this.update();
        this.handleUpCalls();
        this.handleDownCalls();
        this.handleLock();
        this.handleGroundLock();
    }

    public static void main(String[] args) {
        launch();
    }
}