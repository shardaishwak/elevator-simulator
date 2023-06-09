package com.example.learningjavafx;

import com.example.learningjavafx.Enumerations.ElevatorDirection;
import com.example.learningjavafx.Elevator.ElevatorController;
import com.example.learningjavafx.Helpers.Console;

import java.util.ArrayList;


/**
 * The scheduler will determine the most efficient elevator for the passenger
 * <p>
 * How to determine (checkpoint):
 * <p>
 * 1. User request elevator to go to up (each case should log sequentially):
 * -> There is an elevator below the request floor that is going up: take the request
 * -> Is there any IDLE elevator: take that
 * -> There is no elevator below going up: take the first elevator that is below with direction down. After a while, it will move up.
 * -> All elevators are up:
 *  -> Take the one that is going down
 *  -> if all going up, take the one that is not going down: this way, after serving all calls, the elevator will go to take the user
 *
 */

/**
 * Difference elevator-request positions cases.
 * To find the most optimal elevator, we need to consider all the positions that the elevator and request
 * might be. In total there are 4 combinations of the direction between the elevator and passenger, considering the
 * options to be UP and DOWN.
 * <p>
 * ELEVATOR     |         REQUEST
 *    UP        |           UP
 *    UP        |          DOWN *          |           UP
 *   DOWN       |          DOWN
 * <p>
 *  Now, for each of the single combinations, the elevator can be at the top or at the bottom of the request, so the updated
 *  table would look like this
 * <p>
 *  BELOW: the elevator position is below the requested floor
 * <p>
 * ELEVATOR     |         REQUEST       |       RELATIVE POSITION
 *    UP        |           UP          |             BOTTOM
 *    UP        |           UP          |             ABOVE
 *    UP        |          DOWN         |            BOTTOM
 *    UP        |          DOWN         |             ABOVE
 *   DOWN       |           UP          |            BOTTOM
 *   DOWN       |           UP          |             ABOVE
 *   DOWN       |          DOWN         |            BOTTOM
 *   DOWN       |          DOWN         |             ABOVE
 * <p>
 *
 * Therefore, in total, we have 8 combinations. Some of these are cases are best options for the elevator,
 * while in others the request is quite expensive. In the case where the request is quite expensive, the
 * IDLE elevator will be used, if any available.
 * <p>
 * We can not categorize the combinations from most efficient to the least efficient requests. The requests, will
 * be divided into class A, B, C, D, with class A being the most efficient and class D the least.
 * <p>
 * Here is the summary of the classification:
 *  CLASS       |       ELEVATOR     |         REQUEST       |       RELATIVE POSITION
 *    A         |          UP        |           UP          |             BELOW
 *    D         |          UP        |           UP          |             ABOVE
 *    B         |          UP        |          DOWN         |             BELOW
 *    C         |          UP        |          DOWN         |             ABOVE
 *    C         |         DOWN       |           UP          |             BELOW
 *    B         |         DOWN       |           UP          |             ABOVE
 *    D         |         DOWN       |          DOWN         |             BELOW
 *    A         |         DOWN       |          DOWN         |             ABOVE
 * <p>
 * Not surprisingly, we can find the pattern in the association of the classes as well.
 * We have two of each possible cases for each class.
 * The First and last row are the best cases: the elevator will reach the floor.
 * The class B, the elevator will take a turn in the direction, but not after a long time
 * The class C, the elevator will take a U-turn longer than the class B
 * The class D, the elevator will take the double U-turn: will go on direction, change direction, go all other
 * direction and then change direction again to reach the floor.
 * <p>
 *
 * After all the methods for finding the elevator in one of the classes, we check the list and if there are
 * multiple, we will select the one that is the closest to the request. This will be easy for the class A and B,
 * but in the class C and D we might need to simulate the elevator moving before making the entire scheduler decision.
 */


public class Scheduler {
    /**
     * The scheduler controls all the elevators in the building
     * Initializing a new scheduler in the building means adding a new instance for each elevator
     * <p>
     * This design is useful if we want to have multiple block of elevators independent of each other. For instance,
     * after some research, it has been found that the Burj Khalifa has around 50 elevators, but not all the
     * elevators are managed by one scheduler. Instead, there are many schedulers that handle some particular elevators.
     */
    private final ArrayList<ElevatorController> elevatorControllers;
    /**
     * Global elevators ground lock boolean
     */
    private boolean groundLocked;
    /**
     * Global elevator fire lock boolean
     */
    private boolean fireLocked;

