package com.example.learningjavafx.Components;


import java.util.UUID;

/**
 *  Class defining each floor of the building
 *  Each building will contain an external button controller
 */
public class Floor {
    private final UUID id;
    /**
     * External button controller
     */
    private final ExternalController controller;
    /**
     * Door instance
     */
    private final Door door;

    public Floor() {
        this.id = UUID.randomUUID();
        this.controller = new ExternalController();
        this.door = new Door();
    }

    /**
     * GETTERS AND SETTERS
     */

    public UUID getId() {return this.id;}
    public ExternalController getController() {return this.controller;}
    public Door getDoor() {return this.door;}

}
