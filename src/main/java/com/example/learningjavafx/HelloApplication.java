package com.example.learningjavafx;

import com.example.learningjavafx.Components.Elevator;
import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.Enumerations.ElevatorDirection;
import com.example.learningjavafx.Helpers.Console;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
        // NOTE: call is floor reached before calling move
        // Elevator 1
        this.updateElevatorQueueStatus();
        this.updateElevatorsState();
        this.updateCalledSigns();

        RunnableBuilding.building.scheduler.run();
    }

    private void handleInternalFloorInput(int index) {
        ElevatorController elevator = RunnableBuilding.building.elevators.get(index);
        TextField internalInput = (TextField) scene.lookup("#internal"+index);
        internalInput.textProperty().addListener((observableValue, oldValue, newValue) -> {

            try {
                Integer value = Integer.parseInt(newValue);
                if (value < 0 || value > RunnableBuilding.floors) {
                    System.out.println("INVALID INPUT");
                } else {
                    elevator.internalRequest(value);
                }
            } catch(Exception err) {}
            internalInput.setText("");
            internalInput.setDisable(true);
        });
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

    public void handleClearUpdateSystem() {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                container.setText("");
            }
        });
    }
    public void setUpdateSystem(String message) {
        Label container = (Label) scene.lookup("#systemupdate");
        container.setText(message + (message.length() > 0 ? "  x" : ""));

    }

    public void handleLock() {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Button button = (Button) scene.lookup("#lock"+i);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean locked = RunnableBuilding.building.scheduler.isFireLocked();
                    Button fireButton = (Button) scene.lookup("#fireLock");

                    if (locked) {
                        // we have to unlock it
                        RunnableBuilding.building.scheduler.disableFireLock();
                        fireButton.setText("FIRE LOCK");
                        button.setText("ALARM");
                        setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
                    } else {
                        RunnableBuilding.building.scheduler.enableFireLock();
                        fireButton.setText("FIRE UNLOCK");
                        button.setText("DIS ALARM");
                        setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
                    }
                }
            });
        }
    }



    /**
     * The ground lock blocks all the elevators. The elevators are sent to the ground floor.
     */
    private void handleGroundLock() {
        Button button = (Button) scene.lookup("#groundLock");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (RunnableBuilding.building.scheduler.isGroundLocked()) {
                    button.setText("GROUND LOCK");
                    RunnableBuilding.building.scheduler.disableGroundLock();
                    setUpdateSystem("The elevators has been unlocked.\nAll elevators to the ground floor.");
                } else {
                    button.setText("GROUND UNLOCK");
                    RunnableBuilding.building.scheduler.enableGroundLock();
                    setUpdateSystem("The elevators has been locked.\nAll elevators to the ground floor.");
                }
            }
        });
    }

    /**
     * The ground lock blocks all the elevators. The elevators are sent to the ground floor.
     */
    public void handleFireLock() {
        Button button = (Button) scene.lookup("#fireLock");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (RunnableBuilding.building.scheduler.isFireLocked()) {
                    button.setText("FIRE LOCK");
                    RunnableBuilding.building.scheduler.disableFireLock();
                    setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
                } else {
                    button.setText("FIRE UNLOCK");
                    RunnableBuilding.building.scheduler.enableFireLock();
                    setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
                }
            }
        });
    }

    // ID: firemanexit
    // ID: firemanpassword
    private void handleAccessFiremanPanel() {
        Button firemanEnter = (Button) scene.lookup("#firemanEnter");
        firemanEnter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String passwordValue = ((PasswordField) scene.lookup("#firemanpassword")).getText();
                if (passwordValue.equals("secret")) {
                    Button groundLock = (Button) scene.lookup("#groundLock");
                    Button fireLock = (Button) scene.lookup("#fireLock");
                    Button exit = (Button) scene.lookup("#firemanexit");

                    groundLock.setVisible(true);
                    fireLock.setVisible(true);
                    exit.setVisible(true);
                    firemanEnter.setVisible(false);
                } else {
                    Console.log("APPLICATION", "WRONG PASSWORD");
                    setUpdateSystem("Wrong password.");
                }
            }
        });
    }

    public void handleExitFiremanPanel() {
        Button button = (Button) scene.lookup("#firemanexit");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button groundLock = (Button) scene.lookup("#groundLock");
                Button fireLock = (Button) scene.lookup("#fireLock");
                Button exit = (Button) scene.lookup("#firemanexit");
                Button firemanEnter = (Button) scene.lookup("#firemanEnter");

                groundLock.setVisible(false);
                fireLock.setVisible(false);
                exit.setVisible(false);
                firemanEnter.setVisible(true);

                setUpdateSystem("Exit successful.");
                Console.log("APPLICATION", "FIREMAN EXITED.");
            }
        });
    }



    private void setup() {
        this.update();
        this.handleInternalFloorInput(0);
        this.handleInternalFloorInput(1);
        this.handleInternalFloorInput(2);
        this.handleUpCalls();
        this.handleDownCalls();
        this.handleLock();
        this.handleGroundLock();
        this.handleFireLock();
        this.handleAccessFiremanPanel();
        this.handleExitFiremanPanel();
        this.handleClearUpdateSystem();
    }

    public static void main(String[] args) {
        launch();
    }
}