package org.example.models.map;

import org.example.models.vehicles.Vehicle;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map{

    public final int[][] grille;
    public final int width;
    public final int height;

    // directions for each lane in the intersection
    public final LaneDirection[][] laneDirections;

    public List<Intersection> intersections = new ArrayList<>();
    public List<Point> roads_at_edge = new ArrayList<>();
    private List<Vehicle> vehicles;

    public Map(int x, int y) {
        this.width = y;
        this.height = x;
        this.grille = new int[x][y];
        this.laneDirections = new LaneDirection[x][y];

        // Initialize all cells
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                this.grille[i][j] = 0;
                this.laneDirections[i][j] = null;
            }
        }
    }

    /**
     * Set the lane direction for a given point
     */
    private void setLaneDirection(Point p, LaneDirection direction) {
        this.laneDirections[p.x][p.y] = direction;
    }
    
    public LaneDirection[][] getlinedirection(){
        return this.laneDirections;
    }

    public LaneDirection getlinedirection(Point p){
        return this.laneDirections[p.x][p.y];
    }

    public void setLanesDirection() {
       for (int i = 0; i < this.width; i++ ){
           for (int j = 0; j < this.height; j++){
               if(this.grille[j][i] == 1){
                if(this.width-1>=i+1 && this.grille[j][i+1] == 0){
                    this.setLaneDirection(new Point(j, i), LaneDirection.NORTH);
                }
                if(i-1 >= 0 &&this.grille[j][i-1] == 0){
                    this.setLaneDirection(new Point(j, i), LaneDirection.SOUTH);
                }
                if(this.height-1>=j+1 && this.grille[j+1][i] == 0){
                    this.setLaneDirection(new Point(j, i), LaneDirection.EAST);
                }
                if(j-1>= 0 && this.grille[j-1][i] == 0){
                    this.setLaneDirection(new Point(j, i), LaneDirection.WEST);
                }
               }
           }
       }

       for(Intersection i : intersections){
              Point p = i.getPos();
              this.setLaneDirection(new Point(p.x, p.y), LaneDirection.SOUTH_WEST);
              this.setLaneDirection(new Point(p.x + 1, p.y), LaneDirection.SOUTH_EAST);
              this.setLaneDirection(new Point(p.x, p.y + 1), LaneDirection.NORTH_WEST);
              this.setLaneDirection(new Point(p.x + 1, p.y + 1), LaneDirection.NORTH_EAST);
       }
    }

    /**
     * Print the map for visualization
     */
    public void Print_Map() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                System.out.print(this.grille[i][j] + " ");
            }
            System.out.println();
        }
    }

    /**
     * Print the lane directions for visualization .
     */
    public void Print_Lane_Directions() {
        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                if (this.laneDirections[i][j] != null) {
                    switch (this.laneDirections[i][j]) {
                        case NORTH:
                            System.out.print("N  ");
                            break;
                        case SOUTH:
                            System.out.print("S  ");
                            break;
                        case EAST:
                            System.out.print("E  ");
                            break;
                        case WEST:
                            System.out.print("W  ");
                            break;
                        case NORTH_EAST:
                            System.out.print("NE ");
                            break;
                        case NORTH_WEST:
                            System.out.print("NW ");
                            break;
                        case SOUTH_EAST:
                            System.out.print("SE ");
                            break;
                        case SOUTH_WEST:
                            System.out.print("SW ");
                            break;
                        case INTERSECTION:
                            System.out.print("I  ");
                            break;
                        default:
                            System.out.print("?  ");
                            break;
                    }
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Réccupérer les véhicules de la carte
     * @return Liste de véhicules
     */
    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    /**
     * Mettre les véhicules dans la carte
     * @param vehicles Liste de véhicules
     */
    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    /**
     * Check if an intersection exists at the same horizontal or vertical position
     * @param x
     * @param y
     * @return
     */
    private boolean intersectionExists(int x, int y) {
        for (Intersection intersection : intersections) {
            int ix = intersection.getPos().x;
            int iy = intersection.getPos().y;

            // Check if (x, y) is within a 5x5 area centered around (ix, iy)
            if (Math.abs(ix - x) <= 4 || Math.abs(iy - y) <= 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the intersections in the grid
     * @return List of intersections
     */
    public List<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * is the point an intersection
     */
    public boolean isIntersection(Point p) {
        return grille[p.x][p.y] == 2;
    }

    /**
     * is the point a road
     */
    public boolean isRoad(Point p) {
        return grille[p.x][p.y] == 1;
    }

    /**get the intersection at the point
     * @param p
     * @return
     */
    public Intersection getIntersection(Point p) {
        for (Intersection i : intersections) {

            Point left = new Point(i.getPos().x + 1, i.getPos().y);
            Point right = new Point(i.getPos().x, i.getPos().y + 1);
            Point bottom = new Point(i.getPos().x + 1, i.getPos().y + 1);
            if (i.getPos().equals(p)
                    || left.equals(p)
                    || right.equals(p)
                    || bottom.equals(p)) {
                return i;
            }
        }
        return null;
    }

    /**
     * Set the intersections in the grid
     * @param n Number of intersections to set
     * @throws Exception
     */
    public void setIntersections(int n) throws Exception {
        for (int i = 0; i < n; i++) {
            Random random = new Random();
            int x = random.nextInt(this.height - 3) + 1;
            int y = random.nextInt(this.width - 3) + 1;

            if (intersectionExists(x, y)) {
                i--;
                continue;
            }

            Point p = new Point(x, y);
            Random rand = new Random();
            ArrayList<IntersectionType> types = new ArrayList<>();
            types.add(IntersectionType.FOUR_WAY);
            types.add(IntersectionType.THREE_WAY_N);
            types.add(IntersectionType.THREE_WAY_S);
            types.add(IntersectionType.THREE_WAY_E);

            IntersectionType type = types.get(rand.nextInt(types.size()));

            Add_Intersection(new Intersection(p.x, p.y, type));

            set_valide(intersections.get(intersections.size() - 1));
        }

        for (int i = 0; i < this.width; i++) {
            if (this.grille[0][i] == 1) this.roads_at_edge.add(new Point(0, i));
            if (this.grille[this.height - 1][i] == 1) this.roads_at_edge.add(new Point(this.height - 1, i));
        }

        for (int i = 0; i < this.height; i++) {
            if (this.grille[i][0] == 1) this.roads_at_edge.add(new Point(i, 0));
            if (this.grille[i][this.width - 1] == 1) this.roads_at_edge.add(new Point(i, this.width - 1));
        }
    }

    /**
     * Set the roads in the grid
     * @param intersection Intersection to set
     * @throws Exception
     */
    private void set_valide(Intersection intersection) throws Exception {
        Point p = intersection.getPos();

        switch (intersection.getType()) {
            case FOUR_WAY:
                setVertical_all(p);
                setHorizontal_all(p);
                break;
            case THREE_WAY_N:
                setVertical_north(p);
                setHorizontal_all(p);
                break;
            case THREE_WAY_S:
                setVertical_south(p);
                setHorizontal_all(p);
                break;
            case THREE_WAY_E:
                setHorizontal_east(p);
                setVertical_all(p);
                break;
            case THREE_WAY_W:
                setHorizontal_west(p);
                setVertical_all(p);
                break;
            default:
                throw new Exception("Invalid Intersection Type");
        }
    }

    /**
     * On met la route verticale dans la grille + on met les directions des voies .
     */
    private void setVerticalP(Point p, int i) throws Exception {
        if (this.grille[i][p.y] == 0) {
            this.grille[i][p.y] = 1;
            this.grille[i][p.y + 1] = 1;

//            // si la route est au nord
//            if (i < p.x) {
//                setLaneDirection(new Point(i, p.y), LaneDirection.NORTH);
//                setLaneDirection(new Point(i, p.y + 1), LaneDirection.SOUTH);
//            }
//            // si la route est au sud
//            else {
//                setLaneDirection(new Point(i, p.y), LaneDirection.SOUTH);
//                setLaneDirection(new Point(i, p.y + 1), LaneDirection.NORTH);
//            }
        } else if (this.grille[i][p.y] == 1) {
            Add_Intersection(new Intersection(i, p.y, IntersectionType.FOUR_WAY));
        }
    }

    /**
     * On met la route horizontale dans la grille + on met les directions des voies .
     */
    private void setHorizontalP(Point p, int j) throws Exception {
        if (this.grille[p.x][j] == 0) {
            this.grille[p.x][j] = 1;
            this.grille[p.x + 1][j] = 1;
//            if (j < p.y) {
//                setLaneDirection(new Point(p.x, j), LaneDirection.WEST);
//                setLaneDirection(new Point(p.x + 1, j), LaneDirection.EAST);
//            } else {
//                setLaneDirection(new Point(p.x, j), LaneDirection.EAST);
//                setLaneDirection(new Point(p.x + 1, j), LaneDirection.WEST);
//            }
        } else if (this.grille[p.x][j] == 1) {
            Add_Intersection(new Intersection(p.x, j, IntersectionType.FOUR_WAY));
        }
    }

    // If the intersection doesn't lack vertical road
    private void setVertical_north(Point p) throws Exception {
        for (int i = p.x; i < this.height; i++) {
            setVerticalP(p, i);
        }
    }

    // If the intersection lacks a road to the south
    private void setVertical_south(Point p) throws Exception {
        for (int i = 0; i < p.x; i++) {
            setVerticalP(p, i);
        }
    }

    // If the intersection lacks a road to the north
    private void setVertical_all(Point p) throws Exception {
        for (int i = 0; i < this.height; i++) {
            setVerticalP(p, i);
        }
    }

    // If the intersection doesn't lack horizontal road
    private void setHorizontal_all(Point p) throws Exception {
        for (int j = 0; j < this.width; j++) {
            setHorizontalP(p, j);
        }
    }

    // If the intersection lacks a road to the east
    private void setHorizontal_east(Point p) throws Exception {
        for (int j = 0; j < p.y; j++) {
            setHorizontalP(p, j);
        }
    }

    // If the intersection lacks a road to the west
    private void setHorizontal_west(Point p) throws Exception {
        for (int j = p.y; j < this.width; j++) {
            setHorizontalP(p, j);
        }
    }

    /**
     * On ajoute une intersection dans la grille + on met les directions des voies .
     * @param i Intersection to add
     */
    private void Add_Intersection(Intersection i) throws Exception {

        this.intersections.add(i);
        this.grille[i.getPos().x][i.getPos().y] = 2;
        this.grille[i.getPos().x + 1][i.getPos().y] = 2;
        this.grille[i.getPos().x][i.getPos().y + 1] = 2;
        this.grille[i.getPos().x + 1][i.getPos().y + 1] = 2;


//        // Set lane directions for the intersection
//        setLaneDirection(new Point(i.getPos().x, i.getPos().y), LaneDirection.NORTH_EAST);
//        setLaneDirection(new Point(i.getPos().x + 1, i.getPos().y), LaneDirection.SOUTH_EAST);
//        setLaneDirection(new Point(i.getPos().x, i.getPos().y + 1), LaneDirection.NORTH_WEST);
//        setLaneDirection(new Point(i.getPos().x + 1, i.getPos().y + 1), LaneDirection.SOUTH_WEST);

        Exception e = new Exception("Invalid Map Intersection at " + intersections.get(intersections.size() - 1).getPos());
        for (int j = 0; j < this.width; j++) {
            if (this.grille[0][j] == 2) {
                throw e;
            }
            if (this.grille[this.height - 1][j] == 2) {
                throw e;
            }
        }
        for (int j = 0; j < this.height; j++) {
            if (this.grille[j][0] == 2) {
                throw e;
            }
            if (this.grille[j][this.width - 1] == 2) {
                throw e;
            }
        }
    }

    /**
     * Fonction pour obtenir les voisins d'un point + on vérifie si la direction est valide
     * @param current Current point
     * @return List of neighbors
     */
    public List<Point> ContinueInDirection(Point current) {
        List<Point> neighbors = new ArrayList<>();
        neighbors.add(new Point(current.x + 1, current.y));
        neighbors.add(new Point(current.x - 1, current.y));
        neighbors.add(new Point(current.x, current.y + 1));
        neighbors.add(new Point(current.x, current.y - 1));

        List<Point> validNeighbors = new ArrayList<>();
        for (Point neighbor : neighbors) {
            // si le voisin est en dehors de la grille
            if (neighbor.x < 0 || neighbor.y < 0 || neighbor.x >= height || neighbor.y >= width) {
                continue;
            }
            // si le voisin n'est pas une route
            if (grille[neighbor.x][neighbor.y] == 0) {
                continue;
            }
            // si le voisin est une intersection
            LaneDirection direction = laneDirections[neighbor.x][neighbor.y];
            if (direction != null && isValidDirection(direction, current, neighbor)) {
                validNeighbors.add(neighbor);
            }
        }
        return validNeighbors;
    }

    /**
     * Check On vérifie si la direction est valide
     * @param neighborDirection Lane direction
     * @param current Current point
     * @param neighbor Neighbor point
     * @return vrai si la direction est valide
     */
    public boolean isValidDirection(LaneDirection neighborDirection, Point current, Point neighbor) {
        switch (neighborDirection) {
            case NORTH:
                return neighbor.x < current.x;
            case SOUTH:
                return neighbor.x > current.x;
            case EAST:
                return neighbor.y > current.y;
            case WEST:
                return neighbor.y < current.y;
            case NORTH_EAST:
                return (neighbor.x < current.x || neighbor.y > current.y);
            case NORTH_WEST:
                return (neighbor.x < current.x || neighbor.y < current.y);
            case SOUTH_EAST:
                return (neighbor.x > current.x || neighbor.y > current.y);
            case SOUTH_WEST:
                return (neighbor.x > current.x || neighbor.y < current.y);
            default:
                System.out.println("Invalid direction dans isValidDirection");
                return false;
        }
    }
}