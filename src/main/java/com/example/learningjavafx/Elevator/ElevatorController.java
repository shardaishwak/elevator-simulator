package com.example.learningjavafx.Elevator;

import com.example.learningjavafx.Components.Door;
import com.example.learningjavafx.Enumerations.ElevatorDirection;
import com.example.learningjavafx.Helpers.Console;

import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.UUID;

/**
 * Algorithms explanation:
 *
 * The elevator controller will have approximately three priority queues.
 * These priority queues will allow us to prioritize the floors from which the requests are coming.
 * For example, if the elevator has queues on floors 5, 7, and 9, and there is another request in the same direction for floor 8, we will prioritize floor 8 instead of 9, even if the request from floor 8 came after the request from floor 9.
 * The priority queue can be considered a sorted queue, but it is much more efficient than sorting an array every time a request needs to be added.
 * Sorting an array requires shifting elements at index n, which is computationally expensive when the array is long.
 *
 * The three priority queues will be named the upQueue, downQueue, and currentQueue.
 * Depending on the direction of the elevator, the system will assign the corresponding direction queue to the currentQueue.
 * Once the current task is completed, we will invert the direction of the queue list.
 * For example, if the elevator is moving up, the currentQueue will reference the upQueue.
 * When the current task is finished, the currentQueue will reference the downQueue.
 * Whenever there is a request, we will determine its direction and add it to the appropriate upQueue or downQueue.
 */
public class ElevatorController {
    /**
     * TODO: Insert Elevator Class
     */
    private final UUID ID;

    /**
     * The instance variable will contain the position of the controller
     */
    private int currentFloor;
    /**
     * The instance variable will store the current direction of the elevator
     * The type is Enum with values of UP, DOWN, IDLE
     */
    private ElevatorDirection elevatorDirection;
    /**
     * Priority queue for up requests
     * The system will store all the requests that require the elevator to go in up
     * direction compared to the current position
     */

    private PriorityQueue<Integer> upQueue;
    /**
     * Based on the this.direction value, the currentQueue reference variable
     * will point to one of the upQueue or downQueue memory address.
     * For instance, if the elevator is going up, the currentQueue will be in upQueue.
     *
     *
     */
    private PriorityQueue<Integer> currentQueue;
    /**
     * Priority queue for down requests
     * The system will store all the requests that require the elevator to go in down
     * direction compared to the current position
     */
    private PriorityQueue<Integer> downQueue;

    /**
     * The triggered for locking all the elevators to their current position
     * When we add the floating points to the currentFloor, the isLocked will trigger
     * the elevator to reach the closest and safest floor in the same direction
     */
    private boolean locked;

    /**
     * Status for the firelock: if locked it will be true and we can retrieve the infomration to update the system
     */
    private boolean fireLock;
    /**
     * Status for the ground lock: if locked it will be true and we can retrieve the infomration to update the system
     */
    private boolean groundLock;

    /**
     * After calling the elevator, we have to wait until the user does not enter the input
     * We will add all the request that requre waiting until user enters the destination floor
     * in this hashset. For each update we will check if there is a current floor match value
     * if ther is, the elevator will not not move.
     */
    private final HashSet<Integer> userInputRequest;

    /**
     * An instance representing the door.
     */
    private final Door door;



    /**
     * Contractor:
     * Initialize the queues to default values. downQueue will store items in the reverseOrder, as
     * the poll should return the maximum value in this case.
     *
     * Initially, all elevators are in IDLE mode, until a request has not been sent by the scheduler.
     *
     * Why reverseOrder?
     * When the elevator is going down, the priority is given to the highest value floors because it is
     * going in decreasing order of the floor.
     */
    public ElevatorController() {
        /**
         * If not parameter provided to the constructor: the elevator initial floor is the ground floor.
         */
        this(0);
    }

    /**
     * Constructor with parameter
     */
    public ElevatorController(int initialFloor) {
        /**
         * Initialize a random ID
         */
        this.ID = UUID.randomUUID();
        /**
         * Initialize the priority queue will poll to min value
         */
        this.upQueue = new PriorityQueue<>();
        /**
         * Initialize the priority queue with reverseOrder for poll to target the max value
         */
        this.downQueue = new PriorityQueue<>(Collections.reverseOrder());
        /**
         * All the elevators will begin from the
         */
        this.currentFloor = initialFloor;
        /**
         * The direction of the elevator is based on the initial floor
         *
         * If the elevator is between the floor (4,8]: down direction
         * if the elevator is between the floor [0,4]: up direction
         */
        this.currentQueue =  initialFloor > 4 && initialFloor <= 8 ? this.downQueue : this.upQueue;
        /**
         * Initially in IDLE mode as there is no request
         */
        this.elevatorDirection = ElevatorDirection.IDLE;
        this.locked = false;
        this.userInputRequest = new HashSet<>();

        this.door = new Door();
    }


    /**
     * Processing the request when the user clicks from the inside of the elevator
     */
    public void internalRequest(int requestFloor) {
        // remove the current floor from the
        this.userInputRequest.remove(this.currentFloor);
        this.addRequest(requestFloor);
    }

