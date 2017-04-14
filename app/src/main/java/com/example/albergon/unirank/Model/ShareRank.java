package com.example.albergon.unirank.Model;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 14.04.2017.
 */
public class ShareRank {

    public String name = null;
    public String date = null;
    public ArrayList<Integer> ranking = null;
    public HashMap<String, Integer> settings = null;

    // required by firebase
    public ShareRank() {

    }

    public ShareRank(String name, String date, List<Integer> ranking, Map<Integer, Integer> settings) {

        //TODO: args check

        this.name = name;
        this.date = date;
        this.ranking = new ArrayList<>(ranking);
        this.settings = processSettings(settings);
    }

    private HashMap<String,Integer> processSettings(Map<Integer, Integer> settings) {

        HashMap<String, Integer> stringSettings = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : settings.entrySet()) {
            stringSettings.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        return stringSettings;
    }

}
