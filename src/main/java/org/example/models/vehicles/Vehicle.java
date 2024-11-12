package org.example.models.vehicles;

import java.awt.Point;

public class Vehicle {
    private static int id = 0;
    private final int vehicleId;
    private final Point destination;
    private Point position;
    private int speed;

    public Vehicle(Point destination, Point position, int speed) {
        this.destination = destination;
        this.position = position;
        this.speed = speed;
        vehicleId = ++id;
    }
    public Point getDestination() {
        return destination;
    }
    public Point getPosition() {
        return position;
    }
    public int getSpeed() {
        return speed;
    }
    public int getVehicleId() {
        return vehicleId;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
