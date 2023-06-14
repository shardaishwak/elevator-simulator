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
 * <p>
 * These include the status of the elevator, the messages and the desired system control.
 */
public class Updater {
    /**
     * Update the state of the elevators for each timeframe:
     * update color of the elevators
     * <p>
     * We need to loop each elevator to get the status. For each floor
     * we check if the elevator is at that elevator. if the elevator is at that
     * elevator, we will change the color of the elevator state BOX (central ones)
     * to black. The default color is white.
     * <p>
     * If the elevator is locked, the colors will be changed to red.
     * <p>
     * On each change, we need to check if we should move the elevator or not.
     * An elevator must not move if there is an internal request to process.
     * In this case, we wait until the user inputs the internal floor value and then move the elevator
     * in the next direction.
     */
    public void updateElevatorsState() {
        for (int j = 0; j < RunnableBuilding.elevators; j++) {
            ElevatorController elevator = RunnableBuilding.building.elevators.get(j);
            int currentFloor = elevator.getCurrentFloor();

            for (int i = 0; i < RunnableBuilding.floors; i++) {
                Rectangle floorTime = (Rectangle) RunnableApplication.scene.lookup("#e"+j+i);
                if (i == currentFloor && !elevator.isLocked()) {
                    floorTime.setFill(Color.rgb(0,0,0));
                } else if (elevator.isLocked()) {
                    floorTime.setFill(Color.rgb (105,0,0));
                } else {
                    floorTime.setFill(Color.rgb(255,255,255));
                }
            }
            if (elevator.hasToWaitUserInput()) {
                TextField internalInput = (TextField) RunnableApplication.scene.lookup("#internal"+j);
                internalInput.setDisable(false);
            }
        }
    }

    /**
     * Show the list of all the items in the queue for status
     * To do this, we need to map each elevator and retrieve the up, down and current
     * queues. These dequeues are displayed in the state[index] label.
     */
    public void updateElevatorQueueStatus() {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Label label = (Label) RunnableApplication.scene.lookup("#state"+i);
            ElevatorController controller = RunnableBuilding.building.elevators.get(i);
            label.setText(
                    controller.getCurrentQueue().toString()+
                    "\n"+controller.getUpQueue()+
                    "\n"+controller.getDownQueue()
            );
        }
    }

    /**
     * These are the small tiny tiles near the up button to show which elevator has been
     * called and where is the next destination of it.
     * <p>
     * To do this, we need to loop each floor. On each floor, we loop the elevators
     * If any of the elevator will reach a particular floor i, we change the colors of the
     * indicator with id called[index] to green. WE must break from the loop because if any
     * of the other elevators is not at that floor, the state will be overridden.
     *
     */
    public void updateCalledSigns() {
        for (int i = 0; i < RunnableBuilding.floors; i++) {
            Rectangle calledContainer = (Rectangle) RunnableApplication.scene.lookup("#called"+i);
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
}
