package com.example.learningjavafx.Application;

import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.RunnableApplication;
import com.example.learningjavafx.RunnableBuilding;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * The updater methods are called by the Timeframe to refresh the graphic
 * on a particular interval.
 *
 * These include the status of the elevator, the messages and the desired system control.
 */
public class Updater {
    /**
     * Update the state of the elevators for each timeframe:
     * update color of the elevators
     */
    public void updateElevatorsState() {
        // Loop each elevators
        for (int j = 0; j < RunnableBuilding.elevators; j++) {
            // Get the elevator
            ElevatorController elevator = RunnableBuilding.building.elevators.get(j);
            // Get the current floor elevator is at.
            int currentFloor = elevator.getCurrentFloor();
            // Loop each floor of the elevator
            for (int i = 0; i < RunnableBuilding.floors; i++) {
                // Get the square representing the position of the elevator
                Rectangle floorTime = (Rectangle) RunnableApplication.scene.lookup("#e"+j+i);
                // If the elevator has reached the destination floor (currentFloor)
                // then we will show a filled color.
                if (i == currentFloor && !elevator.isLocked()) {
                    floorTime.setFill(Color.rgb(0,0,0));
                } /*else if (elevator.isGroundLock()) {
                    floorTime.setFill(Color.rgb(155,0,0));
                } else if (elevator.isFireLock()) {
                    floorTime.setFill(Color.rgb(255,0,0));
                } */
                // If the elevator is locked, all the floor squares are red
                else if (elevator.isLocked()) {
                    floorTime.setFill(Color.rgb (105,0,0));
                }
                // If not, the default color is white.
                else {
                    floorTime.setFill(Color.rgb(255,255,255));
                }
            }

            // If the elevator has to wait for the user input, show the internal control input
            if (elevator.hasToWaitUserInput()) {
                //System.out.println("Show the user the options for floors keypad");
                // we will have an input field
                // we will show the input field
                // on enter: call internalRequest from the elevator

                TextField internalInput = (TextField) RunnableApplication.scene.lookup("#internal"+j);
                internalInput.setDisable(false);
            }
        }
    }

    /**
     * Show the list of all the items in the queue for status
     */
    public void updateElevatorQueueStatus() {
        // Loop each elevator
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            // Get the state field for the elevator.
            Label label = (Label) RunnableApplication.scene.lookup("#state"+i);
            // Get the current controller to get the queues
            ElevatorController controller = RunnableBuilding.building.elevators.get(i);
            // get all the queues and show them
            label.setText(controller.getCurrentQueue().toString()+"\n"+controller.getUpQueue()+"\n"+controller.getDownQueue());
        }
    }

    /**
     * These are the small tiny tiles near the up button to show which elevator has been
     * called and where is the next destination of it.
     */
    public void updateCalledSigns() {
        // loop each floor
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            // Get the colored tile
            Rectangle calledContainer = (Rectangle) RunnableApplication.scene.lookup("#called"+i);
            // Loop each elevator
            for (int j = 0; j < RunnableBuilding.elevators; j++) {
                // Get the controller of the elevator
                ElevatorController controller = RunnableBuilding.building.elevators.get(j);

                // If the floor is inside the queue, show the color green meaning that the
                // elevator is going there.
                if (controller.getUpQueue().contains(i) || controller.getDownQueue().contains(i)) {
                    calledContainer.setFill(Color.rgb(0, 255, 0));
                    break;
                } else {
                    // Default is white for not called floor.
                    calledContainer.setFill(Color.rgb(255, 255, 255));
                }
            }

        }
    }
}
