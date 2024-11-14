package org.example;


import org.example.models.map.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Map map = new Map(10,10);
        map.setIntersections(1);
        map.Print_Map();
    }
}