// src/main/java/org/example/controllers/SimulationController.java
package org.example.controllers;

import org.example.models.map.LaneDirection;
import org.example.models.map.Map;
import org.example.models.vehicles.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * La classe simulationController permet de gérer la simulation de la circulation des véhicules.
 */
public class SimulationController {
    private Map map;
    private List<Vehicle> vehicles = new CopyOnWriteArrayList<>();
    private ScheduledExecutorService scheduler; // Pour gérer les tâches périodiques (les threads des véhicules).

    /**
     * Initialise la simulation avec les paramètres donnés.
     *
     * @param mapWidth         la largeur de la carte
     * @param mapHeight        la hauteur de la carte
     * @param numIntersections le nombre d'intersections
     * @param numVehicles      le nombre de véhicules
     * @throws Exception si une erreur survient
     */
    public void initializeSimulation(int mapWidth, int mapHeight, int numIntersections, int numVehicles)
            throws Exception {
        map = new Map(mapHeight, mapWidth);
        map.setIntersections(numIntersections);
        map.setLanesDirection();
        vehicles = new ArrayList<>();
        generateNVehicles(numVehicles);
        map.setVehicles(vehicles);
    }


    /**
     * Démarre la simulation => démarre les threads des véhicules.
     */
    public void startSimulation() {
        scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        for (Vehicle vehicle : vehicles) {
            VehicleThread vehicleThread = new VehicleThread(vehicle, map, scheduler);
            vehicleThread.start();
        }
    }

    /**
     * Arrête la simulation.
     */
    public void stopSimulation() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }


    /**
     * Ajoute un véhicule à la simulation.
     */
    public List<Vehicle> addVehicles(int numVehicles) {
        List<Vehicle> newVehicles = new ArrayList<>();
        for (int i = 0; i < numVehicles; i++) {
            Vehicle vehicle = createVehicule();
            vehicles.add(vehicle);
            newVehicles.add(vehicle);
        }
        map.setVehicles(vehicles);
        return newVehicles;
    }

    /**
     * Génère n véhicules sur la carte.
     *
     * @param n le nombre de véhicules à générer

     */
    private void generateNVehicles(int n) {
        for (int i = 0; i < n; i++) {
            Vehicle vehicle = createVehicule();
            vehicles.add(vehicle);
        }
    }

    /**
     * Retourne la carte de la simulation.
     *
     * @return la carte de la simulation
     */
    public Map getMap() {
        return map;
    }

    /**
     * Retourne la liste des véhicules de la simulation.
     *
     * @return la liste des véhicules de la simulation
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }



    public Vehicle createVehicule(){
        Random rand = new Random();
        Point starting;
        Point destination;

        // Find a valid starting point
        do {
            starting = map.roads_at_edge.get(rand.nextInt(map.roads_at_edge.size()));
        } while (!isValidStartingPoint(starting, map));

        // Find a valid destination point
        do {
            destination = map.roads_at_edge.get(rand.nextInt(map.roads_at_edge.size()));
        } while (!isValidDestinationPoint(destination, map) || isStartCloseToDestination(starting, destination));

        Vehicle vehicle;
        int vehicleType = rand.nextInt(3) + 1;


        vehicle = switch (rand.nextInt(3)) {
            case 0 -> new Car(destination, starting, 1, vehicleType, map);
            case 1 -> new Truck(destination, starting, 1, vehicleType, map);
            default -> new Bus(destination, starting, 1, vehicleType, map);
        };
        vehicle.calculatePath(map);
        vehicle.setSimulationController(this);
        return vehicle;
    }


    /**
     * Vérifie si le point de départ est proche de la destination.
     *
     * @param start       le point de départ
     * @param destination la destination
     * @return true si le point de départ est proche de la destination, false sinon
     */
    boolean isStartCloseToDestination(Point start, Point destination){
        return Math.abs(start.x - destination.x) + Math.abs(start.y - destination.y) < 2;
    }

    /**
     * retourner une list des position de tous les vehicules
     */
    public List<Point> getVehiclesPositions() {
        List<Point> positions = new ArrayList<>();
        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                positions.add(vehicle.getPosition());
            }
        }
        return positions;
    }

    /**
     * Vérifie si un véhicule est à la position donnée.
     *
     * @param point la position à vérifier
     * @return true si un véhicule est à la position donnée, false sinon
     */
    public boolean isVehicleAt(Point point) {
        synchronized (vehicles) {
            for (Vehicle vehicle : vehicles) {
                if (vehicle.getPosition().equals(point)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Vérifie si le point donné est un point de départ valide.
     *
     * @param point le point à vérifier
     * @param map   la carte sur laquelle vérifier
     * @return true si le point est un point de départ valide, false sinon
     */
    private boolean isValidStartingPoint(Point point, Map map) {
        LaneDirection direction = map.getlinedirection()[point.x][point.y];
        if (point.x < map.height / 2 && direction == LaneDirection.SOUTH) return true;
        if (point.x >= map.height / 2 && direction == LaneDirection.NORTH) return true;
        if (point.y < map.width / 2 && direction == LaneDirection.EAST) return true;
        if (point.y >= map.width / 2 && direction == LaneDirection.WEST) return true;
        return false;
    }

    /**
     * Vérifie si le point donné est un point de destination valide.
     *
     * @param point le point à vérifier
     * @param map   la carte sur laquelle vérifier
     * @return true si le point est un point de destination valide, false sinon
     */
    private boolean isValidDestinationPoint(Point point, Map map) {
        LaneDirection direction = map.laneDirections[point.x][point.y];
        if (point.x < map.height / 2 && direction == LaneDirection.NORTH) return true;
        if (point.x >= map.height / 2 && direction == LaneDirection.SOUTH) return true;
        if (point.y < map.width / 2 && direction == LaneDirection.WEST) return true;
        if (point.y >= map.width / 2 && direction == LaneDirection.EAST) return true;
        return false;
    }

    public int getTotalPathChanges() {
        return vehicles.stream().mapToInt(Vehicle::getHowManyDidChange).sum();
    }
}