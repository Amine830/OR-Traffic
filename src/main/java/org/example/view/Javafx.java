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
import org.example.Main;
import org.example.models.map.Map;
import org.example.models.vehicles.Vehicle;

import java.awt.*;
import java.util.List;

public class Javafx extends Application {
    private static Map map;
    private static List<Vehicle> vehicles;
    private GridPane gridPane;

    @Override
    public void start(Stage primaryStage) throws Exception {
        map = new Map(20, 20);
        map.setIntersections(3);
        Main.generateNVehicles(1, map);
        //Main.generateVehicle(map, new Point(0, 2), new Point(18, 18), "car");

        vehicles = Main.vehicles;

        gridPane = new GridPane();
        drawMap();
        map.setLanesDirection();
        map.Print_Map();
        map.Print_Lane_Directions();

        Scene scene = new Scene(gridPane, map.width * 20, map.height * 20);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Traffic Simulation");
        primaryStage.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> updateVehicles()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

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

    private void updateVehicles() {
        gridPane.getChildren().clear();
        drawMap();
        for (Vehicle vehicle : vehicles) {
            vehicle.moveTowardsDestination(map);
            Rectangle rect = new Rectangle(20, 20, Color.BLUE);
            gridPane.add(rect, vehicle.getPosition().y, vehicle.getPosition().x);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}