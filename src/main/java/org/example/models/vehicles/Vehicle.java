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
    public List<Point> path = new ArrayList<>();
    public boolean Calculated = false;

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

    public void addPointToPath( Point p) {
        this.path.add(p);
    }
    public void removePointFromPath(Point p) {
        this.path.remove(p);
    }



    public void Calulate_Next_point(Map map){
        Point current = path.getLast();
        System.out.println(path.size());
        Point destination = this.getDestination();
        //compare all neighbors of current position and return the one that is closest to destination
        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(current.x + 1, current.y));
        neighbors.add(new Point(current.x - 1, current.y));
        neighbors.add(new Point(current.x, current.y + 1));
        neighbors.add(new Point(current.x, current.y - 1));

        Point closest = null;
        double minDistance = Integer.MAX_VALUE;
        for(Point neighbor : neighbors){

            //if one of the neighbors is the destination, set it as the next position
            if(neighbor.x == destination.x && neighbor.y == destination.y){
                addPointToPath(neighbor);
                System.out.println("Calculated Path");
                Calculated = true;
                return;
            }

            //check if neighbor is valid road in the map

            if(neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= map.height|| neighbor.y >= map.width){
                continue;
            }
            if(map.grille[neighbor.x][neighbor.y] ==0){
                continue;
            }

            double distance = getDistance(neighbor, destination);
            if(distance < minDistance){
                minDistance = distance;
                closest = neighbor;
            }
        }
        assert closest != null;
        addPointToPath(closest);
    }

    private double getDistance(Point a, Point b){
        //euclidean distance
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}