    public Scheduler(ArrayList<ElevatorController> controllers) {
        this.elevatorControllers = controllers;
        this.groundLocked = false;
        this.fireLocked = false;
    }

    public void acceptRequestAndProcess(ElevatorDirection requestElevatorDirection, int requestFloor) {
        // If emergency lock: no request
        if (this.areAllElevatorsLocked()) {
            Console.log("SCHEDULER", "ERROR: CALLED PROCESS BUT ELEVATORS ARE LOCKED");
            return;
        }
        ElevatorController optimalElevator = findOptimalElevator(requestElevatorDirection, requestFloor);
        // This request is to go and reach the user. After that, there will be another request inside the same
        // elevator to go on the floor the user wants.
        optimalElevator.externalRequest(requestFloor);
    }

    private ElevatorController findOptimalElevator(ElevatorDirection requestElevatorDirection, int requestFloor) {
        // WE would not consider the locked the elevators
        ArrayList<ElevatorController> unlockedElevators = new ArrayList<>();
        for (ElevatorController controller : this.elevatorControllers) {
            if (!controller.isLocked()) unlockedElevators.add(controller);
        }
        // Class A
        ArrayList<ElevatorController> classAElevators = findElevatorInClassA(unlockedElevators, requestElevatorDirection, requestFloor);
        ArrayList<ElevatorController> idleElevators = findElevatorInIDLEMode(unlockedElevators);
        // Both the class A and IDLE are the efficient one. Find the closest of these two.
        classAElevators.addAll(idleElevators);
        if (classAElevators.size() > 0) {
            Console.log("SCHEDULER", "CLASS A OR IDLE");
            // Find the most optimal out of it. The closest one
            // find the closes' elevator.
            return this.findClosestElevator(classAElevators, requestFloor);
        }

        // Class B
        ArrayList<ElevatorController> classBElevators = findElevatorInClassB(unlockedElevators, requestElevatorDirection, requestFloor);
        if (classBElevators.size() > 0) {
            Console.log("SCHEDULER", "CLASS B");
            // Find the most optimal out of it. The closest one
            // It will make the U turn. The distant it is, the nearest it will be later.
            return this.findFarthestElevator(classBElevators, requestFloor);
        }

        // Class C
        ArrayList<ElevatorController> classCElevators = findElevatorInClassC(unlockedElevators, requestElevatorDirection, requestFloor);
        if (classCElevators.size() > 0) {
            Console.log("SCHEDULER", "CLASS C");
            // Find the most optimal out of it. The closest one
            // WE will find the closest on to the elevator because after the single turn, it will be closer
            // The floor than the other elevators.
            return this.findClosestElevator(classCElevators, requestFloor);
        }

        // Class D
        ArrayList<ElevatorController> classDElevators = findElevatorInClassD(unlockedElevators, requestElevatorDirection, requestFloor);
        if (classDElevators.size() > 0) {
            Console.log("SCHEDULER", "CLASS D");
            // Find the most optimal out of it. The closest one

            // The most efficient one is the one that has to do the least double turn around.
            // The least means that the elevator first is the farthest and then the least after.
            // Therefore, a priori, we have to find the farthest one in the list.
            return this.findFarthestElevator(classDElevators, requestFloor);
        }

        Console.log("SCHEDULER", "GENERAL");
        // If none matched, return any.
        return unlockedElevators.get(0);

    }

    /**
     * Finding the Most optimal elevator for class A
     *
     * @return ArrayList<ElevatorController>: list of all the elevators
     */
    private ArrayList<ElevatorController> findElevatorInClassA(ArrayList<ElevatorController> elevatorControllers, ElevatorDirection requestElevatorDirection, int requestFloor) {
        ArrayList<ElevatorController> optimalControllers = new ArrayList<>();
        for (ElevatorController elevator : elevatorControllers) {
            if (elevator.isIDLEMode()) continue;
            // The elevator and request are UP and the elevator is BELOW the request floor.
            boolean conditionUp = elevator.getDirection() == ElevatorDirection.UP && requestElevatorDirection == ElevatorDirection.UP && elevator.getCurrentFloor() <= requestFloor;
            // The elevator and request are DOWN and the elevator is ABOVE the request floor.
            boolean conditionDown = elevator.getDirection() == ElevatorDirection.DOWN && requestElevatorDirection == ElevatorDirection.DOWN && elevator.getCurrentFloor() >= requestFloor;

            // If any of these two conditions are met, the elevator is found
            if (conditionUp || conditionDown) optimalControllers.add(elevator);
        }
        return optimalControllers;
    }

