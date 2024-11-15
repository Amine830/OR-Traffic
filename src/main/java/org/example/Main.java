package org.example;


import org.example.models.map.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        Map map = new Map(120,120);
        map.setIntersections(12);
        map.Print_Map();
    }
}