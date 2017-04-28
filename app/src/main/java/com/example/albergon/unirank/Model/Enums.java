package com.example.albergon.unirank.Model;

/**
 * Created by Tobia Albergoni on 28.04.2017.
 */

public class Enums {

    private Enums() {}

    public enum PopularIndicatorsCategories {
        GENDER, TYPE, BIRTHYEAR;
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


}
