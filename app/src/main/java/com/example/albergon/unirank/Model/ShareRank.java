package com.example.albergon.unirank.Model;

import com.example.albergon.unirank.Database.Tables;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 14.04.2017.
 */
public class ShareRank {

    // aggregation fields
    public String date = null;
    public ArrayList<Integer> ranking = null;
    public HashMap<String, Integer> settings = null;

    //settings fields
    public String gender = null;
    public String country = null;
    public int birthYear = 0;
    public String userType = null;

    // required by firebase
    public ShareRank() {

    }

    public ShareRank(String date, List<Integer> ranking, Map<Integer, Integer> settings, Settings userSettings) {

        //TODO: args check

        this.date = date;
        this.ranking = new ArrayList<>(ranking);
        this.settings = processSettings(settings);

        gender = userSettings.getGender();
        country = userSettings.getCountryCode();
        birthYear = userSettings.getYearOfBirth();
        userType = userSettings.getType().toString();
    }

    private HashMap<String,Integer> processSettings(Map<Integer, Integer> settings) {

        HashMap<String, Integer> stringSettings = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : settings.entrySet()) {
            stringSettings.put(String.valueOf(entry.getKey()), entry.getValue());
        }

        return stringSettings;
    }

}
