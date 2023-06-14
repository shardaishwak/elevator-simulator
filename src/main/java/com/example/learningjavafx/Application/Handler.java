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
     * <p>
     * HOW TO
     * Firs twe get the target elevator and the input field of that elevator.
     * We need to listen to the change in the input so that whenever the user
     * enters an input, we process it instantly.
     * Note that we must ensure that the values entered by the user is valid
     * This can be done by catching the error. We must endure that the entered value
     * is between the range [0, max_floors]. After entering and processing the input, we
     * must block the input fields.
     * <p>
     * Note that the input fields are open only when there is some request to process.
     */
    public void handleInternalFloorInput(int index) {
        ElevatorController elevator = RunnableBuilding.building.elevators.get(index);
        TextField internalInput = (TextField) RunnableApplication.scene.lookup("#internal"+index);
        internalInput.textProperty().addListener((observableValue, oldValue, newValue) -> {

            try {
                int value = Integer.parseInt(newValue);
                if (value < 0 || value >= RunnableBuilding.floors) {
                    Console.log("HANDLER", "INVALID INTERNAL FLOOR SELECTION");
                    RunnableApplication.setUpdateSystem("Invalid floor value");
                } else {
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
     * <p>
     * We need to give an action method to each button to trigger the request into
     * the scheduler. This can be done by looping all the buttons and adding a
     * setOnAction lambda method. The method will add the floor to the request.
     * <p>
     * We do not need to ensure if the floor is already to process as the Scheduler
     * class contains the logic for it,
     */
    public void handleUpCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Button button = (Button) RunnableApplication.scene.lookup("#callup"+i);
            if (i == RunnableBuilding.floors-1) {
                button.setVisible(false);
                continue;
            }
            int finalI = i;
            button.setOnAction(event -> RunnableBuilding.building.scheduler.acceptRequestAndProcess(ElevatorDirection.UP, finalI));
        }
    }
    /**
     * Add the event listener to all the down button for the elevator.
     * We can target each button without duplicates using their index.
     * <p>
     * We need to give an action method to each button to trigger the request into
     *      * the scheduler. This can be done by looping all the buttons and adding a
     *      * setOnAction lambda method. The method will add the floor to the request.
     *      *
     *      * We do not need to ensure if the floor is already to process as the Scheduler
     *      * class contains the logic for it,
     */
    public void handleDownCalls() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Button button = (Button) RunnableApplication.scene.lookup("#calldown"+i);
            if (i == 0) {
                button.setVisible(false);
                continue;
            }
            int finalI = i;
            button.setOnAction(event -> RunnableBuilding.building.scheduler.acceptRequestAndProcess(ElevatorDirection.UP, finalI));
        }
    }

    /**
     * Add event listeners to when the user clicks on one of the lock button
     * of one of the elevators.
     * <p>
     * If one of the elevator is locked, all the elevators are automatically locked
     * To do so, we need to add the lock listener click to all the lock buttons
     * for each elevator.
     * <p>
     * Then, we need to trigger the forelock method from the scheduler class.
     * The method will trigger the lock in each elevator if any of the elevators clicks
     * on the alarm button
     * <p>
     * All the buttons will get locked.
     */
    public void handleLock() {
        // Loop for each lock in the elevator
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Button button = (Button) RunnableApplication.scene.lookup("#lock"+i);
            button.setOnAction(event -> {
                boolean locked = RunnableBuilding.building.scheduler.isFireLocked();
                Button fireButton = (Button) RunnableApplication.scene.lookup("#fireLock");

                if (locked) {
                    RunnableBuilding.building.scheduler.disableFireLock();
                    fireButton.setText("FIRE LOCK");
                    updateAlarmButtonsStatus("ALARM", false);
                    RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
                } else {
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
     * <p>
     *  If one of the elevator is locked, all the elevators are automatically locked
     *      * To do so, we need to add the lock listener click to all the lock buttons
     *      * for each elevator.
     *      *
     *      * Then, we need to trigger the ground lock method from the scheduler class.
     *      * The method will trigger the lock in each elevator if any of the elevators clicks
     *      * on the alarm button
     *      *
     *      * All the buttons will get locked.
     */
    public void handleGroundLock() {
        Button button = (Button) RunnableApplication.scene.lookup("#groundLock");
        button.setOnAction(actionEvent -> {
            if (RunnableBuilding.building.scheduler.isGroundLocked()) {
                button.setText("GROUND LOCK");
                RunnableBuilding.building.scheduler.disableGroundLock();
                updateAlarmButtonsStatus("ALARM", false);
                RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the ground floor.");
            } else {
                button.setText("GROUND UNLOCK");
                RunnableBuilding.building.scheduler.enableGroundLock();
                updateAlarmButtonsStatus("LOCKED", true);
                RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the ground floor.");
            }
        });
    }

    /**
     * The ground lock blocks all the elevators. The elevators are sent to the first floor.
     * <p>
     * The lock will trigger the lock as in any other field.
     */
    public void handleFireLock() {
        Button button = (Button) RunnableApplication.scene.lookup("#fireLock");
        button.setOnAction(actionEvent -> {
            if (RunnableBuilding.building.scheduler.isFireLocked()) {
                button.setText("FIRE LOCK");
                RunnableBuilding.building.scheduler.disableFireLock();
                updateAlarmButtonsStatus("ALARM", false);
                RunnableApplication.setUpdateSystem("The elevators has been unlocked.\nAll elevators to the last floor.");
            } else {
                button.setText("FIRE UNLOCK");
                RunnableBuilding.building.scheduler.enableFireLock();
                updateAlarmButtonsStatus("LOCKED", true);
                RunnableApplication.setUpdateSystem("The elevators has been locked.\nAll elevators to the last floor.");
            }
        });
    }


    /**
     * Change the status of all alarm buttons
     * <p>
     * This will lock all the elevator status based on the value of the provider.
     */
    public void updateAlarmButtonsStatus(String status, boolean disabled) {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Button alarm = (Button) RunnableApplication.scene.lookup("#lock"+i);
            alarm.setText(status);
            alarm.setDisable(disabled);
        }
    }

    /**
     * Handle the password input entering of the fireman and unlock all the options for the fireman
     * *   can unlock the fire lock after triggered by an alarm
     *  and can send all the elevators to the ground lock.
     * <p>
     *  When accessing the fireman panel, we need to add the password input value to confirm
     *  that the value entered is correct.
     * <p>
     *  If the value is correct, the ground lock and forelock buttons will be shown.
     *  If not, we will show an error message
     */
    public void handleAccessFiremanPanel() {
        Button firemanEnter = (Button) RunnableApplication.scene.lookup("#firemanEnter");
        firemanEnter.setOnAction(actionEvent -> {
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
        });
    }

    /**
     * Handle the logout of the fireman from the admin panel
     * <p>
     * The button will trigger all the fireman options to hide
     */
    public void handleExitFiremanPanel() {
        Button button = (Button) RunnableApplication.scene.lookup("#firemanexit");
        button.setOnAction(actionEvent -> {
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
