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
    HashMap<Vehicle, Integer> Traffic = new HashMap<>();

    public SmallNetwork(Intersection networkIntersection, Vehicle vehicle) {
        vehicles.add(vehicle);
        this.networkIntersection = networkIntersection;
    }

    public boolean is_first(Vehicle vehicle) {
        return Traffic.get(vehicle) == 1;
    }

    public void calculatePreority() {
        //for every vehicle in the network if there is vehicle infont make it 2
        for (Map.Entry<Vehicle, Integer> entry : Traffic.entrySet()) {
            if(entry.getKey().is_someone_infront()){
                Traffic.put(entry.getKey(), 2);
            }else{
                //for the rest we implement a algorithm to calculate the preority 1 goes first 2 waits
                Traffic.put(entry.getKey(), 1);
            }
        }



    }

    public void addVehicleToQueue(Vehicle vehicle) {
        if(!Traffic.containsKey(vehicle)) {
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
        vehicles.add(vehicle);
    }



}
