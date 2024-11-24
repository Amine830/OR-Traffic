package org.example.models.vehicles;

import org.example.models.map.Map;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

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
        vehicleId = ++id;
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

    public void addPointToPath(Point p) {
        this.path.add(p);
    }

    public void removePointFromPath(Point p) {
        this.path.remove(p);
    }

    public void calculateNextPoint(Map map) {
        Point current = path.get(path.size() - 1);
        System.out.println(path.size());
        Point destination = this.getDestination();
        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(current.x + 1, current.y));
        neighbors.add(new Point(current.x - 1, current.y));
        neighbors.add(new Point(current.x, current.y + 1));
        neighbors.add(new Point(current.x, current.y - 1));

        Point closest = null;
        double minDistance = Integer.MAX_VALUE;
        for (Point neighbor : neighbors) {
            if (neighbor.x == destination.x && neighbor.y == destination.y) {
                addPointToPath(neighbor);
                System.out.println("Calculated Path");
                calculated = true;
                return;
            }

            if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= map.height || neighbor.y >= map.width) {
                continue;
            }
            if (map.grille[neighbor.x][neighbor.y] == 0) {
                continue;
            }

            double distance = getDistance(neighbor, destination);
            if (distance < minDistance) {
                minDistance = distance;
                closest = neighbor;
            }
        }
        assert closest != null;
        addPointToPath(closest);
    }

    private double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    public List<Point> getPath() {
        return path;
    }

    public boolean isCalculated() {
        return calculated;
    }
}