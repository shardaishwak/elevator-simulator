package com.example.learningjavafx;

import com.example.learningjavafx.Components.Floor;
import com.example.learningjavafx.Elevator.ElevatorController;

import java.util.ArrayList;
import java.util.UUID;

public class Building {
    private final UUID id;
    private final ArrayList<Floor> floors;
    public final ArrayList<ElevatorController> elevators;
    public final Scheduler scheduler;

    public Building(int totalFloors, int totalElevators) {
        this.id = UUID.randomUUID();

        this.elevators = new ArrayList<>();
        for (int i = 0; i < totalElevators; i++) {
            this.elevators.add(new ElevatorController());
        }

        this.floors = new ArrayList<>();
        for (int i = 0; i < totalFloors; i++) {
            this.floors.add(new Floor());
        }

        this.scheduler = new Scheduler(elevators);
    }

    public UUID getId() {
        return id;
    }

    public ArrayList<Floor> getFloors() {
        return this.floors;
    }
}