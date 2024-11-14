package org.example.models.map;


import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        for (int i = 0; i < this.grille.length; i++) {
            for (int j = 0; j < this.grille[i].length; j++) {
                System.out.print(STR."\{this.grille[i][j]} ");
            }
            System.out.println();
        }
    }

    /**
     * Set the intersections in the grid
     * @param n Number of intersections to set
     * @throws Exception
     */
    public void setIntersections(int n) throws Exception {


            //call to a function that sets intersection
            Intersection intersection = new Intersection(2,2,IntersectionType.FOUR_WAY);
            Add_Intersection(intersection);
            set_valide(intersection);
            Intersection intersection2 = new Intersection(8,8,IntersectionType.THREE_WAY_N);
            Add_Intersection(intersection2);
            set_valide(intersection2);

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
            case THREE_WAY_E:
                setVertical_all(p);
                setHorizontal_east(p);
            case THREE_WAY_W:
                setVertical_all(p);
                setHorizontal_west(p);
            default:
                throw new Exception("Invalid Intersection Type");

        }
    }

    //if the intersection dont lack vertical road
    private void setVertical_north (Point p){
        for (int i = p.x; i<this.height;i++){
            setVerticalP(p, i);
        }
    }
    //if the intersection lacks a road to the south
    private void setVertical_south (Point p){
        for (int i = 0; i<p.x;i++){
            setVerticalP(p, i);
        }
    }
    //if the intersection lacks a road to the north
    private void setVertical_all(Point p){
        for (int i = 0; i < this.height; i++) {
            setVerticalP(p, i);
        }
    }

    /**
     * sets the vertical road in the grid
     */
    private void setVerticalP(Point p, int i) {
        if (this.grille[i][p.y] == 0) {
            this.grille[i][p.y] = 1;
            this.grille[i][p.y + 1] = 1;
        } else if (this.grille[i][p.y] == 1) {
            Add_Intersection(new Intersection(i,p.y, IntersectionType.FOUR_WAY));
        }
    }



    //if the intersection doesnt lack horizontal road
    private void setHorizontal_all(Point p){
        for (int j = 0; j < this.width; j++) {
            setHorizontalP(p, j);
        }
    }
    //if the intersection lacks a road to the east
    private void setHorizontal_east (Point p){
        for (int j = 0; j<p.y;j++){
            setHorizontalP(p, j);
        }
    }
    //if the intersection lacks a road to the west
    private void setHorizontal_west (Point p){
        for (int j = p.y; j<this.width;j++){
            setHorizontalP(p, j);
        }
    }

    /**
     * sets the horizontal road in the grid
     */
    private void setHorizontalP(Point p, int j) {
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
    private void Add_Intersection(Intersection i){
        this.intersections.add(i);
        this.grille[i.getPos().x][i.getPos().y] = 2;
        this.grille[i.getPos().x+1][i.getPos().y] = 2;
        this.grille[i.getPos().x][i.getPos().y+1] = 2;
        this.grille[i.getPos().x+1][i.getPos().y+1] = 2;
    }
}
