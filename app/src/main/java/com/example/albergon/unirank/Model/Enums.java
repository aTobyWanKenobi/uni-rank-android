package com.example.albergon.unirank.Model;

/**
 * Created by Tobia Albergoni on 28.04.2017.
 */

public class Enums {

    private Enums() {}

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

    public enum TypesOfUsers {
        HighSchoolStudent("High school student"),
        UniversityStudent("University student"),
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


}
