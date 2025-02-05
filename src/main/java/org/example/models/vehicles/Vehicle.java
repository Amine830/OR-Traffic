package org.example.models.vehicles;

import org.example.controllers.SimulationController;
import org.example.models.map.Intersection;
import org.example.models.map.LaneDirection;
import org.example.models.map.Map;
import org.example.models.map.Turns;
import org.example.models.network.SmallNetwork;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Class representing a vehicle.
 */
public class Vehicle {
    private static int id = 0;
    private final int vehiculeTexture;
    private final int vehicleId;
    private final Point destination;
    private Point position;
    private int speed;
    private List<Point> path = new ArrayList<>();
    private List<Intersection> intersectionsToPass = new ArrayList<>();
    private boolean arrived = false;
    public boolean turning = false;
    public int TimeWating = 0;
    public int vehiclesBehind = 0;
    private Map map;


    public Turns nextTurn = null;

    public SmallNetwork localnetwork;


    private SimulationController simulationController;

    public Vehicle(Point destination, Point position, int speed , int vehiculeTexture ,Map map) {
        this.destination = destination;
        this.position = position;
        this.speed = speed;
        this.vehicleId = ++id;
        this.vehiculeTexture = vehiculeTexture;
        this.path.add(position);
        this.map = map;
    }

    public int getVehiculeTexture() {
        return vehiculeTexture;
    }
    public Point getDestination() {
        return destination;
    }

    public void setSimulationController(SimulationController simulationController) {
        this.simulationController = simulationController;
    }


