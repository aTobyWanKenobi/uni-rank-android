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

    public static class SavedRankingsTable implements BaseColumns {
        public static final String TABLE_NAME = "Saved_Rankings";
        public static final String RANKING_NAME = "Name";
        public static final String RANKING_DATE = "Date";
    }

    public static class SavedAggregationsTable implements BaseColumns {
        public static final String TABLE_NAME = "Saved_Aggregations";
        public static final String SAVED_ID = "SavedID";
        public static final String SAVED_INDICATOR = "Indicator";
        public static final String SAVED_WEIGHT = "Weight";
    }

    public static class SavedRankListTable implements BaseColumns {
        public static final String TABLE_NAME = "Saved_RankList";
        public static final String SAVED_RANK = "Rank";
        public static final String SAVED_UNI_ID = "UniID";
        public static final String SAVED_RANKING_ID = "RankingID";
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
        public final static String SCORE = "Score";

        IndicatorsList(final String name) {
            TABLE_NAME = name;
        }
    }


}

