package org.example.models.central;

import org.example.models.map.Intersection;
import org.example.models.vehicles.Vehicle;

import java.util.List;

/**
 * Classe TrafficControlCenter
 * pour gérer le trafic des véhicules et des intersections
 */
public class TrafficControlCenter {
    private List<Vehicle> vehicles;
    private List<Intersection> intersections;

    public TrafficControlCenter(List<Vehicle> vehicles, List<Intersection> intersections) {
        this.vehicles = vehicles;
        this.intersections = intersections;
    }

    public void manageTraffic() {
        for (Intersection intersection : intersections) {
            intersection.manageTraffic();
        }
        for (Vehicle vehicle : vehicles) {
            // TODO : gestion du trafic des véhicules
        }
    }
}