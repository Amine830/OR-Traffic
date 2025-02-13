// src/main/java/org/example/view/Javafx.java
package org.example.view;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.example.controllers.SimulationController;
import org.example.models.map.Map;
import org.example.models.vehicles.Vehicle;

import java.awt.*;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.image.Image;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import org.example.models.vehicles.VehicleThread;


/**
 * Classe Javafx pour la simulation de la circulation des véhicules.
 */
public class Javafx extends Application {

    private Map map;
    private GridPane gridPane;
    private List<Vehicle> vehicles;
    private ScheduledExecutorService scheduler;
    private SimulationController simulationController;

    // Labels for statistics
    private VBox statsBox;
    private Label vehicleCountLabel;
    private Label pathChangeCountLabel;
    private Label totalWaitingTimeLabel;
    private Label totalVehicleCountLabel;
    private Label averageWaitingTimeLabel;
    private Label vehiclesReachedDestinationLabel;

    // Construction
    private static final int MAP_WIDTH = 50;
    private static final int MAP_HEIGHT = 35;
    private static final int NUM_VEHICLES = 15;
    private static final int NUM_INTERSECTIONS = 4;

    // ------------------------------------ Variables  ------------------------------------
    private int initialVehicleCount;
    private int totalVehicleCount = 0;
    private int activeVehicleCount = 0;
    public static int TotalWaitingTime = 0;
    private int vehiclesReachedDestination = 0;
    private static final int UPDATE_INTERVAL = 200;
    private static final int MAX_ACTIVE_VEHICLES = 30;
    private static final int TOTAL_VEHICLES_TO_GENERATE = 200;
    // ----------------------------------------------------------------------------------------

    // Images
    private Image Nroad;
    private Image Sroad;
    private Image Eroad;
    private Image Wroad;
    private Image Inter;



    /**
     * Démarre l'application.
     *
     * @param primaryStage la scène principale
     * @throws Exception si une erreur survient
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        loadImages();

        simulationController = new SimulationController();
        simulationController.initializeSimulation(MAP_WIDTH, MAP_HEIGHT, NUM_INTERSECTIONS, NUM_VEHICLES);
        map = simulationController.getMap();
        vehicles = new CopyOnWriteArrayList<>(simulationController.getVehicles());

        initialVehicleCount = vehicles.size();
        totalVehicleCount = initialVehicleCount;
        activeVehicleCount = initialVehicleCount;

        gridPane = new GridPane();
        drawMap();

        initializeStatsBox();

        HBox root = new HBox(10, gridPane, statsBox);
        root.setPadding(new Insets(10));

        Scene scene = new Scene(root, map.width * 20 + 200, map.height * 20 + 20);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Traffic Simulation");
        primaryStage.show();

        runSimulation();
    }

    /**
     * Exécuter la simulation.
     * Démarre les threads des véhicules à l'aide d'un scheduler.
     */
    private void runSimulation() {
        simulationController.startSimulation();

        scheduler = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());
        // Mettre à jour les véhicules et leur position.
        scheduler.scheduleAtFixedRate(this::updateVehicles, 0, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);

        // Mettre à jour les statistiques.
        scheduler.scheduleAtFixedRate(this::updateStats, 0, 1000, TimeUnit.MILLISECONDS);

        // Ajouter des véhicules périodiquement.
        scheduler.scheduleAtFixedRate(this::addVehiclesPeriodically, 0, 5000, TimeUnit.MILLISECONDS);

