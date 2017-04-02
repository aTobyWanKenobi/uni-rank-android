package com.example.albergon.unirank.Model;

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
    private Aggregator settings = null;
    private Ranking<Integer> result = null;

    public SaveRank(String name, String date, Aggregator aggregator, Ranking<Integer> result) {
        this.name = name;
        this.date = date;
        settings = aggregator;
        this.result = result;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Map<Integer, Integer> getSettings() {
        return settings.getSettings();
    }

    public List<Integer> getResult() {
        return result.getList();
    }
}
