package com.example.albergon.unirank.Model;

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
    private List<Integer> result = null;
    private int id = -1;

    /**
     * Public constructor that encapsulates the arguments.
     *
     * @param name      name of the save
     * @param date      date of save or update
     * @param settings  indicators and respective weight map
     * @param result    aggregation result, list of university ids
     */
    public SaveRank(String name, String date, Map<Integer, Integer> settings, List<Integer> result) {

        // arguments check
        if(name == null || date == null || settings == null || result == null) {
            throw new IllegalArgumentException("Cannot have null arguments in SaveRank");
        } else if(name.isEmpty() || date.isEmpty() || settings.isEmpty() || result.isEmpty()) {
            throw new IllegalArgumentException("Cannot have empty arguments in SaveRank");
        }

        this.name = name;
        this.date = date;
        this.settings = settings;
        this.result = new ArrayList<>(result);
    }

    /**
     * Public constructor that additionally takes the id of the save in the database. Useful to perform
     * updates to saves and delete old data.
     */
    public SaveRank(String name, String date, Map<Integer, Integer> settings, List<Integer> result, int id) {
        this(name, date, settings, result);

        // arguments check
        if(id < 0) {
            throw new IllegalArgumentException("Id of saved aggregation in database cannot be negative");
        }

        this.id = id;
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
    public Map<Integer, Integer> getSettings() {

        return new HashMap<>(settings);
    }

    /**
     * Getter for the result attribute.
     *
     * @return  result of the aggregation as a sorted list of university ids
     */
    public List<Integer> getResult() {
        return new ArrayList<>(result);
    }

    /**
     * Getter of the id attribute. If this is a new save, it's -1 by default.
     *
     * @return      id of aggregation in database if update, -1 if new save
     */
    public int getId() {
        return id;
    }
}
