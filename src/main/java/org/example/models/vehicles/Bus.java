package org.example.models.vehicles;

import org.example.models.map.Map;

import java.awt.Point;

public class Bus extends Vehicle {
    public Bus(Point destination, Point position, int speed, int vehicleType, Map map) {
        super(destination, position, speed, vehicleType,map);
    }
}