package org.example;


import org.example.models.map.Map;
import org.example.models.vehicles.Vehicle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main  {
    static List<Vehicle> vehicles = new ArrayList<>();
    public static void main(String[] args) throws Exception {

        Map map = new Map(20,20);
        map.setIntersections(3);

        map.Print_Map();
        generateNVehicles(1, map);

        for (Vehicle vehicle : vehicles){
            while(!vehicle.Calculated){
                vehicle.Calulate_Next_point(map);
            }
            System.out.println(vehicle.path);
        }

    }


    public static void generateNVehicles(int n, Map map){
        //for now we will generate 1 vehicle
        Random rand = new Random();
        Point starting = map.roads_at_edge.get(rand.nextInt(map.roads_at_edge.size()));
        Point destination = map.roads_at_edge.get(rand.nextInt(map.roads_at_edge.size()));
        Vehicle vehicle = new Vehicle(destination, starting, 1);
        vehicles.add(vehicle);

    }
}