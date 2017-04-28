package com.example.albergon.unirank.Model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models the set of settings present in the local database and is used to store them in
 * a unique object
 */
public class Settings {

    private String country = null;
    private Enums.GenderEnum gender = null;
    public int yearOfBirth = 0;
    public Enums.TypesOfUsers type = null;

    public Settings(String country, Enums.GenderEnum gender, int year, Enums.TypesOfUsers type) {

        // arguments validation
        if(country == null || gender == null || type == null) {
            throw new IllegalArgumentException("Arguments for Settings cannot be null");
        } else if(country.length() < 3 || country.length() > 3 || !countryCodes.contains(country)) {
            throw new IllegalArgumentException("Country has to be one of application's country codes");
        } else if(!gender.toString().equals("Male") && !gender.toString().equals("Female")) {
            throw new IllegalArgumentException("Gender has to be either Male or Female");
        } else if(year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR)) {
            throw new IllegalArgumentException("Year of birth not plausible");
        }

        this.country = country;
        this.gender = gender;
        this.type = type;
        yearOfBirth = year;

    }

    public String getCountryCode() {
        return country;
    }

    public String getCountryName() {
        return countryMap.get(country);
    }

    public Enums.GenderEnum getGender() {
        return gender;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public Enums.TypesOfUsers getType() {
        return type;
    }

    private static final Map<String, String> countryMap = createCountryMap();

    private static Map<String, String> createCountryMap() {

        Map<String, String> codeToName = new HashMap<>();
        // TODO: insert all country mappings
        // TODO: create country codes list

        return codeToName;
    }

    public static final List<String> countryCodes = Arrays.asList("Tes");
}


