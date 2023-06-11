package com.example.learningjavafx.Application;

import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.Enumerations.ElevatorDirection;
import com.example.learningjavafx.Helpers.Console;
import com.example.learningjavafx.RunnableApplication;
import com.example.learningjavafx.RunnableBuilding;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Handling all the component and their actions
 * We are not using the controller Class because
 * the elements are targeted dynamically:
 * my this I mean, in RunnableController would require
 * to instantiate each element and then target them.
 * But with the Handler, we can call the RunnableApplication
 * as static and from there fetch the element using their ID
 * For instance, the buttons have the index in front of them
 * and using a for loop, we can target each button and add the functionality
 * by keeping the code concise and elegant.
 */
public class Handler {
    /**
     * Handle the internal input from the user
     * When the elevator reached the user request floor
     * the elevator will remain stop until the user has not entered the
     * destination floor. After adding the floor, the elevator will continue in the direction.
     */
    public void handleInternalFloorInput(int index) {
        // Getting the target elevator
        ElevatorController elevator = RunnableBuilding.building.elevators.get(index);
        // Getting the internal control input field
        TextField internalInput = (TextField) RunnableApplication.scene.lookup("#internal"+index);
        // Adding the listener to the text
        internalInput.textProperty().addListener((observableValue, oldValue, newValue) -> {

            // Will catch any exception that happens
            try {
                // Try parsing the string to integer
                int value = Integer.parseInt(newValue);
                // Check that the value of the input is valid
                if (value < 0 || value >= RunnableBuilding.floors) {
                    // Send an answer for invalid input
                    Console.log("HANDLER", "INVALID INTERNAL FLOOR SELECTION");
                    RunnableApplication.setUpdateSystem("Invalid floor value");
                } else {
                    // Add the request to the elevator as a queue.
                    elevator.internalRequest(value);
                }
            } catch(Exception ignored) {}
            internalInput.setText("");
            internalInput.setDisable(true);
        });
    }

