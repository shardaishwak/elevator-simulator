package com.example.learningjavafx.Components;

import com.example.learningjavafx.Elevator.ElevatorController;

public class Elevator {
    private final ElevatorController controller;

    private final Door door;

    private boolean emergencyTrigger;

    private boolean locked;

    public Elevator() {
        this.controller = new ElevatorController();
        this.door = new Door();
        this.emergencyTrigger = false;
        this.locked = false;

    }

}