    /**
     * In the external request, we will add the floor to the hashset for upcoming waiting queue.
     * @param requestFloor
     */
    public void externalRequest(int requestFloor) {
        this.userInputRequest.add(requestFloor);
        this.addRequest(requestFloor);
    }

    /**
     * Check if the elevator has to wait until the user enters the input
     * @return
     */
    public boolean hasToWaitUserInput() {
        return this.userInputRequest.contains(this.currentFloor);
    }
    /**
     * Process the internal request and external request.
     * In all cases, the elevator needs to stop.
     */
    public void addRequest(int requestFloor) {
        if (requestFloor > 7 || requestFloor < 0) {
            this.console("ERROR: INVALID INPUT. EXPECTED RANGE FOR FLOOR [0,8]");
            return;
        }
        // CASE: elevator is in IDLE mode: we can go in any direction
        // NOTE: We must ensure using isIDLEMode that there is no request.
        // Only in that case we must

        // the number is already in  the queue: do nothing
        if (this.upQueue.contains(requestFloor) || this.downQueue.contains(requestFloor)) return;

        // BY NOW: upQueue and downQueue are EMPTY
        
        // The initial condition means that the elevator is doing nothing by now. 
        // We are processing the same request.
        if (this.currentFloor == requestFloor) {
            // What happens if the user clicks the same floor button?
            // make them wait and enter the door
            this.changeDirectionToIDLE();
            // TODO: request insert input again
        } else if (this.elevatorDirection == ElevatorDirection.IDLE) {
            // The user has to go DOWN
            if (requestFloor < this.currentFloor) {
                this.downQueue.add(requestFloor);
                this.changeDirectionToDown();
            } else {
                // The passenger wants to go to the upper floor
                this.upQueue.add(requestFloor);
                this.changeDirectionToUp();
            }
            
        } else if (this.elevatorDirection == ElevatorDirection.UP) {
            // The elevator is going UP and the request in the upper direction that where the elevator is right now
            // The elevator can go up
            if (currentFloor < requestFloor) {
                this.upQueue.add(requestFloor);
            } else {
                this.downQueue.add(requestFloor);
            }
        } else {
            if (currentFloor < requestFloor) {
                this.upQueue.add(requestFloor);
            } else {
                this.downQueue.add(requestFloor);
            }
        }
        this.console("UP QUEUE: "+this.upQueue);
        this.console("DOWN QUEUE: "+this.downQueue);
        //this.getStatus();
    }

    /**
     * process next requeuest
     *
     * Get the next floor to process from the currentQueue
     * if the currentFloor sis not the nextfloor, move up or down
     * if not, show that the floor has been reached
     */
    private void processNextRequest() {

        int nextFloor = this.currentQueue.peek();

        if (this.currentFloor == nextFloor) {
            this.currentQueue.poll();
            // There are chances that the requests to one direction have finished but there is the other direction to process

            this.console("FLOOR: REACHED ("+this.currentFloor+")");

            // TODO: Initialize opening procedure
            return;
        }
        // Handle the direction of the elevator
        if (this.elevatorDirection == ElevatorDirection.UP) {

            this.setElevatorFloor(this.currentFloor+1);

        } else if (this.elevatorDirection == ElevatorDirection.DOWN) {
            this.setElevatorFloor(this.currentFloor-1);
        }



    }

    /**
     * Call it before the processNextREquest > move because it will poll the request
     * @return
     */
    public boolean isFloorReached() {
        return this.currentFloor == this.currentQueue.peek();
    }

    /**
     * Handle the movement of the elevator
     *
     * Check for the IDLE mode. If true, do nothing
     *
     * If not idle mode, move to up or down
     * For each movement, check if required changing the direction and process the request after
     */
    public void move() {

        /**
         * Do not move if has to wait user inpt
         */
        if (this.hasToWaitUserInput()) {
            this.console("WAITING USER TO ENTER THE FLOOR");
            return;
        }

        /**
         * Do not move if elevator is locked.
         */
        if (this.locked) {
            this.console("ERROR: CALLED MOVE BUT LOCKED ELEVATOR - NO REQUEST CAN BE PROCESSED");
            return;
        }
        // The elevator has no request. Just stay in the idle mode
        if (isIDLEMode()) {
            //System.out.println("Idle mode");
            this.changeDirectionToIDLE();
        } else {
            // There is a request to process because the elevator is not in the idle mode
            // if we have to change the direction, do It
            this.changeDirectionIfRequired();
            this.processNextRequest();
            this.console("FLOOR: " + this.currentFloor);

        }

    }

    /**
     * Change the direction of the elevator to UP
     */
    public void changeDirectionToUp() {
        this.currentQueue = this.upQueue;
        this.elevatorDirection = ElevatorDirection.UP;
        this.console("DIRECTION: UP");
    }

    /**
     * Change the direction of the elevator to DOWN
     */
    public void changeDirectionToDown() {
        this.currentQueue = this.downQueue;
        this.elevatorDirection = ElevatorDirection.DOWN;
        this.console("DIRECTION: DOWN");
    }