    /**
     * Add the event listener to all the up button for the elevator.
     * We can target each button without duplicates using their index.
     */
    public void handleUpCalls() {
        // Looping each floor
        // Each button has index type: callup[index] where index is the index of the floor, so i.
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            // Getting the button
            Button button = (Button) RunnableApplication.scene.lookup("#callup"+i);
            // If the up button is the last floor, do not show it because the user cannot go up.
            if (i == RunnableBuilding.floors-1) {
                button.setVisible(false);
                continue;
            }
            int finalI = i;
            // Add button click listener
            button.setOnAction(event -> {
                // When clicked, add the request to the queue.
                RunnableBuilding.building.scheduler.acceptRequestAndProcess(ElevatorDirection.UP, finalI);
            });
        }
    }
    /**
     * Add the event listener to all the down button for the elevator.
     * We can target each button without duplicates using their index.
     */
    public void handleDownCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            // Looping each floor
            // Each button has index type: call down[index] where index is the index of the floor, so i.
            Button button = (Button) RunnableApplication.scene.lookup("#calldown"+i);
            // If the button is on the first floor, do not show it because cannot go down further.
            if (i == 0) {
                button.setVisible(false);
                continue;
            }
            int finalI = i;
            // Add the click event listener.
            button.setOnAction(event -> {
                // Add the request to the queue.
                RunnableBuilding.building.scheduler.acceptRequestAndProcess(ElevatorDirection.UP, finalI);
            });
        }
    }

    /**
     * Add event listeners to when the user clicks on one of the lock button
     * of one of the elevators.
     */
    public void handleLock() {
        // Loop for each lock in the elevator
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            // Target the elevator button
            Button button = (Button) RunnableApplication.scene.lookup("#lock"+i);
            // Add click event listener to the button
            button.setOnAction(event -> {
                // Get status of the elevator: locked or not
                boolean locked = RunnableBuilding.building.scheduler.isFireLocked();
                // Get the forelock button
                Button fireButton = (Button) RunnableApplication.scene.lookup("#fireLock");

                // If the elevator was locked, we will unlock the elevator
                // We will also disable the fire alarm and the fireman can intervene here.
                if (locked) {
                    // we have to unlock it
                    RunnableBuilding.building.scheduler.disableFireLock();
                    fireButton.setText("FIRE LOCK");
                    updateAlarmButtonsStatus("ALARM", false);
                    RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
                } else {
                    // We will enable the fire lock
                    // All elevators are stopped
                    // Only the fireman from its panel can unlock the system.
                    RunnableBuilding.building.scheduler.enableFireLock();
                    fireButton.setText("FIRE UNLOCK");
                    updateAlarmButtonsStatus("DIS ALARM", false);
                    RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
                }
            });
        }
    }

    /**
     * The ground lock blocks all the elevators. The elevators are sent to the first floor.
     */
    public void handleGroundLock() {
        // Target the button
        Button button = (Button) RunnableApplication.scene.lookup("#groundLock");
        // Add click event listener
        button.setOnAction(actionEvent -> {
            // If the elevator is locked.
            if (RunnableBuilding.building.scheduler.isGroundLocked()) {
                // Disable the ground lock and the elevators are on the first floor.
                button.setText("GROUND LOCK");
                RunnableBuilding.building.scheduler.disableGroundLock();
                updateAlarmButtonsStatus("ALARM", false);
                RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the ground floor.");
            } else {
                // Enable the ground lock: elevators sent to the first floor.
                button.setText("GROUND UNLOCK");
                RunnableBuilding.building.scheduler.enableGroundLock();
                updateAlarmButtonsStatus("LOCKED", true);
                RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the ground floor.");
            }
        });
    }

    /**
     * The ground lock blocks all the elevators. The elevators are sent to the first floor.
     */
    public void handleFireLock() {
        Button button = (Button) RunnableApplication.scene.lookup("#fireLock");
        // Add click event listener
        button.setOnAction(actionEvent -> {
            // If already locked, unlock
            if (RunnableBuilding.building.scheduler.isFireLocked()) {
                button.setText("FIRE LOCK");
                RunnableBuilding.building.scheduler.disableFireLock();
                updateAlarmButtonsStatus("ALARM", false);
                RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
            } else {
                // Add the fire lock: elevator are sent to the last floor for exit.
                button.setText("FIRE UNLOCK");
                RunnableBuilding.building.scheduler.enableFireLock();
                updateAlarmButtonsStatus("LOCKED", true);
                RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
            }
        });
    }


    /**
     * Change the status of all alarm buttons
     */
    public void updateAlarmButtonsStatus(String status, boolean disabled) {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Button alarm = (Button) RunnableApplication.scene.lookup("#lock"+i);
            alarm.setText(status);
            alarm.setDisable(disabled);
        }
    }
    // ID: fireman-exit
    // ID: fireman password

    /**
     * Handle the password input entering of the fireman and unlock all the options for the fireman     *   can unlock the fire lock after triggered by an alarm
     *  and can send all the elevators to the ground lock.
     */
    public void handleAccessFiremanPanel() {
        // Getting the fireman Submit password button
        Button firemanEnter = (Button) RunnableApplication.scene.lookup("#firemanEnter");
        // Add the click event
        firemanEnter.setOnAction(actionEvent -> {
            // Get the password field
            String passwordValue = ((PasswordField) RunnableApplication.scene.lookup("#firemanpassword")).getText();
            // check if the password is valid: secret is the password
            if (passwordValue.equals("secret")) {
                // Get the ground, fire and exit button and enable them by making them visible.
                Button groundLock = (Button) RunnableApplication.scene.lookup("#groundLock");
                Button fireLock = (Button) RunnableApplication.scene.lookup("#fireLock");
                Button exit = (Button) RunnableApplication.scene.lookup("#firemanexit");

                groundLock.setVisible(true);
                fireLock.setVisible(true);
                exit.setVisible(true);
                // Do not show the enter button
                firemanEnter.setVisible(false);
                Console.log("APPLICATION", "LOGIN SUCCESS");
                RunnableApplication.setUpdateSystem("Login success.");
            } else {
                // Show that the password was wrong.
                Console.log("APPLICATION", "WRONG PASSWORD");
                RunnableApplication.setUpdateSystem("Wrong password.");
            }
        });
    }

    /**
     * Handle the logout of the fireman from the admin panel
     */
    public void handleExitFiremanPanel() {
        // Target the exit button
        Button button = (Button) RunnableApplication.scene.lookup("#firemanexit");
        // Add the click listener to the targeted button
        button.setOnAction(actionEvent -> {
            // Hide the ground, fire and exit button and show the firemanEnter and password field
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
        });
    }
}
