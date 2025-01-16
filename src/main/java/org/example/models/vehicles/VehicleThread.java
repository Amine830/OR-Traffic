package org.example.models.vehicles;

import org.example.models.map.Map;

/**
 * Class repressentant un thread de véhicule.
 */
public class VehicleThread extends Thread {
    private final Vehicle vehicle;
    private final Map map;

    /**
     * Constructeur de la classe VehicleThread.
     *
     * @param vehicle le véhicule
     * @param map     la carte
     */
    public VehicleThread(Vehicle vehicle, Map map) {
        this.vehicle = vehicle;
        this.map = map;
    }

    /**
     * Méthode run du thread.
     */
    @Override
    public void run() {
        while (!vehicle.isArrived()) {
            synchronized (map) {
                vehicle.moveToNextPoint(map);
            }
            try {
                // Sleep for the vehicle's speed in seconds (speed is in tiles per second)
                Thread.sleep(vehicle.getSpeed() * 1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}