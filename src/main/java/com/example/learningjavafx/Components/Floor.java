package com.example.learningjavafx.Components;


import java.util.UUID;

public class Floor {
    private final UUID id;
    private final ExternalController controller;
    private final Door door;

    public Floor() {
        this.id = UUID.randomUUID();
        this.controller = new ExternalController();
        this.door = new Door();
    }

    public UUID getId() {return this.id;}
    public ExternalController getController() {return this.controller;}
    public Door getDoor() {return this.door;}

}
