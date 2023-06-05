package com.example.learningjavafx.Components;

import com.example.learningjavafx.Enumerations.ExternalDirection;

import java.util.UUID;

public class ExternalController {
    private final UUID id;
    private boolean pressed;
    private ExternalDirection direction;

    public ExternalController() {
        this.id = UUID.randomUUID();
        this.pressed = false;
        this.direction = ExternalDirection.NULL;
    }

    public void onPress(ExternalDirection direction) {
        this.direction = direction;
    }
    public void onDepress() {
        this.pressed = false;
        this.direction = ExternalDirection.NULL;
    }


    public UUID getId() {return this.id;}
    public boolean isPressed() {return this.pressed;}
    public ExternalDirection getDirection() {return this.direction;}
}
