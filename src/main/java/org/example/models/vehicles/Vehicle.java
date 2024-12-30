package org.example.models.vehicles;

import org.example.models.map.Map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a vehicle.
 */
public class Vehicle {
    private static int id = 0;
    private final int vehicleId;
    private final Point destination;
    private Point position;
    private int speed;
    private List<Point> path = new ArrayList<>();
    private boolean calculated = false;

    public Vehicle(Point destination, Point position, int speed) {
        this.destination = destination;
        this.position = position;
        this.speed = speed;
        this.vehicleId = ++id;
        this.path.add(position);
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

    public List<Point> getPath() {
        return path;
    }

    public boolean isCalculated() {
        return calculated;
    }

    /**
         * Calculer le prochain point.
     *
     * @param map la carte sur laquelle le véhicule se déplace.
     * @return le prochain point.
     */
    public Point calculateNextPoint(Map map) {
        Point current = position;
        List<Point> neighbors = map.getNeighbors(current);

        Point closest = null;
        double minDistance = Double.MAX_VALUE;
        for (Point neighbor : neighbors) {
            double distance = getDistance(neighbor, destination);
            if (distance < minDistance) {
                minDistance = distance;
                closest = neighbor;
            }
        }
        return closest;
    }

    /**
     * Se déplacer vers la destination.
     *
     * @param map la carte sur laquelle le véhicule se déplace.
     */
    public void moveTowardsDestination(Map map) {
        if (position.equals(destination)) {
            return;
        }
        Point nextPoint = calculateNextPoint(map);
        if (nextPoint != null) {
            position = nextPoint;
            path.add(nextPoint);
        }
    }

    private double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}