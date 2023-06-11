package com.example.learningjavafx.Components;

import com.example.learningjavafx.Enumerations.DoorStatus;

import java.util.UUID;

/**
 * Door component of the elevator
 */
public class Door {
    private final UUID id;
    /**
     * Each door will contain the door status of closed or opened or jammed.
     */
    private DoorStatus status;

    public Door() {
        this.id = UUID.randomUUID();
        this.status = DoorStatus.OPEN;
    }

    /**
     * SETTERS AND GETTERS
     * @param status
     */

    public void setStatus(DoorStatus status) {
        this.status = status;
    }

    public UUID getId() {return this.id;}
    public DoorStatus getStatus() {return this.status;}
}
