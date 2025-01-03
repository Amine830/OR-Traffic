package org.example.models.vehicles;

import org.example.controllers.SimulationController;
import org.example.models.map.Map;

import java.awt.Point;
import java.util.*;

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
    private boolean arrived = false;

    private SimulationController simulationController;

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

    public void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }

    public Point getPosition() {
        return position;
    }

    public boolean isArrived() {return arrived;}

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
     * Se déplacer vers la destination.
     *
     *
     */
    public void moveToNextPoint() {

        if (position.equals(destination)) {
            arrived = true;
            return;
        }

        if (path.isEmpty() || path.getFirst() == null) {
            return;
        }
        Point nextPoint = path.getFirst();

        //here put the priority logic for the intersection




        if (!is_next_move_colision(nextPoint)) {
            this.position = nextPoint;
            this.path.removeFirst();
        }

    }

    /**
     * calculates the path
     *
     * @return
     */

    public void calculatePath(Map map) {
        List<List<Point>> paths = new ArrayList<>();

        checkAllPaths(map, paths);
        System.out.println("Paths for vehicle " + vehicleId + " : " + paths.size());
        if(paths.isEmpty()) {
            System.out.println("No path found for vehicle " + vehicleId);
            return;
        }
        path = paths.get(0);

        path.removeFirst();

        calculated = true;

    }


    /**
     * Calculer le prochain point.
     *
     * @param map la carte sur laquelle le véhicule se déplace.
     * @return le prochain point.
     */
    public void checkAllPaths(Map map, List<List<Point>> paths) {
        Queue<List<Point>> queue = new LinkedList<>();
        Set<Point> visited = new HashSet<>();

        // Initialize the BFS with the starting position
        List<Point> initialPath = new ArrayList<>();
        initialPath.add(position);
        queue.add(initialPath);
        visited.add(position);

        while (!queue.isEmpty()) {
            List<Point> currentPath = queue.poll();
            Point currentPoint = currentPath.get(currentPath.size() - 1);

            // If the current point is a destination, store the path
            if (currentPoint.equals(destination)) {
                paths.add(new ArrayList<>(currentPath));
                continue; // Continue to explore other paths
            }

            // Check if the current point is a road
            if (map.isRoad(currentPoint)) {
                List<Point> neighbors = map.ContinueInDirection(currentPoint);

                for (Point neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        List<Point> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }

            // Check if the current point is an intersection
            if (map.isIntersection(currentPoint)) {
                List<Point> neighbors = map.ContinueInDirection(currentPoint);

                for (Point neighbor : neighbors) {
                    if (!visited.contains(neighbor)) {
                        visited.add(neighbor);
                        List<Point> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }
        }
    }



    public boolean is_next_move_colision(Point next_move){
        List<Point> vehicles_positions = simulationController.getVehiclesPositions();
        for (Point vehicle_position : vehicles_positions){

            if (vehicle_position.equals(next_move)){
                return true;
            }
        }

        return false;
    }

    private double getDistance(Point a, Point b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }
}