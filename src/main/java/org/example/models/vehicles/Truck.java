package org.example.models.vehicles;


import org.example.models.map.Map;

import java.awt.Point;

public class Truck extends Vehicle {
    public Truck(Point destination, Point position, int speed, int vehicleType, Map map) {
        super(destination, position, speed, vehicleType,map);
    }
}
