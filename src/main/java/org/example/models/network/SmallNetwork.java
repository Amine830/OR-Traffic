package org.example.models.network;

import org.example.models.map.Intersection;
import org.example.models.vehicles.Vehicle;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SmallNetwork {

    final List<Vehicle> vehicles = Collections.synchronizedList(new ArrayList<>());
    Intersection networkIntersection;
    public ConcurrentHashMap<Vehicle, Integer> Traffic = new ConcurrentHashMap<>();

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
        for(Vehicle vehicle : Traffic.keySet()) {
            int score = vehicle.vehiclesBehind*10+1;
            if(vehicle.is_someone_infront()) {
                score = 0;
            }
            Traffic.put(vehicle, score);
        }


        LinkedHashMap<Vehicle, Integer> sortedMap = new LinkedHashMap<>();
        ArrayList<Integer> list = new ArrayList<>();

        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
            list.add(entry.getValue());
        }


        Collections.sort(list, Collections.reverseOrder());
        for (int num : list) {
            for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedMap.put(entry.getKey(), num);
                }
            }
        }
        Vehicle first = null;
        for(Map.Entry<Vehicle, Integer> entry : sortedMap.entrySet()) {
            first = entry.getKey();
            break;
        }

        for(Vehicle vehicle : Traffic.keySet()) {
            if(vehicle.nextTurn!=null &&
                    first.nextTurn!=null
                    &&!vehicle.isConflict(first.nextTurn,vehicle.nextTurn)) {

                //get the value of the vehicle from the sorted map
                int value = sortedMap.get(vehicle);
                if(value!=0){
                    Traffic.put(vehicle, 1);
                }else{
                    Traffic.put(vehicle, 0);
                }
            } else {
                Traffic.put(vehicle, 0);
            }
        }

    }



    public void addVehicleToQueue(Vehicle vehicle) {
        Traffic.put(vehicle, 1);
        //calculatePreority();
    }

    public void removeVehicle(Vehicle vehicle) {
        Traffic.remove(vehicle);
        //calculatePreority();
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
            if (vehicles.contains(vehicle)) {
                return;
            }
            vehicles.add(vehicle);
        }
    }
}