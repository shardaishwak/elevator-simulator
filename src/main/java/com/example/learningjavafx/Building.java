package com.example.learningjavafx;

import com.example.learningjavafx.Components.Floor;
import com.example.learningjavafx.Elevator.ElevatorController;

import java.util.ArrayList;
import java.util.UUID;

/**
 * The building class will contain an instance of the scheduler, floors and elevators moving
 * the vuilding will be identifiable with an id
 */
public class Building {
    private final UUID id;
    /**
     * A private instance of the floor
     */
    private final ArrayList<Floor> floors;
    /**
     * A public and final instance of the elevators
     */
    public final ArrayList<ElevatorController> elevators;
    /**
     * A public and final instance of the scheduler.
     */
    public final Scheduler scheduler;

    public Building(int totalFloors, int totalElevators) {
        this.id = UUID.randomUUID();

        /**
         * Creating the elevators and appending them to the list
         */
        this.elevators = new ArrayList<>();
        for (int i = 0; i < totalElevators; i++) {
            this.elevators.add(new ElevatorController());
        }

        /**
         * Creating floors and appending them
         */
        this.floors = new ArrayList<>();
        for (int i = 0; i < totalFloors; i++) {
            this.floors.add(new Floor());
        }

        /**
         * Initializing the scheduler with the elevators to control
         */
        this.scheduler = new Scheduler(elevators);
    }

    /**
     * GETTERS AND SETTERS
     */

    /**
     * Get the ID of the elevator
     * @return
     */
    public UUID getId() {
        return id;
    }
}