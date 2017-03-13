package com.example.albergon.unirank.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class models one indicator about a specific performance field of an university. It stores a
 * Map which contains all associations university/score stored in the database. It also provides
 * methods to retrieve specific indicator information such as the score of single universities.
 */
public class Indicator {

    private Map<University, Double> entries = null;
    private final int id;

    /**
     * Public constructor that takes a Map associating universities to their score in the indicator.
     * A copy of this Map is stored. The indicator also stores an id, which is the index by which it
     * is identified in the IndicatorsList enumeration, and that can be used to retrieve information
     * about the indicator it refers to.
     *
     * @param entries   Map with university/score pairs
     * @param id        unique indicator index
     */
    public Indicator(Map<University, Double> entries, int id) {
        this.entries = new HashMap<>(entries);
        this.id = id;
    }

    /**
     * Retrieve the score of an university in this indicator or a default value if it is not present
     * in the ranking.
     *
     * @param university    university we wish to know the score in the indicator
     * @return              university's score or 0.0 if not present
     */
    // TODO: verify availability of call (API issues?)
    public double scoreOf(University university) {
        //noinspection Since15
        return entries.getOrDefault(university, 0.0);
    }

    /**
     * Getter for the indicator's index in the IndicatorsList enumeration.
     *
     * @return  unique indicator identifier
     */
    public int getId() {
        return id;
    }
}
