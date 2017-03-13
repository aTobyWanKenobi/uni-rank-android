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

    private Tables() {};

    /**
     * This nested static class models the table containing university information. Inheriting from
     * BaseColumns ensure it has the _ID attribute commonly used in android SQLite databases.
     */
    public static class UniversitiesTable implements BaseColumns {
        public static final String TABLE_NAME = "UNIVERSITIES";
        public static final String UNI_NAME = "Name";
        public static final String COUNTRY = "Country";
        public static final String ACRONYM = "Acronym";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    /**
     * This nested enumeration contains a list of the indicators currently present in the database.
     * Since every indicator table has the same structure, its field names can be accessed from the
     * enumeration.
     */
    public enum IndicatorsList implements BaseColumns {

        AcademicReputation("ACADEMIC_REPUTATION"),
        EmployerReputation("EMPLOYER_REPUTATION"),
        StudentStaffRatio("STUDENT_STAFF_RATIO"),
        InternationalOutlook("INTERNATIONAL_OUTLOOK"),
        Citations("CITATIONS");

        public final String TABLE_NAME;
        public final static String SCORE = "Score";

        IndicatorsList(final String name) {
            TABLE_NAME = name;
        }

        public String deleteEntriesQuery() {
            return "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }
}

