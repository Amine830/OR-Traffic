// src/main/java/org/example/view/Javafx.java
package org.example.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.controllers.SimulationController;
import org.example.models.map.Map;
import org.example.models.vehicles.Vehicle;

import java.util.List;

/**
 * Classe Javafx
 * pour gérer l'interface graphique de la simulation
 */
public class Javafx extends Application {
    private SimulationController simulationController;
    private Map map;
    private List<Vehicle> vehicles;
    private GridPane gridPane;

    /**
     * Méthode start
     * pour démarrer l'interface graphique
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        simulationController = new SimulationController();
        simulationController.initializeSimulation(40, 30, 4, 16);
        map = simulationController.getMap();
        vehicles = simulationController.getVehicles();

        gridPane = new GridPane();
        drawMap();
        map.Print_Map();
        map.Print_Lane_Directions();

        Scene scene = new Scene(gridPane, map.width * 20, map.height * 20);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Traffic Simulation");
        primaryStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(250), e -> updateVehicles()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
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
        for (Vehicle vehicle : vehicles) {
            vehicle.moveTowardsDestination(map);
            Rectangle rect = new Rectangle(20, 20, Color.BLUE);
            gridPane.add(rect, vehicle.getPosition().y, vehicle.getPosition().x);
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