    public int getSpeed() {
        return speed;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setPosition(Point position) {
        this.position = position;
    }



    // Synchronized methods needed...
    public synchronized Point getPosition() {
        return position;
    }

    public synchronized List<Point> getPath() {
        return path;
    }

    public synchronized boolean isArrived() {
        return arrived;
    }



private synchronized void get_local_network(){
    if(intersectionsToPass.isEmpty()){
        return;
    }
    List<Vehicle> vehicles = new CopyOnWriteArrayList<>(simulationController.getVehicles());
    for(Vehicle vehicle : vehicles){
        if(vehicle.localnetwork != null && !vehicle.intersectionsToPass.isEmpty()){
            if(vehicle.intersectionsToPass.getFirst().equals(intersectionsToPass.getFirst())
                    && vehicle != this){
                localnetwork = vehicle.localnetwork;
                localnetwork.addVehicle(this);
                return;
            }
        }
    }
    localnetwork = new SmallNetwork(intersectionsToPass.getFirst(), this);
}

    /**
     * Se déplacer vers la destination.
     */


    public synchronized void moveToNextPoint(Map map) {


        if (position.equals(destination)) {
            arrived = true;
            return;
        }

        if (path.isEmpty() || path.getFirst() == null) {
            return;
        }

        Point nextPoint = path.getFirst();

        if (!intersectionsToPass.isEmpty() ) {
            Intersection nextIntersection = intersectionsToPass.getFirst();
            int distance = distance(position, nextIntersection.getPos());
            get_local_network();
            if (distance < 4 && localnetwork != null ) {
                getVehiclesBehind();
                localnetwork.addVehicleToQueue(this);
                if(nextTurn == null){
                    nextTurn = getTurn(position, path, map);
                }
            }

        }


        if(!turning) {

            if (map.isIntersection(nextPoint)) {
                if(!localnetwork.is_first(this)){
                    TimeWating++;
                    return;
                }
                synchronized (localnetwork) {
                    for (Vehicle v : localnetwork.getVehicles()) {
                        if (v.turning) {
                            if (isConflict(nextTurn, v.nextTurn)) {
                                TimeWating++;
                                return;
                            }
                        }
                    }

                    if (!is_next_move_colision(nextPoint) && is_next_afterI_free(nextPoint)) {
                        this.position = nextPoint;
                        this.path.removeFirst();
                        turning = true;

                    } else {
                        TimeWating++;
                    }
                }
                return;
            }else{
                if (!is_next_collision_localnetwork(nextPoint)) {
                    this.position = nextPoint;
                    this.path.removeFirst();
                }else{
                    TimeWating++;
                }
                return;
            }

        } else {
            if(map.isIntersection(position)){
                if(map.isRoad(nextPoint)){
                    if (!is_next_move_colision(nextPoint)) {
                        this.position = nextPoint;
                        this.path.removeFirst();
                        localnetwork.removeVehicle(this);
                        localnetwork.getVehicles().remove(this);
                        turning = false;
                        nextTurn = null;
                        intersectionsToPass.removeFirst();
                        get_local_network();
                    }else{
                        TimeWating++;
                    }
                    return;
                }
            }

        }

        if (!is_next_move_colision(nextPoint)) {
            this.position = nextPoint;
            this.path.removeFirst();
        }else{
            TimeWating++;
        }


    }

    public boolean is_next_collision_localnetwork(Point nextPoint){
        if(localnetwork == null){
            return false;
        }
        LaneDirection direction = map.getlinedirection(position);
        for(Vehicle vehicle : localnetwork.getVehicles()){
            LaneDirection otherDirection = map.getlinedirection(vehicle.position);
            if(direction == otherDirection && vehicle.position.equals(nextPoint)){
                return true;
            }
        }
        return false;
    }


    public boolean is_next_afterI_free(Point nextPoint){
        //get the index of the next point in the path
        int index = path.indexOf(nextPoint);
        if(index == -1){
            return true;
        }
        Point Current = path.get(index);
        while(map.isIntersection(Current)){
            index++;
            if(index == path.size()){
                return true;
            }
            Current = path.get(index);
        }
        if(Current.equals(destination)){
            return true;
        }
        index = path.indexOf(Current);
        return !is_next_move_colision(path.get(index+1));
    }



    /**
     * calculates the path
     *
     * @return
     */

    public void calculatePath(Map map) {

        List<List<Point>> paths = new ArrayList<>();
        checkAllPaths(map, paths, position, destination);

        if(paths.isEmpty()) {
            System.out.println("No path found for vehicle " + vehicleId);
            return;
        }
        //get the shortest path
        for(List<Point> p : paths){
            //condition here to add if there is a intersection to avoid

            if(p.size() < this.path.size() || this.path.size()<2){
                this.path = p;
            }
        }

        for(Point point : path){
            if(map.isIntersection(point)){
                Intersection intersection = map.getIntersection(point);
                if(!intersectionsToPass.contains(intersection)){
                    intersectionsToPass.add(intersection);
                }
            }
        }
        path.removeFirst();



    }


    /**
     * Calculer le prochain point.
     *
     * @param map la carte sur laquelle le véhicule se déplace.
     * @return le prochain point.
     */
    public void checkAllPaths(Map map, List<List<Point>> paths, Point start, Point destination) {
        Queue<List<Point>> queue = new LinkedList<>();

        // Initialize the BFS with the starting position
        List<Point> initialPath = new ArrayList<>();
        initialPath.add(start);
        queue.add(initialPath);

        while (!queue.isEmpty()) {
            List<Point> currentPath = queue.poll();
            Point currentPoint = currentPath.get(currentPath.size() - 1);

            // If the current point is the destination, store the path
            if (currentPoint.equals(destination)) {
                paths.add(new ArrayList<>(currentPath));
                if(paths.size() > 0){
                    return;
                }
                continue; // Continue to explore other paths
            }

            // Check if the current point is a road
            if (map.isRoad(currentPoint)) {
                List<Point> neighbors = map.ContinueInDirection(currentPoint);

                for (Point neighbor : neighbors) {
                    // Avoid cycles in the current path
                    if (!currentPath.contains(neighbor)) {
                        List<Point> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }

            // Check if the current point is an intersection
            if (map.isIntersection(currentPoint)) {
                List<Point> neighbors = map.ContinueInDirection(currentPoint);

                for (Point neighbor : neighbors) {
                    // Avoid cycles in the current path
                    if (!currentPath.contains(neighbor)) {
                        List<Point> newPath = new ArrayList<>(currentPath);
                        newPath.add(neighbor);
                        queue.add(newPath);
                    }
                }
            }
        }
    }


    public synchronized boolean is_next_move_colision(Point next_move){

        List<Vehicle> Ve = simulationController.getVehicles();
        for (Vehicle V : Ve){
            if (V == null){
                continue;
            }
            if (V.position.equals(next_move)){
                return true;
            }
        }


        return false;
    }


    private Turns getTurn (Point start, List<Point> path , Map map){

        LaneDirection startDirection = map.getlinedirection(start);
        LaneDirection endDirection = null;
        int indexOfStart = path.indexOf(start);
        if(indexOfStart == -1){
            indexOfStart = 0;
        }
        //go from this index till we find a intersection
        int i;
        for( i = indexOfStart; i < path.size(); i++){
            //if we find the vehicule destination
            if(path.get(i).equals(destination)){
               return null;
            }
            if(map.isIntersection(path.get(i))){
                break;
            }
        }
        for (int j = i; j < path.size(); j++){
            if(map.isRoad(path.get(j))){
                endDirection = map.getlinedirection(path.get(j));
                break;
            }
        }

        assert endDirection != null;

        switch (startDirection) {
            case NORTH:
                switch (endDirection) {
                    case NORTH:
                        return Turns.FROM_NORTH_STRAIGHT;
                    case EAST:
                        return Turns.FROM_NORTH_RIGHT;
                    case WEST:
                        return Turns.FROM_NORTH_LEFT;

                }
            case SOUTH:
                switch (endDirection) {
                    case SOUTH:
                        return Turns.FROM_SOUTH_STRAIGHT;
                    case EAST:
                        return Turns.FROM_SOUTH_LEFT;
                    case WEST:
                        return Turns.FROM_SOUTH_RIGHT;
                }
            case EAST:
                switch (endDirection) {
                    case EAST:
                        return Turns.FROM_EAST_STRAIGHT;
                    case NORTH:
                        return Turns.FROM_EAST_LEFT;
                    case SOUTH:
                        return Turns.FROM_EAST_RIGHT;
                }
            case WEST:
                switch (endDirection) {
                    case WEST:
                        return Turns.FROM_WEST_STRAIGHT;
                    case NORTH:
                        return Turns.FROM_WEST_RIGHT;
                    case SOUTH:
                        return Turns.FROM_WEST_LEFT;
                }
            default:
                return null;
        }

    }

    public synchronized boolean isConflict(Turns t, Turns otherTurn) {

        switch (t){
            case FROM_NORTH_STRAIGHT -> {
                return (otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT);
            }
            case FROM_NORTH_LEFT -> {
                return (otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_RIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT );
            }
            case FROM_NORTH_RIGHT -> {
                return (otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT );
            }
            case FROM_SOUTH_STRAIGHT -> {
                return (otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_EAST_RIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT);
            }
            case FROM_SOUTH_LEFT -> {
                return (otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_LEFT );
            }
            case FROM_SOUTH_RIGHT -> {
                return (otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_LEFT );
            }
            case FROM_EAST_STRAIGHT -> {
                return (otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT
                );
            }
            case FROM_EAST_LEFT -> {
                return (otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_WEST_STRAIGHT ||
                        otherTurn == Turns.FROM_WEST_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT );
            }
            case FROM_EAST_RIGHT -> {
                return (otherTurn == Turns.FROM_WEST_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT );
            }
            case FROM_WEST_STRAIGHT -> {
                return (otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_RIGHT);
            }
            case FROM_WEST_LEFT -> {
                return (otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_EAST_STRAIGHT ||
                        otherTurn == Turns.FROM_EAST_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_RIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT ||
                        otherTurn == Turns.FROM_SOUTH_STRAIGHT ||
                        otherTurn == Turns.FROM_SOUTH_LEFT );
            }
            case FROM_WEST_RIGHT -> {
                return (otherTurn == Turns.FROM_EAST_LEFT ||
                        otherTurn == Turns.FROM_NORTH_STRAIGHT ||
                        otherTurn == Turns.FROM_NORTH_LEFT );
            }

        }
        return false;
    }

    private int distance(Point p1, Point p2){
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    public boolean is_someone_infront(){
        if(intersectionsToPass.isEmpty()|| nextTurn == null){
            return false;
        }
        LaneDirection direction = map.getlinedirection(position);
        Intersection intersection = this.intersectionsToPass.getFirst();
        for (Vehicle vehicle : localnetwork.getVehicles()) {
            LaneDirection otherDirection = map.getlinedirection(vehicle.position);
            if ( direction == otherDirection &&
                    distance(vehicle.position, intersection.getPos()) <
                            distance(this.position, intersection.getPos())) {
                return true;
            }
        }
        return false;
    }

    public void getVehiclesBehind() {
        if(intersectionsToPass.isEmpty()|| nextTurn == null){
            return;
        }
        vehiclesBehind = 0;
        LaneDirection direction = map.getlinedirection(position);
        Intersection intersection = this.intersectionsToPass.getFirst();
        for (Vehicle vehicle : localnetwork.getVehicles()) {
            LaneDirection otherDirection = map.getlinedirection(vehicle.position);
            if ( direction == otherDirection &&
                    distance(vehicle.position, intersection.getPos()) >
                            distance(this.position, intersection.getPos())) {
                vehiclesBehind++;
            }
        }
    }

    private int traffic_in_area(Intersection intersection)
    {
        int traffic = 0;
        for (Vehicle vehicle : simulationController.getVehicles())
        {
            if (!vehicle.intersectionsToPass.isEmpty() &&
                    vehicle.intersectionsToPass.getFirst().equals(intersection))
            {

                traffic++;
            }
        }
        return traffic;
    }

    public boolean is_street_blocked()
    {
        Point CurrentPos = this.position;
        LaneDirection direction = this.map.getlinedirection(CurrentPos);
        while (!map.isIntersection(CurrentPos) || CurrentPos.x>1 || CurrentPos.y>1 || CurrentPos.x<map.height || CurrentPos.y<map.width) {
            switch (direction) {
                case NORTH:
                    CurrentPos = new Point(CurrentPos.x + 1, CurrentPos.y);
                    break;
                case SOUTH:
                    CurrentPos = new Point(CurrentPos.x - 1, CurrentPos.y);
                    break;
                case EAST:
                    CurrentPos = new Point(CurrentPos.x, CurrentPos.y - 1);
                    break;
                case WEST:
                    CurrentPos = new Point(CurrentPos.x, CurrentPos.y + 1);
                    break;
                default:
                    return false;

            }
            if(!simulationController.isVehicleAt(CurrentPos)){
                return false;
            }
        }
        return true;
    }
}