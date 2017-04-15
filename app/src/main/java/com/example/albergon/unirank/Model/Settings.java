package com.example.albergon.unirank.Model;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class models the set of settings present in the local database and is used to store them in
 * a unique object
 */
public class Settings {

    private String country = null;
    private String gender = null;
    public int yearOfBirth = 0;
    public TypesOfUsers type = null;

    public Settings(String country, String gender, int year, TypesOfUsers type) {

        // arguments validation
        if(country == null || gender == null || type == null) {
            throw new IllegalArgumentException("Arguments for Settings cannot be null");
        } else if(country.length() < 3 || country.length() > 3 || !countryCodesSet.contains(country)) {
            throw new IllegalArgumentException("Country has to be one of application's country codes");
        } else if(!gender.equals("Male") && !gender.equals("Female")) {
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

    public String getGender() {
        return gender;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public TypesOfUsers getType() {
        return type;
    }

    public enum TypesOfUsers {
        HighSchoolStudent("High school student"),
        UniversityStudent("University student"),
        Parent("Parent"),
        NoType("No type");

        private String STRING_REP = null;

        public String toString() {
            return STRING_REP;
        }

        private TypesOfUsers(String toString) {
            STRING_REP = toString;
        }
    }

    private static final Map<String, String> countryMap = createCountryMap();

    private static Map<String, String> createCountryMap() {

        Map<String, String> codeToName = new HashMap<>();
        // TODO: insert all country mappings
        // TODO: create country codes list

        return codeToName;
    }

    private static final String[] countryCodesArray = {"Tes"};
    private static final Set<String> countryCodesSet = new HashSet<>(Arrays.asList(countryCodesArray));
}


