package com.example.albergon.unirank.Database;

import com.example.albergon.unirank.Indicator;
import com.example.albergon.unirank.University;

/**
 * This interface specifies the access methods to the local university and indicators database. It
 * has to be implemented by database helpers to ensure the interface with whatever SQLite implementation
 * is currently deployed stays the same for the rest of the application.
 */
public interface UniRankDatabase {


    University getUniversity(int id);

    Indicator getIndicator(int id);

}
