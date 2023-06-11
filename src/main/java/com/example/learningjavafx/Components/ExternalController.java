package com.example.learningjavafx.Components;

import com.example.learningjavafx.Enumerations.ExternalDirection;

import java.util.UUID;

/**
 * External control class will handle the status of the external floor directions
 */
public class ExternalController {
    private final UUID id;
    /**
     * Check if the button is clicked.
     */
    private boolean pressed;
    /**
     * Get the status of clicked button
     */
    private ExternalDirection direction;

    public ExternalController() {
        this.id = UUID.randomUUID();
        this.pressed = false;
        this.direction = ExternalDirection.NULL;
    }

    /**
     * GETTERS and SETTERS
     * @param direction
     */

    /**
     * Handle click on the button
     */
    public void onPress(ExternalDirection direction) {
        this.direction = direction;
    }

    /**
     * Handle depress and disabling
     */
    public void onDepress() {
        this.pressed = false;
        this.direction = ExternalDirection.NULL;
    }


    public UUID getId() {return this.id;}
    public boolean isPressed() {return this.pressed;}
    public ExternalDirection getDirection() {return this.direction;}
}
