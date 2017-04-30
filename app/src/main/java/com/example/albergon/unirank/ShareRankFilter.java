package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.ShareRank;

/**
 * This interface is used in conjunction with lambda expressions to filter retrieved SharedRanks
 * according to a firebase query.
 */
public interface ShareRankFilter {

    boolean filter(Object param, ShareRank sharedAggregation);
}
