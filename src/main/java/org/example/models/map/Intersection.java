package org.example.models.map;

import org.example.models.vehicles.Vehicle;

import java.awt.Point;
import java.util.*;
import java.util.Map;

public class Intersection {
    private final Point position;
    private IntersectionType type;
    private List<Vehicle> vehicles ;





    public Intersection(int x, int y, IntersectionType type) {
        this.position = new Point(x, y);
        this.type = type;;

    }
    //for the prototype-------

    HashMap<Vehicle, Turns> Traffic = new HashMap<>();

    public synchronized void addTraffic(Vehicle v, Turns t){
        Traffic.put(v, t);
    }

    public synchronized void removeTraffic(Vehicle v){
        Traffic.remove(v);
    }



    public synchronized boolean hasTraffic(Vehicle v){
        return Traffic.containsKey(v);
    }

    public synchronized boolean canTurn(Vehicle v, Turns t) {
        // Check all current traffic to see if there's a conflict
        for (Map.Entry<Vehicle, Turns> entry : Traffic.entrySet()) {
            Vehicle otherVehicle = entry.getKey();
            Turns otherTurn = entry.getValue();
            if(!otherVehicle.turning){
                continue;
            }

            // Skip the current vehicle itself
            if (otherVehicle.equals(v)) {
                continue;
            }

            // Check for conflicts between the current vehicle's desired turn and others
            if (isConflict(t, otherTurn)) {
                return false; // Conflict detected
            }
        }
        return true; // No conflict
    }

    private boolean isConflict(Turns t, Turns otherTurn) {
        switch (t){
            case FROM_NORTH_STRAIGHT -> {
                return (otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT);
            }
            case FROM_NORTH_LEFT -> {
                return (otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_RIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT );
            }
            case FROM_NORTH_RIGHT -> {
                return (otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT );
            }
            case FROM_SOUTH_STRAIGHT -> {
                return (otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_EAST_RIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT);
            }
            case FROM_SOUTH_LEFT -> {
                return (otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT );
            }
            case FROM_SOUTH_RIGHT -> {
                return (otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT );
            }
            case FROM_EAST_STRAIGHT -> {
                return (otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT
                );
            }
            case FROM_EAST_LEFT -> {
                return (otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT );
            }
            case FROM_EAST_RIGHT -> {
                return (otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT );
            }
            case FROM_WEST_STRAIGHT -> {
                return (otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_RIGHT);
            }
            case FROM_WEST_LEFT -> {
                return (otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT );
            }
            case FROM_WEST_RIGHT -> {
                return (otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT );
            }

        }
        return false;
    }

    //-------------------------




    public Point getPos() {
        return position;
    }

    public IntersectionType getType() {
        return type;
    }



}
