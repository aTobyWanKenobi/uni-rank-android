package com.example.albergon.unirank.Database;

import android.provider.BaseColumns;

/**
 * This class contains the current relational database structure, such as table and column names,
 * delete queries. Indicators tables are represented in a nested enumeration which is easily
 * extended with new indicators.
 *
 * The constructor of the class is private to ensure it won't be instantiated.
 */
public final class Tables {

    private Tables() {}

    /**
     * This nested static class models the table containing university information. Inheriting from
     * BaseColumns ensure it has the _ID attribute commonly used in android SQLite databases.
     */
    public static class UniversitiesTable implements BaseColumns {
        public static final String TABLE_NAME = "Universities";
        public static final String UNI_NAME = "Name";
        public static final String COUNTRY = "Country";
        public static final String ACRONYM = "Acronym";
    }

    /**
     * This nested static class models the table containing information about saved aggregations.
     * In particular it stores names and dates. Inheriting from BaseColumns ensure it has the _ID
     * attribute commonly used in android SQLite databases.
     */
    public static class Saves implements BaseColumns {
        public static final String TABLE_NAME = "Saves";
        public static final String RANKING_NAME = "Name";
        public static final String RANKING_DATE = "Date";
    }

    /**
     * This nested static class models the table containing information about saved aggregations.
     * In particular it stores the indicator and weight settings for all saves. Inheriting from
     * BaseColumns ensure it has the _ID attribute commonly used in android SQLite databases.
     */
    public static class SavesSettings implements BaseColumns {
        public static final String TABLE_NAME = "Saves_Settings";
        public static final String SAVED_NAME = "SaveName";
        public static final String SAVED_INDICATOR = "Indicator";
        public static final String SAVED_WEIGHT = "Weight";
    }

    /**
     * This nested static class models the table containing information about saved aggregations.
     * In particular it stores the positions of universities in each save. Inheriting from
     * BaseColumns ensure it has the _ID attribute commonly used in android SQLite databases.
     */
    public static class SavesRankings implements BaseColumns {
        public static final String TABLE_NAME = "Saves_Rankings";
        public static final String SAVED_RANK = "Rank";
        public static final String SAVED_UNI_ID = "UniID";
        public static final String SAVED_NAME = "SaveName";
    }

    /**
     * This nested static class models the table Settings, where some information about the user of
     * the application is stored, in order to attach meaningful fields to every ranking he shares and
     * create useful query possibilities on the common shared pool of aggregations.
     */
    public static class Settings implements BaseColumns {
        public static final String TABLE_NAME = "Settings";
        public static final String BIRTH_YEAR = "BirthYear";
        public static final String COUNTRY = "Country";
        public static final String GENDER = "Gender";
        public static final String USER_TYPE = "UserType";
    }

    /**
     * This nested enumeration contains a list of the indicators currently present in the database.
     * Since every indicator table has the same structure, its field names can be accessed from the
     * enumeration.
     */
    public enum IndicatorsList implements BaseColumns {

        AcademicReputation("Academic_Reputation"),
        EmployerReputation("Employer_Reputation"),
        StudentStaffRatio("Student_Staff_Ratio"),
        InternationalOutlook("International_Outlook"),
        Citations("Citations");

        public final String TABLE_NAME;
        public final String NAME;
        public final static String SCORE = "Score";

        IndicatorsList(final String name) {
            TABLE_NAME = name;
            NAME = removeUnderscores(name);
        }
    }

    private static String removeUnderscores(String s) {
        return s.replace("_", " ");
    }


}

