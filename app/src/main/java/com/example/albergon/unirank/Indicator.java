package com.example.albergon.unirank;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Indicator {

    private Map<University, Double> entries = null;
    private final int id;

    public Indicator(Map<University, Double> entries, int id) {
        this.entries = new HashMap<>(entries);
        this.id = id;
    }

    // TODO: verify availability of call
    public double scoreOf(University university) {
        //noinspection Since15
        return entries.getOrDefault(university, 0.0);
    }

    public int getId() {
        return id;
    }
}
