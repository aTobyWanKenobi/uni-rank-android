package com.example.albergon.unirank.Model;

/**
 * This not instantiable container class contains many of the enumerations used through the application
 */
public class Enums {

    // private constructor
    private Enums() {}

    /**
     * This enumeration models the user characteristics used to differentiate aggregations.
     */
    public enum PopularIndicatorsCategories {
        GENDER("Gender"),
        TYPE("User type"),
        BIRTHYEAR("User age"),
        TIMEFRAME("Upload date"),
        COUNTRY("Country");

        private String TO_STRING;

        PopularIndicatorsCategories(final String toString) {
            TO_STRING = toString;
        }

        public String toString() {
            return TO_STRING;
        }
    }

    /**
     * This enumeration models genders in UniRank.
     */
    public enum GenderEnum {
        MALE("Male"),
        FEMALE("Female");

        private String TO_STRING;

        GenderEnum(final String toString) {
            TO_STRING = toString;
        }

        public String toString() {
            return TO_STRING;
        }
    }

    /**
     * This enumeration models user types in UniRank.
     */
    public enum TypesOfUsers {
        HighSchoolStudent("High School"),
        UniversityStudent("University"),
        Parent("Parent"),
        NoType("No type");

        private String TO_STRING = null;

        TypesOfUsers(String toString) {
            TO_STRING = toString;
        }

        public String toString() {
            return TO_STRING;
        }
    }

    /**
     * This enumeration models time intervals used to identify recent indicator trends.
     */
    public enum TimeFrame {
        MONTH("This month"),
        YEAR("This year");

        private String TO_STRING = null;

        TimeFrame(String toString) {
            TO_STRING = toString;
        }

        public String toString() {
            return TO_STRING;
        }
    }

    /**
     * This enumeration models age ranges.
     */
    public enum UserAgeCategories {
        KIDS("Less than 15 years old"),
        TEENS("15-20 years old"),
        YOUNGS("20-27 years old"),
        ADULTS("27-50 years old"),
        OLD("More than 50 years old");

        private String TO_STRING = null;

        UserAgeCategories(String toString) {
            TO_STRING = toString;
        }

        public String toString() {
            return TO_STRING;
        }
    }

    /**
     * This enumeration lists month abbreviation in order to easily manipulate Firebase dates.
     */
    public enum MonthsAbbreviations {
        JANUARY("Jan"),
        FEBRUARY("Feb"),
        MARCH("Mar"),
        APRIL("Apr"),
        MAY("May"),
        JUNE("Jun"),
        JULY("Jul"),
        AUGUST("Aug"),
        SEPTEMBER("Sep"),
        OCTOBER("Oct"),
        NOVEMBER("Nov"),
        DECEMBER("Dec");

        private String TO_STRING = null;

        MonthsAbbreviations(String toString) {
            TO_STRING = toString;
        }

        public String toString() {
            return TO_STRING;
        }
    }


}
