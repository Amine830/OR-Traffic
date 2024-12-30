package org.example.models.map;

import org.example.models.vehicles.Vehicle;

import java.awt.Point;
import java.util.List;

public class Intersection {
    private final Point position;
    private IntersectionType type;
    private List<Vehicle> vehicles;

    public Intersection(int x, int y, IntersectionType type) {
        this.position = new Point(x, y);
        this.type = type;
    }

    public Point getPos() {
        return position;
    }

    public IntersectionType getType() {
        return type;
    }

    public void manageTraffic() {
        for (Vehicle vehicle : vehicles) {
            //vehicle.calculateNextPoint(); //TODO
        }
    }

}
