package com.example.albergon.unirank.Model;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 02.04.2017.
 */

public class SaveRank {

    private String name = null;
    private String date = null;
    private Map<Integer, Integer> settings = null;
    private List<Integer> result = null;
    private int id = -1;

    public SaveRank(String name, String date, Map<Integer, Integer> settings, List<Integer> result) {

        // TODO: argument check
        this.name = name;
        this.date = date;
        this.settings = settings;
        this.result = new ArrayList<>(result);
    }

    public SaveRank(String name, String date, Map<Integer, Integer> settings, List<Integer> result, int id) {
        this(name, date, settings, result);
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Map<Integer, Integer> getSettings() {

        return new HashMap<>(settings);
    }

    public List<Integer> getResult() {
        return new ArrayList<>(result);
    }

    public int getId() {
        return id;
    }
}
