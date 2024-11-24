package org.example.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.example.models.map.Map;
import org.example.models.vehicles.Vehicle;
import org.example.Main;

import java.awt.Point;
import java.util.List;

public class Javafx extends Application {
    private static Map map;
    private static List<Vehicle> vehicles;

    @Override
    public void start(Stage primaryStage) throws Exception {
        map = new Map(20, 20);
        map.setIntersections(3);
        Main.generateNVehicles(1, map);

        GridPane gridPane = new GridPane();

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

        Scene scene = new Scene(gridPane, map.width * 20, map.height * 20);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Map View");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}