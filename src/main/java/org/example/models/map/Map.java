package org.example.models.map;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {


    private final int [][] grille;
    private final int width;
    private final int height;

    private List<Intersection> intersections = new ArrayList<>();


    public Map(int x, int y) {
        this.width = y;
        this.height = x;
        this.grille = new int [x][y];
        //init all -1

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                this.grille[i][j] = 0;
            }
        }

    }


    /**
     * Print the map for visualization
     */
    public void Print_Map(){
        for (int i = 0; i < this.width; i++) {
            for (int j = 0; j < this.height; j++) {
                System.out.print(STR."\{this.grille[i][j]} ");
            }
            System.out.println();
        }
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
     * Set the intersections in the grid
     * @param n Number of intersections to set
     * @throws Exception
     */
    public void setIntersections(int n) throws Exception {
        for (int i = 0; i < n; i++) {
            //adds a new intersection in random positon buffers from edges by 1 with a random type
            Random random = new Random();
            int x = random.nextInt(this.height - 4) + 2;
            int y = random.nextInt(this.width - 4) + 2;

            if (intersectionExists(x, y)) {
                i--;
                continue;
            }



            Point p = new Point(x, y);

            IntersectionType type = IntersectionType.values()[(random.nextInt(3))];
            Add_Intersection(new Intersection(p.x,p.y,type));

            set_valide(intersections.getLast());
        }
    }

    /**
     * Set the roads in the grid
     * @param intersection Intersection to set
     * @throws Exception
     */
    private void set_valide(Intersection intersection) throws Exception {
        Point p = intersection.getPos();

        switch (intersection.getType()){
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
                setVertical_all(p);
                setHorizontal_east(p);
                break;
            case THREE_WAY_W:
                setVertical_all(p);
                setHorizontal_west(p);
                break;
            default:
                throw new Exception("Invalid Intersection Type");

        }
    }

    //if the intersection dont lack vertical road
    private void setVertical_north (Point p) throws Exception {
        for (int i = p.x; i<this.height;i++){
            setVerticalP(p, i);
        }
    }
    //if the intersection lacks a road to the south
    private void setVertical_south (Point p) throws Exception {
        for (int i = 0; i<p.x;i++){
            setVerticalP(p, i);
        }
    }
    //if the intersection lacks a road to the north
    private void setVertical_all(Point p) throws Exception {
        for (int i = 0; i < this.height; i++) {
            setVerticalP(p, i);
        }
    }

    /**
     * sets the vertical road in the grid
     */
    private void setVerticalP(Point p, int i) throws Exception {
        if (this.grille[i][p.y] == 0) {
            this.grille[i][p.y] = 1;
            this.grille[i][p.y + 1] = 1;
        } else if (this.grille[i][p.y] == 1) {
            Add_Intersection(new Intersection(i,p.y, IntersectionType.FOUR_WAY));
        }
    }



    //if the intersection doesnt lack horizontal road
    private void setHorizontal_all(Point p) throws Exception {
        for (int j = 0; j < this.width; j++) {
            setHorizontalP(p, j);
        }
    }
    //if the intersection lacks a road to the east
    private void setHorizontal_east (Point p) throws Exception {
        for (int j = 0; j<p.y;j++){
            setHorizontalP(p, j);
        }
    }
    //if the intersection lacks a road to the west
    private void setHorizontal_west (Point p) throws Exception {
        for (int j = p.y; j<this.width;j++){
            setHorizontalP(p, j);
        }
    }

    /**
     * sets the horizontal road in the grid
     */
    private void setHorizontalP(Point p, int j) throws Exception {
        if (this.grille[p.x][j] == 0) {
            this.grille[p.x][j] = 1;
            this.grille[p.x+1][j] = 1;
        } else if (this.grille[p.x][j] == 1) {
            Add_Intersection(new Intersection(p.x,j, IntersectionType.FOUR_WAY));
        }
    }


    /**
     * Add the intersection to the list and update the grid
     * @param i Intersection to add
     */
    private void Add_Intersection(Intersection i) throws Exception {
        System.out.println("WTF x: " + i.getPos().x + ", y: " + i.getPos().y + ", type: " + i.getType());
        this.intersections.add(i);
        this.grille[i.getPos().x][i.getPos().y] = 2;
        this.grille[i.getPos().x+1][i.getPos().y] = 2;
        this.grille[i.getPos().x][i.getPos().y+1] = 2;
        this.grille[i.getPos().x+1][i.getPos().y+1] = 2;
        //checks to valid if edges are not intersection
        Exception e = new Exception(STR."Invalid Map Intersection at \{intersections.getLast().getPos() }");
        for (int j = 0; j < this.width; j++) {
            if (this.grille[0][j] == 2) {
                throw e;
            }
            if (this.grille[this.height-1][j] == 2) {
                throw e;
            }
        }
        for (int j = 0; j < this.height; j++) {
            if (this.grille[j][0] == 2) {
                throw e;
            }
            if (this.grille[j][this.width-1] == 2) {
                throw e;
            }
        }
    }
}
