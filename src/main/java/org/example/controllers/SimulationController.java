// src/main/java/org/example/controllers/SimulationController.java
package org.example.controllers;

import org.example.models.map.LaneDirection;
import org.example.models.map.Map;
import org.example.models.vehicles.Bus;
import org.example.models.vehicles.Car;
import org.example.models.vehicles.Truck;
import org.example.models.vehicles.Vehicle;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * La classe simulationController permet de gérer la simulation de la circulation des véhicules.
 */
public class SimulationController {
    private Map map;
    private List<Vehicle> vehicles;

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
        generateNVehicles(numVehicles, map);
        map.setVehicles(vehicles);
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

    /**
     * Génère n véhicules sur la carte.
     *
     * @param n   le nombre de véhicules à générer
     * @param map la carte sur laquelle générer les véhicules
     */
    private void generateNVehicles(int n, Map map) {
        Random rand = new Random();
        for (int i = 0; i < n; i++) {
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
            vehicle.calculatePath(map);
            vehicles.add(vehicle);
        }
    }

    boolean isStartCloseToDestination(Point start, Point destination){
        return Math.abs(start.x - destination.x) + Math.abs(start.y - destination.y) < 2;
    }

    /**
     * retourner une list des position de tous les vehicules
     */

    public List <Point> getVehiclesPositions(){
        List <Point> positions = new ArrayList<>();
        for (Vehicle vehicle : vehicles){
            positions.add(vehicle.getPosition());
        }
        return positions;
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
}