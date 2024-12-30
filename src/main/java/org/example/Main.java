package org.example;


import org.example.models.map.Map;
import org.example.models.vehicles.Bus;
import org.example.models.vehicles.Car;
import org.example.models.vehicles.Truck;
import org.example.models.vehicles.Vehicle;
import org.example.models.central.TrafficControlCenter;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class Main  {
    public static List<Vehicle> vehicles = new ArrayList<>();
    public static void main(String[] args) throws Exception {

        Map map = new Map(20,20);
        map.setIntersections(3);

        map.Print_Map();
        generateNVehicles(1, map);

        for (Vehicle vehicle : vehicles){
            while(!vehicle.isCalculated()){
                vehicle.calculateNextPoint(map);
            }
            System.out.println(vehicle.getPath());
        }

//        TrafficControlCenter controlCenter = new TrafficControlCenter(vehicles, map.getIntersections());
//        controlCenter.manageTraffic();

    }


    /**
     * Generer n vehicules
     * @param n
     * @param map
     */
    public static void generateNVehicles(int n, Map map) {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            Point starting = map.roads_at_edge.get(rand.nextInt(map.roads_at_edge.size()));
            Point destination = map.roads_at_edge.get(rand.nextInt(map.roads_at_edge.size()));
            Vehicle vehicle;
            switch (rand.nextInt(3)) {
                case 0:
                    vehicle = new Car(destination, starting, 1);
                    break;
                case 1:
                    vehicle = new Truck(destination, starting, 1);
                    break;
                case 2:
                default:
                    vehicle = new Bus(destination, starting, 1);
                    break;
            }
            vehicles.add(vehicle);
        }
    }

    public static void generateVehicle(Map map, Point starting, Point destination, String vehicleType) {
        Vehicle vehicle;
        switch (vehicleType.toLowerCase()) {
            case "car":
                vehicle = new Car(destination, starting, 1);
                break;
            case "truck":
                vehicle = new Truck(destination, starting, 1);
                break;
            case "bus":
                vehicle = new Bus(destination, starting, 1);
                break;
            default:
                throw new IllegalArgumentException("Invalid vehicle type: " + vehicleType);
        }
        vehicles.add(vehicle);
    }
}