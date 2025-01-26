// src/main/java/org/example/view/Javafx.java
package org.example.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.controllers.SimulationController;
import org.example.models.map.Map;
import org.example.models.vehicles.Vehicle;

import java.awt.*;
import java.util.Iterator;
import java.util.List;
import javafx.scene.image.Image;



/**
 * Classe Javafx
 * pour gérer l'interface graphique de la simulation
 */
public class Javafx extends Application {
    private SimulationController simulationController;
    private Map map;
    private List<Vehicle> vehicles;
    private GridPane gridPane;
    public static int TotalWaitingTime = 0;
    private int TimeUnit = 500;
    private int peakVehicles ;
    private boolean addingVehicles = true;
    private int count = 0;

    // Load images
    private javafx.scene.image.Image northImage;
    private javafx.scene.image.Image southImage;
    private javafx.scene.image.Image eastImage;
    private javafx.scene.image.Image westImage;

    /**
     * Méthode start
     * pour démarrer l'interface graphique
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load images
        northImage = new Image("file:src/main/resources/N.png");
        southImage = new Image("file:src/main/resources/S.png");
        eastImage = new Image("file:src/main/resources/E.png");
        westImage = new Image("file:src/main/resources/W.png");

        simulationController = new SimulationController();
        simulationController.initializeSimulation(50, 35,4 ,1 );
        peakVehicles = 100;
        map = simulationController.getMap();
        vehicles = simulationController.getVehicles();

        gridPane = new GridPane();
        drawMap();
        //map.Print_Map();
        //map.Print_Lane_Directions();

        Scene scene = new Scene(gridPane, map.width * 20, map.height * 20);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Traffic Simulation");
        primaryStage.show();



        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(TimeUnit), e -> {
            updateVehicles();
            //manageVehicleCount();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * managing vehicles count
     */
    private void manageVehicleCount() {
        count = (int) (Math.random() * 5) ;
        if (addingVehicles) {
            if (vehicles.size() < peakVehicles) {
                simulationController.addVehicles(1+ count);
            } else {
                addingVehicles = false;
            }
        }
    }

    /**
     * Méthode drawMap
     * pour dessiner la map
     */
    private void drawMap() {
        for (int i = 0; i < map.height; i++) {
            for (int j = 0; j < map.width; j++) {
                Rectangle rect = new Rectangle(20, 20);
                switch (map.grille[i][j]) {
                    case 1:
                        rect.setFill(Color.GRAY);
                        break;
                    case 2:
                        rect.setFill(Color.RED);
                        break;
                    default:
                        rect.setFill(Color.GREEN);
                        break;
                }
                gridPane.add(rect, j, i);
            }
        }
    }

    /**
     * Méthode updateVehicles
     * pour mettre à jour les véhicules
     */
    private void updateVehicles() {
        gridPane.getChildren().clear();
        drawMap();


        if(vehicles.isEmpty()){
            System.out.println("Total Waiting Time: " + TotalWaitingTime*TimeUnit/1000 + " seconds");
            System.exit(0);
        }


        Iterator<Vehicle> iterator = vehicles.iterator();
        while (iterator.hasNext()) {
            Vehicle vehicle = iterator.next();
            vehicle.moveToNextPoint(map);
            //Rectangle rect = new Rectangle(19, 19, Color.BLUE);
            //gridPane.add(rect, vehicle.getPosition().y, vehicle.getPosition().x);

            // Determine the direction of the vehicle
            Image vehicleImage = getVehicleImage(vehicle);

            ImageView imageView = new ImageView(vehicleImage);
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);
            gridPane.add(imageView, vehicle.getPosition().y, vehicle.getPosition().x);

            // removes the vehicle from the list if it has arrived
            if (vehicle.isArrived()) {
                if (!vehicle.getPosition().equals(new Point(0, 0))) {
                    TotalWaitingTime += vehicle.TimeWating;
                }
                iterator.remove();
                vehicle.setPosition(new Point(0, 0));
                //rect.setFill(Color.GREEN);

            }
        }
    }

    private Image getVehicleImage(Vehicle vehicle) {
        Point current = vehicle.getPosition();
        Point next = vehicle.getPath().isEmpty() ? current : vehicle.getPath().get(0);

        if (next.x < current.x) {
            return northImage;
        } else if (next.x > current.x) {
            return southImage;
        } else if (next.y > current.y) {
            return eastImage;
        } else {
            return westImage;
        }
    }

    /**
     * Méthode main
     * pour lancer l'application
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}


