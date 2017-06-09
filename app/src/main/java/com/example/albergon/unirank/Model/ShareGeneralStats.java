package com.example.albergon.unirank.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * This class models the set of statistics that firebase dynamically updates each time an aggregation
 * is uploaded. It is useful to have it modeled as a class conforming to firebase standards because
 * it allows to retrieve all current statistics at once, modify them and then upload them again.
 */
public class ShareGeneralStats {

    // public firebase fields
    public int byMale = 0;
    public int byFemale = 0;
    public int byHighSchoolStudents = 0;
    public int byUniversityStudents = 0;
    public int byParents = 0;
    public int byOtherType = 0;

    public Map<String, Integer> byCountry = new HashMap<>();
    public Map<String, Integer> byBirthYear = new HashMap<>();
    public Map<String, Integer> byMonth = new HashMap<>();

    public int totalCount = 0;

    // required by firebase
    public ShareGeneralStats() {

    }

    /**
     * This method takes as arguments the parameters of the new upload and updates an existing
     * statistics object according to them.
     */
    public void update(Enums.GenderEnum gender,
                       Enums.TypesOfUsers type,
                       int birthYear,
                       String date,
                       String country) {

        // arguments check
        if(gender == null || type == null || date == null || country == null) {
            throw new IllegalArgumentException("Arguments for ShareGeneralStats update cannot be null");
        } else if(date.isEmpty() || country.isEmpty() || !Countries.countryMap.keySet().contains(country) || birthYear < 0) {
            throw new IllegalArgumentException("Illegal format or content of update argument");
        }

        // update gender count
        switch(gender) {
            case MALE:
                byMale++;
                break;
            case FEMALE:
                byFemale++;
                break;
            default:
                throw new IllegalStateException("Unknown element in enum GenderEnum");
        }

        // update type count
        switch(type) {
            case HighSchoolStudent:
                byHighSchoolStudents++;
                break;
            case UniversityStudent:
                byUniversityStudents++;
                break;
            case Parent:
                byParents++;
                break;
            case NoType:
                byOtherType++;
                break;
            default:
                throw new IllegalStateException("Unknown element in enum TypeOfUsers");
        }

        // update birthYear count
        int birthYearCount = byBirthYear.getOrDefault("year" + birthYear, 0);
        byBirthYear.put("year" + birthYear, ++birthYearCount);

        // update month count
        String month = date.split(" ", 2)[1];
        int monthCount = byMonth.getOrDefault(month, 0);
        byMonth.put(month, ++monthCount);

        // update country count
        int countryCount = byCountry.getOrDefault(country, 0);
        byCountry.put(country, ++countryCount);

        // update total count
        totalCount++;
    }

}
