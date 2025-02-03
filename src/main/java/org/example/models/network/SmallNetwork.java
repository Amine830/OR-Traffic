package org.example.models.network;

import org.example.models.map.Intersection;
import org.example.models.map.Turns;
import org.example.models.vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SmallNetwork  {


    final List<Vehicle> vehicles = Collections.synchronizedList(new ArrayList<>());
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


        //Sort the trafic in the trafic based where the bigger value is the first
        Traffic = Traffic.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(ConcurrentHashMap::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), Map::putAll);

        Vehicle firstVehicle ;
        if (!Traffic.isEmpty()) {
            firstVehicle = Traffic.entrySet().iterator().next().getKey();
            Traffic.put(firstVehicle, 1);
        }else{
            return;
        }

        //for the rest if the value isnt 0 make it 1
        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
            if (entry.getValue() != 0) {
                if(firstVehicle.nextTurn!=null&& entry.getKey().nextTurn!=null
                &&firstVehicle.isConflict(entry.getKey().nextTurn, firstVehicle.nextTurn)) {
                    Traffic.put(entry.getKey(), 0);
                } else {
                    Traffic.put(entry.getKey(), 1);
                }
            }
        }


    }



    public void addVehicleToQueue(Vehicle vehicle) {

        int score = vehicle.vehiclesBehind * 3 + vehicle.TimeWating;
        if (vehicle.is_someone_infront()) {
            score = 0;
        }
        Traffic.put(vehicle, score);


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

    public List<Vehicle> getVehiclesInQueue() {
        List<Vehicle> vehiclesInQueue = new ArrayList<>();
        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
            if (entry.getValue() == 1) {
                vehiclesInQueue.add(entry.getKey());
            }
        }
        return vehiclesInQueue;
    }

    public void addVehicle(Vehicle vehicle) {
        synchronized (vehicles) {
            vehicles.add(vehicle);
        }
    }





}