        // Vérifier si la simulation est terminée.
        scheduler.scheduleAtFixedRate(this::checkSimulationEnd, 0, 1000, TimeUnit.MILLISECONDS);
    }


    /**
     * Arrête la simulation.
     */
    @Override
    public void stop() {
        simulationController.stopSimulation();
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }


    /**
     * Dessiner la carte.
     */
    private void drawMap() {
        for (int i = 0; i < map.height; i++) {
            for (int j = 0; j < map.width; j++) {
                Rectangle rect = new Rectangle(20, 20);
                switch (map.grille[i][j]) {
                    case 1:
                        switch (map.laneDirections[i][j]) {
                            case NORTH:
                                rect.setFill(new ImagePattern(Nroad));
                                break;
                            case SOUTH:
                                rect.setFill(new ImagePattern(Sroad));
                                break;
                            case EAST:
                                rect.setFill(new ImagePattern(Eroad));
                                break;
                            case WEST:
                                rect.setFill(new ImagePattern(Wroad));
                                break;
                        }
                        break;
                    case 2:
                        rect.setFill(new ImagePattern(Inter));
                        break;
                    default:
                        rect.setFill(Color.GRAY);
                        break;
                }
                gridPane.add(rect, j, i);
            }
        }
    }

    /**
     * Mettre à jour les véhicules.
     */
    private void updateVehicles() {
        Platform.runLater(() -> {
            gridPane.getChildren().clear();
            drawMap();

            List<Vehicle> vehiclesToRemove = new ArrayList<>();
            for (Vehicle vehicle : vehicles) {
                Image vehicleImage = getVehicleImage(vehicle);
                ImageView imageView = new ImageView(vehicleImage);
                imageView.setFitWidth(15);
                imageView.setFitHeight(15);
                gridPane.add(imageView, vehicle.getPosition().y, vehicle.getPosition().x);

                if (vehicle.isArrived()) {
                    vehiclesToRemove.add(vehicle);
                    vehiclesReachedDestination++;
                    activeVehicleCount--;
                }
            }

            vehicles.removeAll(vehiclesToRemove);
        });
    }


    /**
     * Met à jour les statistiques.
     */
    private void updateStats() {
        Platform.runLater(() -> {
            vehicleCountLabel.setText("Véhicules Actifs: " + activeVehicleCount);
            totalVehicleCountLabel.setText("Total Véhicules: " + totalVehicleCount);
            totalWaitingTimeLabel.setText("Temps Attente: " + TotalWaitingTime);
            int averageWaitingTime = totalVehicleCount > 0 ? TotalWaitingTime / totalVehicleCount : 0;
            averageWaitingTimeLabel.setText("Attente Moyenne: " + averageWaitingTime);
            vehiclesReachedDestinationLabel.setText("Arrivés: " + vehiclesReachedDestination);
            pathChangeCountLabel.setText("Changements: " + simulationController.getTotalPathChanges());
        });
    }


    /**
     * Ajouter des véhicules périodiquement à la simulation.
     */
    private void addVehiclesPeriodically() {
        Platform.runLater(() -> {
            if (totalVehicleCount < TOTAL_VEHICLES_TO_GENERATE && activeVehicleCount < MAX_ACTIVE_VEHICLES) {
                int vehiclesToAdd = Math.min(5, MAX_ACTIVE_VEHICLES - activeVehicleCount);
                List<Vehicle> newVehicles = simulationController.addVehicles(vehiclesToAdd);
                // Ajouter les nouveaux véhicules à la liste des véhicules.
                this.vehicles.addAll(newVehicles);
                totalVehicleCount += vehiclesToAdd;
                activeVehicleCount += vehiclesToAdd;
                //System.out.println("Added " + vehiclesToAdd + " vehicles. Total vehicles: " + totalVehicleCount);

                // Démarrer les threads des nouveaux véhicules.
                for (Vehicle vehicle : newVehicles) {
                    VehicleThread vehicleThread = new VehicleThread(vehicle, map, scheduler);
                    vehicleThread.start();
                    //System.out.println("Vehicle added: " + vehicle.getVehicleId() + " at " + vehicle.getPosition()+ " taille liste : "+vehicles.size());
                }


            } else {
                //System.out.println("Maximum number of active vehicles reached.");
            }
        });
    }



    /**
     * Obtenir l'image du véhicule.
     *
     * @param vehicle le véhicule
     * @return l'image du véhicule
     */
    private Image getVehicleImage(Vehicle vehicle) {
        Point current = vehicle.getPosition();
        Point next = vehicle.getPath().isEmpty() ? current : vehicle.getPath().getFirst();

        if (next.x < current.x) {
            return new Image("file:src/main/resources/N" + vehicle.getVehiculeTexture() + ".jpg");
        } else if (next.x > current.x) {
            return new Image("file:src/main/resources/S" + vehicle.getVehiculeTexture() + ".jpg");
        } else if (next.y > current.y) {
            return new Image("file:src/main/resources/E" + vehicle.getVehiculeTexture() + ".jpg");
        } else {
            return new Image("file:src/main/resources/W" + vehicle.getVehiculeTexture() + ".jpg");
        }
    }

    /**
     * Charger les images.
     */
    private void loadImages() {
        Nroad = new Image("file:src/main/resources/north.jpg");
        Sroad = new Image("file:src/main/resources/south.jpg");
        Eroad = new Image("file:src/main/resources/east.jpg");
        Wroad = new Image("file:src/main/resources/west.jpg");
        Inter = new Image("file:src/main/resources/inter.jpg");
    }

    /**
     * Initialiser la boîte de statistiques.
     */
    private void initializeStatsBox() {
        statsBox = new VBox(10);
        statsBox.setPadding(new Insets(10));
        statsBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        Label statsTitle = new Label(" Statistiques : ");
        statsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        totalWaitingTimeLabel = new Label("Total Waiting Time: 0");
        averageWaitingTimeLabel = new Label("Average Waiting Time: 0");
        totalVehicleCountLabel = new Label("Total Vehicles: 0");
        vehiclesReachedDestinationLabel = new Label("Vehicles Reached Destination: 0");
        vehicleCountLabel = new Label("Active Vehicles: 0");
        pathChangeCountLabel = new Label("Path Changes: 0");

        // Buttons
        Button closeButton = new Button("Close");
        closeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        closeButton.setOnAction(event -> {
            stop();
            Platform.exit();
        });

        Button restartButton = new Button("Reset");
        restartButton.setStyle("-fx-background-color: blue; -fx-text-fill: white;");
        restartButton.setOnAction(event -> restartSimulation());

        HBox buttonBox = new HBox(10, closeButton, restartButton);
        buttonBox.setPadding(new Insets(10));
        buttonBox.setAlignment(Pos.CENTER);

        statsBox.getChildren().addAll(statsTitle, vehicleCountLabel, totalWaitingTimeLabel, averageWaitingTimeLabel,
                totalVehicleCountLabel, vehiclesReachedDestinationLabel, pathChangeCountLabel, buttonBox);
    }


    /**
     * Redémarrer la simulation.
     */
    private void restartSimulation() {
        stop();
        resetVariables();
        try {
            simulationController.initializeSimulation(MAP_WIDTH, MAP_HEIGHT, NUM_INTERSECTIONS, NUM_VEHICLES);
            map = simulationController.getMap();
            vehicles = new CopyOnWriteArrayList<>(simulationController.getVehicles());

            initialVehicleCount = vehicles.size();
            totalVehicleCount = initialVehicleCount;
            activeVehicleCount = initialVehicleCount;

            runSimulation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Réinitialiser les variables.
     */
    private void resetVariables() {
        TotalWaitingTime = 0;
        totalVehicleCount = 0;
        activeVehicleCount = 0;
        vehiclesReachedDestination = 0;
    }


    /**
     * Vérifier si la simulation est terminée.
     */
    private void checkSimulationEnd() {
        Platform.runLater(() -> {
            if (activeVehicleCount == 0 && vehiclesReachedDestination == totalVehicleCount) {
                showEndOfSimulationMessage();
                stop();
            }
        });
    }

    /**
     * Afficher un message à la fin de la simulation.
     */
    private void showEndOfSimulationMessage() {
        Stage stage = new Stage();
        VBox vbox = new VBox(new Label("La simulation est terminée."));
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));
        Scene scene = new Scene(vbox, 300, 100);
        stage.setScene(scene);
        stage.setTitle("Fin de la simulation");
        stage.show();
        System.out.println("Simulation complete.");
    }


    /**
     * Main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}