    /**
     * Finding the Most optimal elevator for class B
     *
     * @return ArrayList<ElevatorController>: list of all the elevators
     */
    private ArrayList<ElevatorController> findElevatorInClassB(ArrayList<ElevatorController> elevatorControllers, ElevatorDirection requestElevatorDirection, int requestFloor) {
        ArrayList<ElevatorController> optimalControllers = new ArrayList<>();
        for (ElevatorController elevator : elevatorControllers) {
            if (elevator.isIDLEMode()) continue;
            // The elevator is going DOWN and request is to go UP, but the elevator is BELOW the request: it will reach bottom and then go up.
            boolean conditionUp = elevator.getDirection() == ElevatorDirection.DOWN && requestElevatorDirection == ElevatorDirection.UP && elevator.getCurrentFloor() <= requestFloor;
            // The elevator is going UP and request is to go DOWN, but the elevator is ABOVE the request: it will reach up and then go up.
            boolean conditionDown = elevator.getDirection() == ElevatorDirection.UP && requestElevatorDirection == ElevatorDirection.DOWN && elevator.getCurrentFloor() >= requestFloor;

            // If any of these two conditions are met, the elevator is found
            if (conditionUp || conditionDown) optimalControllers.add(elevator);

        }
        return optimalControllers;
    }
    /**
     * Finding the Most optimal elevator for class C
     *
     * @return ArrayList<ElevatorController>: list of all the elevators
     */
    private ArrayList<ElevatorController> findElevatorInClassC(ArrayList<ElevatorController> elevatorControllers, ElevatorDirection requestElevatorDirection, int requestFloor) {
        ArrayList<ElevatorController> optimalControllers = new ArrayList<>();
        for (ElevatorController elevator : elevatorControllers) {
            if (elevator.isIDLEMode()) continue;
            // As same as class B, but in this case, the elevator will pass the floor, go up and then turn back and stop at the floor.
            boolean conditionUp = elevator.getDirection() == ElevatorDirection.UP && requestElevatorDirection == ElevatorDirection.DOWN && elevator.getCurrentFloor() <= requestFloor;
            // As same as class B, but in this case, the elevator will pass the floor, go down and then turn back and stop at the floor.
            boolean conditionDown = elevator.getDirection() == ElevatorDirection.DOWN && requestElevatorDirection == ElevatorDirection.UP && elevator.getCurrentFloor() >= requestFloor;


            // If any of these two conditions are met, the elevator is found
            if (conditionUp || conditionDown) optimalControllers.add(elevator);

        }
        return optimalControllers;
    }

    /**
     * Finding the Most optimal elevator for class C
     *
     * @return ArrayList<ElevatorController>: list of all the elevators
     */
    private ArrayList<ElevatorController> findElevatorInClassD(ArrayList<ElevatorController> elevatorControllers, ElevatorDirection requestElevatorDirection, int requestFloor) {
        ArrayList<ElevatorController> optimalControllers = new ArrayList<>();
        for (ElevatorController elevator : elevatorControllers) {
            if (elevator.isIDLEMode()) continue;
            // the elevator has passed the floor. It will go up, change the direction and go all to down. Then, it will up and take the request.
            boolean conditionUp = elevator.getDirection() == ElevatorDirection.UP && requestElevatorDirection == ElevatorDirection.UP && elevator.getCurrentFloor() >= requestFloor;
            // The elevator has passed the floor. It will go down, change the direction and go all the way up. Then, it will turn again and go down to take the request.
            boolean conditionDown = elevator.getDirection() == ElevatorDirection.DOWN && requestElevatorDirection == ElevatorDirection.DOWN && elevator.getCurrentFloor() <= requestFloor;


            // If any of these two conditions are met, the elevator is found
            if (conditionUp || conditionDown) optimalControllers.add(elevator);

        }
        return optimalControllers;
    }

    /**
     * Find an elevator in the IDLE mode

     * @return ArrayList<ElevatorController>: list of all the elevators
     */
    private ArrayList<ElevatorController> findElevatorInIDLEMode(ArrayList<ElevatorController> elevatorControllers) {
        ArrayList<ElevatorController> optimalControllers = new ArrayList<>();
        for (ElevatorController elevator : elevatorControllers) {
            if (elevator.isIDLEMode()) optimalControllers.add(elevator);
        }
        return optimalControllers;
    }

