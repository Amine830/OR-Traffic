package org.example.models.network;

import org.example.models.map.Intersection;
import org.example.models.map.Turns;
import org.example.models.vehicles.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmallNetwork  {


    List<Vehicle> vehicles = new ArrayList<>();
    Intersection networkIntersection ;

    public SmallNetwork(Intersection networkIntersection, Vehicle vehicle) {
        vehicles.add(vehicle);
        this.networkIntersection = networkIntersection;
    }




    public Intersection getNetworkIntersection() {
        return networkIntersection;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

//    public synchronized boolean canTurn(Vehicle v, Turns t) {
//        // Check all current traffic to see if there's a conflict
//        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
//            Vehicle otherVehicle = entry.getKey();
//            Turns otherTurn = otherVehicle.nextTurn;
//            if(!otherVehicle.turning){
//                continue;
//            }
//
//            // Skip the current vehicle itself
//            if (otherVehicle.equals(v)) {
//                continue;
//            }
//
//            // Check for conflicts between the current vehicle's desired turn and others
//            if (isConflict(t, otherTurn)) {
//                return false; // Conflict detected
//            }
//        }
//        return true; // No conflict
//    }




}
