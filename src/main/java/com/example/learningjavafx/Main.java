package com.example.learningjavafx;

import com.example.learningjavafx.Enumerations.ElevatorDirection;

public class Main {
    public static void main(String[] args) {
        /*ElevatorController controller = new ElevatorController();
        controller.setElevatorFloor(4);
        controller.print();

        ElevatorRunner runner = new ElevatorRunner(controller);
        runner.run(scanner);*/
        /*
        ElevatorController controller1 = new ElevatorController();
        controller1.setDirection(ElevatorDirection.UP);
        controller1.setElevatorFloor(8);

        ElevatorController controller2 = new ElevatorController();
        controller2.setDirection(ElevatorDirection.DOWN);
        controller2.setElevatorFloor(8);

        ElevatorController controller3 = new ElevatorController();
        controller3.setDirection(ElevatorDirection.DOWN);
        controller3.setElevatorFloor(2);

        System.out.println(controller1.getID());
        System.out.println(controller1.getDirection());
        controller1.print();

        System.out.println(controller2.getID());
        System.out.println(controller2.getDirection());
        controller1.print();

        System.out.println(controller3.getID());
        System.out.println(controller3.getDirection());
        controller3.print();



        ArrayList<ElevatorController> elevators = new ArrayList<>();

        elevators.add(controller1);
        elevators.add(controller2);
        elevators.add(controller3);
        Scheduler scheduler = new Scheduler(elevators);

        System.out.println(scheduler.findOptimalElevator(ElevatorDirection.UP, 1).getID());


        */

        Building building = new Building(8, 3);
        building.elevators.get(0).setElevatorFloor(0);
        building.elevators.get(0).setDirection(ElevatorDirection.UP);

        building.elevators.get(1).setElevatorFloor(4);
        building.elevators.get(1).setDirection(ElevatorDirection.DOWN);

        building.elevators.get(2).setElevatorFloor(6);
        building.elevators.get(2).setDirection(ElevatorDirection.IDLE);




        building.scheduler.enableGroundLock();
        building.scheduler.disableGroundLock();








    }
}