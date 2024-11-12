package org.example.models.map;


import java.awt.*;
import java.util.List;

public class Map {


    private final int [][] grille;
    private final int width;
    private final int height;

    private List<Intersection> intersections;


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

    public void Print_Map(){
        for (int i = 0; i < this.grille.length; i++) {
            for (int j = 0; j < this.grille[i].length; j++) {
                System.out.print(this.grille[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void setIntersections(int n){
        for (int i = 0; i < n ; i++) {

            //call to a function that sets intersection
            Intersection intersection = new Intersection(height/2,width/2,IntersectionType.FOUR_WAY);
            set_valide(intersection.getPos());
        }


    }


    private void set_valide(Point p){
        for (int i = 0; i < this.height; i++) {
           if (this.grille[i][p.y] == 0) {

               this.grille[i][p.y] = 1;
               this.grille[i][p.y + 1] = 1;
           } else if (this.grille[i][p.y] == 1) {
               Intersection newI  = new Intersection(i,p.y,IntersectionType.FOUR_WAY);
           }
        }
        for (int j = 0; j < this.width; j++) {
            if (this.grille[p.x][j] == 0) {
                this.grille[p.x][j] = 1;
                this.grille[p.x+1][j] = 1;
            } else if (this.grille[p.x][j] == 1) {
                Intersection newI = new Intersection(p.x,j, IntersectionType.FOUR_WAY);
            }
        }
    }
}
