package org.example.models.map;

import org.example.models.vehicles.Vehicle;

import java.awt.Point;

public class Intersection {
    private final Point position;
    private IntersectionType type;




    public Intersection(int x, int y, IntersectionType type) {
        this.position = new Point(x, y);
        this.type = type;;

    }


    public Point getPos() {
        return position;
    }

    public IntersectionType getType() {
        return type;
    }



}
