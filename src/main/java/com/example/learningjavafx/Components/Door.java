package com.example.learningjavafx.Components;

import com.example.learningjavafx.Enumerations.DoorStatus;

import java.util.UUID;

public class Door {
    private final UUID id;
    private DoorStatus status;

    public Door() {
        this.id = UUID.randomUUID();
        this.status = DoorStatus.OPEN;
    }

    public void setStatus(DoorStatus status) {
        this.status = status;
    }

    public UUID getId() {return this.id;}
    public DoorStatus getStatus() {return this.status;}
}
