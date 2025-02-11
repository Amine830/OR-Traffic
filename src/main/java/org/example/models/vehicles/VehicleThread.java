// src/main/java/org/example/models/vehicles/VehicleThread.java
package org.example.models.vehicles;

import org.example.models.map.Map;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * This class is responsible for moving the vehicle on the map.
 */
public class VehicleThread {
    private final Vehicle vehicle;
    private final Map map;
    private final ScheduledExecutorService scheduler; // Pour les tâches périodiques (les threads des véhicules).

    /**
     * Constructor
     *
     * @param vehicle   the vehicle to move
     * @param map       the map
     * @param scheduler the scheduler
     */
    public VehicleThread(Vehicle vehicle, Map map, ScheduledExecutorService scheduler) {
        this.vehicle = vehicle;
        this.map = map;
        this.scheduler = scheduler;
    }

    /**
     * Start the thread.
     */
    public void start() {
        int updateRate = 250;
        scheduler.scheduleAtFixedRate(() -> {
            synchronized (map) {
                // On déplace le véhicule sur la carte chaque updateRate millisecondes.
                vehicle.moveToNextPoint(map);
            }
        }, 0, updateRate, TimeUnit.MILLISECONDS);
    }
}