package com.example.learningjavafx.Application;

import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.Enumerations.ElevatorDirection;
import com.example.learningjavafx.RunnableApplication;
import com.example.learningjavafx.Helpers.Console;
import com.example.learningjavafx.RunnableBuilding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class Handler {
    public void handleInternalFloorInput(int index) {
        ElevatorController elevator = RunnableBuilding.building.elevators.get(index);
        TextField internalInput = (TextField) RunnableApplication.scene.lookup("#internal"+index);
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

    public void handleUpCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Button button = (Button) RunnableApplication.scene.lookup("#callup"+i);
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

    public void handleDownCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Button button = (Button) RunnableApplication.scene.lookup("#calldown"+i);
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
            Button button = (Button) RunnableApplication.scene.lookup("#lock"+i);
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    boolean locked = RunnableBuilding.building.scheduler.isFireLocked();
                    Button fireButton = (Button) RunnableApplication.scene.lookup("#fireLock");

                    if (locked) {
                        // we have to unlock it
                        RunnableBuilding.building.scheduler.disableFireLock();
                        fireButton.setText("FIRE LOCK");
                        button.setText("ALARM");
                        RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
                    } else {
                        RunnableBuilding.building.scheduler.enableFireLock();
                        fireButton.setText("FIRE UNLOCK");
                        button.setText("DIS ALARM");
                        RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
                    }
                }
            });
        }
    }

    /**
     * The ground lock blocks all the elevators. The elevators are sent to the ground floor.
     */
    public void handleGroundLock() {
        Button button = (Button) RunnableApplication.scene.lookup("#groundLock");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (RunnableBuilding.building.scheduler.isGroundLocked()) {
                    button.setText("GROUND LOCK");
                    RunnableBuilding.building.scheduler.disableGroundLock();
                    RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the ground floor.");
                } else {
                    button.setText("GROUND UNLOCK");
                    RunnableBuilding.building.scheduler.enableGroundLock();
                    RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the ground floor.");
                }
            }
        });
    }

    /**
     * The ground lock blocks all the elevators. The elevators are sent to the ground floor.
     */
    public void handleFireLock() {
        Button button = (Button) RunnableApplication.scene.lookup("#fireLock");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (RunnableBuilding.building.scheduler.isFireLocked()) {
                    button.setText("FIRE LOCK");
                    RunnableBuilding.building.scheduler.disableFireLock();
                    RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
                } else {
                    button.setText("FIRE UNLOCK");
                    RunnableBuilding.building.scheduler.enableFireLock();
                    RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
                }
            }
        });
    }

    // ID: firemanexit
    // ID: firemanpassword
    public void handleAccessFiremanPanel() {
        Button firemanEnter = (Button) RunnableApplication.scene.lookup("#firemanEnter");
        firemanEnter.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String passwordValue = ((PasswordField) RunnableApplication.scene.lookup("#firemanpassword")).getText();
                if (passwordValue.equals("secret")) {
                    Button groundLock = (Button) RunnableApplication.scene.lookup("#groundLock");
                    Button fireLock = (Button) RunnableApplication.scene.lookup("#fireLock");
                    Button exit = (Button) RunnableApplication.scene.lookup("#firemanexit");

                    groundLock.setVisible(true);
                    fireLock.setVisible(true);
                    exit.setVisible(true);
                    firemanEnter.setVisible(false);
                    Console.log("APPLICATION", "LOGIN SUCCESS");
                    RunnableApplication.setUpdateSystem("Login success.");
                } else {
                    Console.log("APPLICATION", "WRONG PASSWORD");
                    RunnableApplication.setUpdateSystem("Wrong password.");
                }
            }
        });
    }

    public void handleExitFiremanPanel() {
        Button button = (Button) RunnableApplication.scene.lookup("#firemanexit");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button groundLock = (Button) RunnableApplication.scene.lookup("#groundLock");
                Button fireLock = (Button) RunnableApplication.scene.lookup("#fireLock");
                Button exit = (Button) RunnableApplication.scene.lookup("#firemanexit");
                Button firemanEnter = (Button) RunnableApplication.scene.lookup("#firemanEnter");

                groundLock.setVisible(false);
                fireLock.setVisible(false);
                exit.setVisible(false);
                firemanEnter.setVisible(true);

                RunnableApplication.setUpdateSystem("Exit successful.");
                Console.log("APPLICATION", "FIREMAN EXITED.");
            }
        });
    }
}
