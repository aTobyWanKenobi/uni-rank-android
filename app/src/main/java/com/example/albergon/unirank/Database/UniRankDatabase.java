package com.example.albergon.unirank.Database;

import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.Model.University;

/**
 * This interface specifies the access methods to the local university and indicators database. It
 * has to be implemented by database helpers to ensure the interface with whatever SQLite implementation
 * is currently deployed stays the same for the rest of the application.
 */
public interface UniRankDatabase {

    University getUniversity(int id);

    Indicator getIndicator(int id);

    void saveAggregation(SaveRank ranking);

    SaveRank getAggregation(int savedId);

}
