package com.example.learningjavafx;

/**
 * A static class for instantiating a new instance of a building
 *
 * We define here the total number floors and elevators
 */
public class RunnableBuilding {
    public static final int floors = 8;
    public static final int elevators = 3;
    /**
     * This will contain all the logic inside a building
     * The building will then initialize all the instanced
     * for elevators, floors, scheduler and other controllers
     * to make the elevator work.
     *
     * Doing this will allow us to be versatile and use the same component each time.
     */
    public static final Building building = new Building(floors, elevators);
}
