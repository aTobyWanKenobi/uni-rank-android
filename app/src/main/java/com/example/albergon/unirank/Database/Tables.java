package com.example.albergon.unirank.Database;

import android.provider.BaseColumns;

/**
 * Created by Tobia Albergoni on 12.03.2017.
 */
public final class Tables {

    private Tables() {};

    public static class UniversitiesTable implements BaseColumns {
        public static final String TABLE_NAME = "UNIVERSITIES";
        public static final String UNI_NAME = "Name";
        public static final String COUNTRY = "Country";
        public static final String ACRONYM = "Acronym";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static class IndicatorTable implements BaseColumns {

        public enum IndicatorsIndex {
            AcademicReputation("ACADEMIC_REPUTATION");

            private final String nodeName;

            IndicatorsIndex(final String nodeName) {
                this.nodeName = nodeName;
            }

            @Override
            public String toString() {
                return nodeName;
            }
        }

        public static final String SCORE = "Score";

    }
}
