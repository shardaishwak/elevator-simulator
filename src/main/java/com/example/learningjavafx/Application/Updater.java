package com.example.learningjavafx.Application;

import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.RunnableApplication;
import com.example.learningjavafx.RunnableBuilding;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Updater {
    public void updateElevatorsState() {
        for (int j = 0; j < RunnableBuilding.elevators; j++) {
            ElevatorController elevator = RunnableBuilding.building.elevators.get(j);
            int currentFloor = elevator.getCurrentFloor();
            for (int i = 0; i < RunnableBuilding.floors; i++) {
                Rectangle floorTime = (Rectangle) RunnableApplication.scene.lookup("#e"+j+i);
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

                TextField internalInput = (TextField) RunnableApplication.scene.lookup("#internal"+j);
                internalInput.setDisable(false);
            }
        }
    }

    public void updateElevatorQueueStatus() {
        for (int i = 0; i < RunnableBuilding.elevators; i++) {
            Label label = (Label) RunnableApplication.scene.lookup("#state"+i);
            ElevatorController controller = RunnableBuilding.building.elevators.get(i);
            label.setText(controller.getCurrentQueue().toString()+"\n"+controller.getUpQueue()+"\n"+controller.getDownQueue());
        }
    }

    /**
     * these are the small square items of 12px to indicate if the floor elevator has been called or not.
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
