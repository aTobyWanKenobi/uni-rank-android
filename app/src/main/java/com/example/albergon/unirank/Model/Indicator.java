package com.example.albergon.unirank.Model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This immutable class models one indicator about a specific performance field of an university.
 * It stores a Map which contains all associations university id/score stored in the database. It also
 * provides methods to retrieve specific indicator information such as the score of single universities.
 */
public class Indicator {

    private Map<Integer, Double> entries = null;
    private final int id;

    /**
     * Public constructor that takes a Map associating universities ids to their score in the indicator.
     * A copy of this Map is stored. The indicator also stores an id, which is the index by which it
     * is identified in the IndicatorsList enumeration, and that can be used to retrieve information
     * about the indicator it refers to.
     *
     * @param entries   Map with id/score pairs
     * @param id        unique indicator index
     */
    @SuppressLint("UseSparseArrays")
    public Indicator(Map<Integer, Double> entries, int id) {

        //TODO: check if score is normalized between 0-100?? Or do it in another place?

        // arguments check
        if(entries == null) {
            throw new IllegalArgumentException("Entries map in indicator cannot be null");
        } else if(id < 0) {
            throw new IllegalArgumentException("Indicator index cannot be negative");
        }

        this.entries = new HashMap<>(entries);
        this.id = id;
    }

    /**
     * Retrieve the score of an university in this indicator or a default value if it is not present
     * in the ranking.
     *
     * @param universityId  university we wish to know the score in the indicator
     * @return              university's score or 0.0 if not present
     */
    public double scoreOf(Integer universityId) {

        // arguments check
        if(universityId < 0) {
            throw new IllegalArgumentException("University id cannot be negative");
        }

        Double score = entries.get(universityId);
        return (score == null)?0.0:score;
    }

    /**
     * Getter for the indicator's index in the IndicatorsList enumeration.
     *
     * @return  unique indicator identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Getter for the number of entries ranked by the indicator.
     *
     * @return  number of universities present in this indicator
     */
    public int getSize() {
        return entries.size();
    }

    /**
     * Getter for the id set of the universities ranked in this indicator.
     *
     * @return set of universities id on the indicator
     */
    public Set<Integer> getIdSet() {

        return entries.keySet();
    }

    /**
     * Getter for the ranking induced by this indicator.
     *
     * @return  ranking of the universities present in this indicator
     */
    public Ranking<Integer> getRanking() {

        List<Integer> ranking = new ArrayList<>(getIdSet());
        ranking.sort(new Ranking.UniIdRankComparator(entries));

        return new Ranking<>(ranking);
    }
}
