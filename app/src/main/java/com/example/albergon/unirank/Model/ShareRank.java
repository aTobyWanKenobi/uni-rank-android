package com.example.albergon.unirank.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models an aggregation intended to be shared in the common pool. It has an "unsafe"
 * structure with public attributes to allow for direct upload on firebase. This class eases the
 * interaction with the remote firebase instance by being able to be uploaded as a Java object and
 * it's not needed to break it down to insert it in remote database.
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

    /**
     * This public constructor initializes the public attributes to construct a ShareRank that is ready to
     * be uploaded to firebase.
     *
     * @param date          date of sharing
     * @param ranking       result of aggregation
     * @param settings      settings of aggregation
     * @param userSettings  user settings in the application
     */
    public ShareRank(String date, List<Integer> ranking, Map<Integer, Integer> settings, Settings userSettings) {

        if(date == null || ranking == null || settings == null || userSettings == null) {
            throw new IllegalArgumentException("Arguments for ShareRank cannot be null");
        } else if(ranking.isEmpty() || settings.isEmpty()) {
            throw new IllegalArgumentException("Arguments for ShareRank cannot be empty");
        }

        this.date = date;
        this.ranking = new ArrayList<>(ranking);
        this.settings = processSettings(settings);

        gender = userSettings.getGender().toString();
        country = userSettings.getCountryCode();
        birthYear = userSettings.getYearOfBirth();
        userType = userSettings.getType().toString();
    }

    /**
     * This method transforms the settings integer map used in the application in a string to integer
     * mapping suitable for firebase upload, which does not accept other types as keys.
     *
     * @param settings  aggregation settings
     * @return          settings in firebase format
     */
    private HashMap<String,Integer> processSettings(Map<Integer, Integer> settings) {

        // arguments check
        if(settings == null) {
            throw new IllegalArgumentException("Settings to transform in firebase format cannot be null");
        }

        HashMap<String, Integer> stringSettings = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : settings.entrySet()) {
            // add "rank" to string representation of rank or firebase cannot serialize map
            stringSettings.put("ind"+String.valueOf(entry.getKey()), entry.getValue());
        }

        return stringSettings;
    }

    /**
     * This method retrieves a correct settings map for an aggregation starting from a firebase
     * retrieved one, which contains strings keys with "ind" appended.
     *
     * @param firebaseSettings  settings in firebase map format
     * @return                  a settings map of integers
     */
    public static Map<Integer, Integer> reprocessSettings(Map<String, Integer> firebaseSettings) {

        // arguments check
        if(firebaseSettings == null) {
            throw new IllegalArgumentException("Cannot convert null settings from firebase");
        }

        Map<Integer, Integer> settings = new HashMap<>();
        for(Map.Entry<String, Integer> entry : firebaseSettings.entrySet()) {
            int indicator = Integer.parseInt(entry.getKey().substring(3));
            settings.put(indicator, entry.getValue());
        }

        return settings;
    }

}
