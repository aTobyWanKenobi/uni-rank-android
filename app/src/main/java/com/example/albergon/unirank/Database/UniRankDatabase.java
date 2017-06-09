package com.example.albergon.unirank.Database;

import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.Model.University;

/**
 * This interface specifies the access methods to the local university and indicators database. It
 * has to be implemented by database helpers to ensure the interface with whatever SQLite implementation
 * is currently deployed stays the same for the rest of the application.
 *
 * TODO: update interface with later additions
 */
public interface UniRankDatabase {

    University retrieveUniversity(int id);

    Indicator retrieveIndicator(int id);

    void saveAggregation(SaveRank ranking);

    SaveRank retrieveSave(String name);

}
