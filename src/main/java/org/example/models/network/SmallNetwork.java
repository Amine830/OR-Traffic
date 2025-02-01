package org.example.models.network;

import org.example.models.map.Intersection;
import org.example.models.map.Turns;
import org.example.models.vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SmallNetwork  {


    List<Vehicle> vehicles = Collections.synchronizedList(new ArrayList<>());
    Intersection networkIntersection;
    ConcurrentHashMap<Vehicle, Integer> Traffic = new ConcurrentHashMap<>();

    public SmallNetwork(Intersection networkIntersection, Vehicle vehicle) {
        synchronized (vehicles) {
            vehicles.add(vehicle);
        }
        this.networkIntersection = networkIntersection;
    }

    public boolean is_first(Vehicle vehicle) {
        return Traffic.get(vehicle) == 1;
    }

    public void calculatePreority() {


        // Calculate scores for each vehicle
        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {

            int score = entry.getKey().vehiclesBehind*10 + entry.getKey().TimeWating*2 + entry.getKey().getPath().size();
            if (entry.getKey().is_someone_infront()) {
                score = 0;
            }

            Traffic.put(entry.getKey(), score);

        }

        // Sort the vehicles based on their scores

        synchronized (vehicles) {
            for (int i = 0; i < vehicles.size(); i++) {
                for (int j = i + 1; j < vehicles.size(); j++) {
                    Integer scoreI = Traffic.get(vehicles.get(i));
                    Integer scoreJ = Traffic.get(vehicles.get(j));
                    if (scoreI != null && scoreJ != null && scoreI < scoreJ) {
                        Vehicle temp = vehicles.get(i);
                        vehicles.set(i, vehicles.get(j));
                        vehicles.set(j, temp);

                    }
                }
            }

            // Set priority for the first vehicle
            if (!vehicles.isEmpty()) {

                Traffic.put(vehicles.get(0), 1);
                Vehicle first = vehicles.get(0);

                // Set priority for the remaining vehicles
                for (int i = 1; i < vehicles.size(); i++) {
                    if (first.nextTurn != null && !vehicles.get(i).is_someone_infront() &&
                            first.isConflict(first.nextTurn, vehicles.get(i).nextTurn)) {
                        Traffic.put(vehicles.get(i), 1);

                    } else {
                        Traffic.put(vehicles.get(i), 2);

                    }
                }
            }
        }

    }

    public void addVehicleToQueue(Vehicle vehicle) {
        if (!Traffic.containsKey(vehicle)) {
            Traffic.put(vehicle, 1000);
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

    public void addVehicle(Vehicle vehicle) {
        synchronized (vehicles) {
            vehicles.add(vehicle);
        }
    }





}
