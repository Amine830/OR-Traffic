package org.example.models.vehicles;

import org.example.controllers.SimulationController;
import org.example.models.map.Intersection;
import org.example.models.map.LaneDirection;
import org.example.models.map.Map;
import org.example.models.map.Turns;

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
    public boolean turning = false;

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
    public void moveToNextPoint(Map map) {

        if (position.equals(destination)) {
            arrived = true;
            return;
        }

        if (path.isEmpty() || path.getFirst() == null) {
            return;
        }
        Point nextPoint = path.getFirst();

        //here put the priority logic for the intersection

        //prototype

        if(!turning){
        if(map.isIntersection(nextPoint)){
            //if the vehicle is the first in the queue
            Intersection intersection = map.getIntersection(nextPoint);
            Turns turn = null;
            if(path.size() >= 4){
                turn = getTurn(position, path.get(3), map);
            }else if(path.size() == 3){
                turn = getTurn(position, path.get(2), map);
            } else if(path.size() == 2) {
                turn = getTurn(position, path.get(1), map);
            }

            if(!intersection.hasTraffic(this)){
                intersection.addTraffic(this, turn);

            }

                    if(!intersection.canTurn(this, turn)){
                        System.out.println("Vehicle " + vehicleId + " is waiting at intersection " + nextPoint);
                        return;
                    }else{
                        turning = true;
                    }

            }
        }

        if(turning){
            if(map.isIntersection(position)){
                Intersection intersection = map.getIntersection(position);
                if(map.isRoad(nextPoint)){
                    System.out.println("Vehicle " + vehicleId + " is liberation at " + position);
                    intersection.removeTraffic(this);
                    turning = false;
                }
            }
        }
        //----------------

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
        path = paths.getFirst();

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
            Point currentPoint = currentPath.getLast();

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


    private Turns getTurn (Point start, Point end , Map map){

        LaneDirection startDirection = map.getlinedirection(start);
        LaneDirection endDirection = map.getlinedirection(end);

        switch (startDirection) {
            case NORTH:
                switch (endDirection) {
                    case NORTH:
                        return Turns.FROM_NORTH_STRAIGHT;
                    case EAST:
                        return Turns.FROM_NORTH_RIGHT;
                    case WEST:
                        return Turns.FROM_NORTH_LEFT;

                }
            case SOUTH:
                switch (endDirection) {
                    case SOUTH:
                        return Turns.FROM_SOUTH_STRAIGHT;
                    case EAST:
                        return Turns.FROM_SOUTH_LEFT;
                    case WEST:
                        return Turns.FROM_SOUTH_RIGHT;
                }
            case EAST:
                switch (endDirection) {
                    case EAST:
                        return Turns.FROM_EAST_STRAIGHT;
                    case NORTH:
                        return Turns.FROM_EAST_LEFT;
                    case SOUTH:
                        return Turns.FROM_EAST_RIGHT;
                }
            case WEST:
                switch (endDirection) {
                    case WEST:
                        return Turns.FROM_WEST_STRAIGHT;
                    case NORTH:
                        return Turns.FROM_WEST_RIGHT;
                    case SOUTH:
                        return Turns.FROM_WEST_LEFT;
                }
            default:
                return null;
        }

    }
}