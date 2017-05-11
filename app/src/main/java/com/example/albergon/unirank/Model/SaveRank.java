package com.example.albergon.unirank.Model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models a performed aggregation which is intended to be stored locally. It stores the
 * name of the save, the date in which it was last updated, the settings of the aggregation and its
 * result, to avoid computing it again.
 */
public class SaveRank {

    private String name = null;
    private String date = null;
    private Map<Integer, Integer> settings = null;
    private Ranking<Integer> result = null;

    /**
     * Public constructor that encapsulates the arguments.
     *
     * @param name      name of the save
     * @param date      date of save or update
     * @param settings  indicators and respective weight map
     * @param result    aggregation result, list of university ids
     */
    public SaveRank(String name, String date, Map<Integer, Integer> settings, Ranking<Integer> result) {

        // arguments check
        if(name == null || date == null || settings == null || result == null) {
            throw new IllegalArgumentException("Cannot have null arguments in SaveRank");
        } else if(name.isEmpty() || date.isEmpty() || settings.isEmpty()) {
            throw new IllegalArgumentException("Cannot have empty arguments in SaveRank");
        }

        this.name = name;
        this.date = date;
        this.settings = settings;
        this.result = result;
    }

    /**
     * Getter for the name attribute.
     *
     * @return  name of the aggregation
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the date attribute.
     *
     * @return  date in which the aggregation was saved or last updated
     */
    public String getDate() {
        return date;
    }

    /**
     * Getter for the settings attribute.
     *
     * @return  mapping from indicators present in the aggregation and their weights
     */
    @SuppressLint("UseSparseArrays")
    public Map<Integer, Integer> getSettings() {

        return new HashMap<>(settings);
    }

    /**
     * Getter for the result attribute list.
     *
     * @return  result of the aggregation as a sorted list of university ids
     */
    public List<Integer> getResultList() {
        return result.getList();
    }

    /**
     * Getter for the result scores.
     *
     * @return  map of university ids and scores
     */
    public Map<Integer, Double> getResultScores() {
        return result.getScores();
    }

}
