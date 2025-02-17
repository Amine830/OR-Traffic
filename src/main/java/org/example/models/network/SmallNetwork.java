package org.example.models.network;

import org.example.models.map.Intersection;
import org.example.models.vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SmallNetwork {

    final List<Vehicle> vehicles = Collections.synchronizedList(new ArrayList<>());
    Intersection networkIntersection;
    public List<Vehicle> Traffic = new ArrayList<>();

    public SmallNetwork(Intersection networkIntersection, Vehicle vehicle) {
        synchronized (vehicles) {
            vehicles.add(vehicle);
        }
        this.networkIntersection = networkIntersection;
    }

    public boolean is_first(Vehicle vehicle) {
        Vehicle first = Traffic.getFirst();
        if(first.equals(vehicle)) {
            return true;
        }
        return !first.isConflict(first.nextTurn, vehicle.nextTurn);
    }

    public void calculatePreority() {
        // Sort the Traffic list based on the score of each vehicle in descending order
        Traffic.sort((v1, v2) -> {
            int score1 = v1.getScore();
            int score2 = v2.getScore();
            return Integer.compare(score2, score1); // Descending order
        });

    }



    public void addVehicleToQueue(Vehicle vehicle) {
        if(!Traffic.contains(vehicle)) {
            Traffic.add(vehicle);
        }
        calculatePreority();
    }


    public void removeVehicle(Vehicle vehicle) {
        Traffic.remove(vehicle);
        calculatePreority();
    }

    public Intersection getNetworkIntersection() {
        return networkIntersection;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

//    public List<Vehicle> getVehiclesInQueue() {
//        List<Vehicle> vehiclesInQueue = new ArrayList<>();
//        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
//            if (entry.getValue() == 1) {
//                vehiclesInQueue.add(entry.getKey());
//            }
//        }
//        return vehiclesInQueue;
//    }

    public void addVehicle(Vehicle vehicle) {
        synchronized (vehicles) {
            if (vehicles.contains(vehicle)) {
                return;
            }
            vehicles.add(vehicle);
        }
    }
}