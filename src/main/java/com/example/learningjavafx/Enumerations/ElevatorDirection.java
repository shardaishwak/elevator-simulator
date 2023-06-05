package com.example.learningjavafx.Enumerations;

/**
 * There are three states in which the elevator can be: UP, DOWN, or IDLE.
 * In the UP state, the elevator is moving in the upward direction, while it moves downward in the DOWN state.
 * In the IDLE state, the elevator is not moving.
 * The UP/DOWN state occurs when the elevator is either moving with a passenger or moving towards a floor to pick up a passenger.
 * The IDLE state is reached when the elevator stops due to the absence of passengers/calls or when it has reached the destination floor and is dropping off a passenger.
 */
public enum ElevatorDirection {
    UP, DOWN, IDLE
}
