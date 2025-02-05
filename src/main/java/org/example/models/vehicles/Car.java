package org.example.models.vehicles;

import org.example.models.map.Map;

import java.awt.Point;

public class Car extends Vehicle {
    public Car(Point destination, Point position, int speed, int vehicleType, Map map) {
        super(destination, position, speed, vehicleType,map);
    }
}