    /**
     * An elevator should enter the IDLE mode if there are no requests to handle
     * This means that the upQueue and downQueue are empty
     * @return boolean
     */
    public boolean isIDLEMode() {
        return this.upQueue.isEmpty() && this.downQueue.isEmpty();
    }

    /**
     * Enter the elevator to the idle mode
     */
    private void changeDirectionToIDLE() {
        this.elevatorDirection = ElevatorDirection.IDLE;
        this.currentQueue = downQueue;
        this.console("DIRECTION: IDLE");
    }

    /**
     * Change the direction of the elevator if there is no request to process in the same direction
     */
    private void changeDirectionIfRequired() {
        // Check if ther is no requiest to process
        if (!isIDLEMode() && this.currentQueue.peek() == null) {
            if (this.elevatorDirection == ElevatorDirection.UP) this.changeDirectionToDown();
            else this.changeDirectionToUp();
        }
    }


    /**
     * Change the curren floor of the elevator
     */
    public void setElevatorFloor(int floor) {
        this.currentFloor = floor;
    }

    /**
     * Simple printable method to show all the status of the elevator
     */
    public void getStatus() {
        System.out.println("Current Floor: " + this.currentFloor);
        System.out.println("Current Enum.Direction: " + this.elevatorDirection);
        System.out.println("Up queue: " + upQueue);
        System.out.println("Down queue: "  + downQueue);
        System.out.println("Upcoming floor: " + this.currentQueue.peek());
        System.out.println("=================================================");
    }

    /**
     * Print method to print the state of the elevator on the console.
     */
    public void print() {
        for(int i = 0; i < 30; i++)
        {
            System.out.println("\b");
        }
        // print request with cross
        for (int i = 0; i <= 8; i++) {
            System.out.print(" "+i+"  ");
        }
        System.out.println();
        for (int i = 0; i <= 8; i++) {
            if (this.upQueue.contains(i) || this.downQueue.contains(i)) System.out.print(" R  ");
            else System.out.print("    ");
        }
        System.out.println();
        for (int i = 0; i <= 8; i++) {
            if (i == this.currentFloor) System.out.print("[X] ");
            else System.out.print("[ ] ");
        }
        System.out.println();
    }


    /**
     * ACCESS MODIFIERS
     */
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    public ElevatorDirection getDirection() {
        return this.elevatorDirection;
    }

    // Get the ID of the elevator
    public UUID getID() {return this.ID;}

    // Get the direction of the elevator
    public void setDirection(ElevatorDirection elevatorDirection) {this.elevatorDirection = elevatorDirection;}
    // Get the current queue list
    public PriorityQueue<Integer> getCurrentQueue() {return this.currentQueue;}
    // Get the up queue list
    public PriorityQueue<Integer> getUpQueue() {return this.upQueue;}
    // Get the down queue list
    public PriorityQueue<Integer> getDownQueue() {return this.downQueue;}
    // Get the door instance.
    public Door getDoor() {return this.door;}






    /**
     * EMERGENCY FUNCTIONS
     */
    public void clearAllRequests() {
        this.elevatorDirection = ElevatorDirection.IDLE;
        this.upQueue.clear();
        this.downQueue.clear();
        this.console("REQUESTS: CLEARED");
    }

    /**
     * Lock the elevator and remove all the calls
     */
    public void lock() {
        // Lock elevator: will not remove
        this.locked = true;
        this.clearAllRequests();
        this.console("LOCK: ON");
    }
    public void unlock() {
        this.locked = false;
        this.console("LOCK: OFF");
    }
    public boolean isLocked() {return this.locked;}

    /**
     * Activate the fire movement:
     * - clear all calls
     * - move elevator until floor not reached: top floor
     * - lock elevator
     */
    public void enableFireLock() {
        this.clearAllRequests();

        // Until it has not reached the floor
        this.setElevatorFloor(7);
        // Lock the elevator
        this.lock();
        this.fireLock = true;
        this.console("FIRE MOVE: ON");
    }
    public void disableFireLock() {
        this.clearAllRequests();
        this.unlock();
        this.fireLock = false;
        this.console("FIRE MOVE: OFF");
    }
    public boolean isFireLock() {return this.fireLock;}

    /**
     * Activate lock from the ground
     * - clear all the calls
     * - move elevator until ground not reached
     * - lock elevator
     */
    public void enableGroundLock() {
        this.clearAllRequests();
        //this.addRequest(0);

        this.setElevatorFloor(0);

        this.lock();
        this.groundLock = true;
        this.console("GROUND MOVE: ON");
    }
    public void disableGroundLock() {
        this.clearAllRequests();
        this.unlock();
        this.groundLock = false;
        this.console("GROUND MOVE: OFF");
    }
    public boolean isGroundLock() {return this.groundLock;}


    protected void console(String message) {
        Console.log(String.format("ELEVATOR-%s", this.getID().toString()),  message);
    }

}