    /**
     * Finding the closest elevator out of all the elevators found so far.
     * <p>
     * In the class A, the closest one is the one given by the absolute of the difference in floors.

     * @return ElevatorController
     *
     * FOr: A
     */
    private ElevatorController findClosestElevator(ArrayList<ElevatorController> controllers, int requestFloor) {
        // just get the nearest elevator
        int min = Math.abs(requestFloor-controllers.get(0).getCurrentFloor());
        int minIndex = 0;
        for (int i = 0; i < controllers.size(); i++) {

            if (Math.abs(requestFloor-controllers.get(i).getCurrentFloor()) < min) {
                min = Math.abs(requestFloor-controllers.get(i).getCurrentFloor());
                minIndex = i;
            }

        }
        return controllers.get(minIndex);
    }

    /**
     * Finding the closest elevator out of all the elevators found so far.
     * <p>
     * In the class B, the closest one is the one given by the sum of the floors.
     * This is because:
     *  if the user is above the elevator, wants to go up, but the elevator is going down, then it has to go
     *  up again. The best one is the one nearest to the floor and going up. This will be the sum of the floors they are at.

     * @return ElevatorController
     *
     * For B
     */
    private ElevatorController findFarthestElevator(ArrayList<ElevatorController> controllers, int requestFloor) {
        int max = Math.abs(requestFloor-controllers.get(0).getCurrentFloor());
        int maxIndex = 0;
        for (int i = 0; i < controllers.size(); i++) {

            if (Math.abs(requestFloor+controllers.get(i).getCurrentFloor()) > max) {
                max = Math.abs(requestFloor-controllers.get(i).getCurrentFloor());
                maxIndex = i;
            }

        }
        return controllers.get(maxIndex);
    }

    /**
     * Updater that runs all the elevators when called
     */
    public void run() {
        for (ElevatorController controller : this.elevatorControllers) {
            controller.move();
            //controller.getStatus();
        }
    }


    /**
     * Getter and setters
     */


    /**
     * ALL THE METHODS FOR EMERGENCY SITUATION
     * Trigger the alarm: lock the elevator to the current floor
     * <p>
     * What will happen if the elevator is locked:
     * <p>
     * All elevators should have fire service modes.
     * The fire service mode can be automatically activated whenever smoke is detected in the building.
     * It can also be activated manually using a key switch on the first floor.
     * When the fire service mode is activated, the elevator cab is designed to return to the first floor.
     * If smoke is detected on the first floor, the elevator is designed to return the cab to an alternate floor.
     * Once the cab has arrived at the recall floor, the elevator doors should open.
     * <p>
     * Source: <a href="https://www.home-elevator.net/info-what-happens-to-elevator-during-fire.php">Resource</a>
     */
    // Checking if all the elevators have an emergency lock set on
    private boolean areAllElevatorsLocked() {
        for (ElevatorController controller: this.elevatorControllers) {
            if (!controller.isLocked()) return false;
        }
        return true;
    }

    /**
     * Enable ground floor lock for all elevators
     */
    public void enableGroundLock() {
        Console.log("SCHEDULER", "GROUND LOCK: ON");
        for (ElevatorController controller : this.elevatorControllers) controller.enableGroundLock();
        this.groundLocked = true;
    }
    /**
     * Disable ground floor lock for all elevators
     */
    public void disableGroundLock() {
        Console.log("SCHEDULER", "GROUND LOCK: OFF");
        for (ElevatorController controller : this.elevatorControllers) controller.disableGroundLock();
        this.groundLocked = false;
    }

    /**
     * Check if the elevator is ground locked;
     */
    public boolean isGroundLocked() {
        return this.groundLocked;
    }

    /**
     * Enable fire lock
     */
    public void enableFireLock() {
        Console.log("SCHEDULER", "FIRE LOCK: ON");
        for (ElevatorController controller : this.elevatorControllers) controller.enableFireLock();
        this.fireLocked = true;
    }

    /**
     * Disable fire lock
     */
    public void disableFireLock() {
        Console.log("SCHEDULER", "FIRE LOCK: OFF");
        for (ElevatorController controller : this.elevatorControllers) controller.disableFireLock();
        this.fireLocked = false;
    }
    /**
     * Check if the elevator is fire lock
     */
    public boolean isFireLocked() {
        return this.fireLocked;
    }

    public void sendElevatorsToOptimalPosition() {
        this.elevatorControllers.get(1).addRequest(4);
        this.elevatorControllers.get(2).addRequest(7);
    }

}
