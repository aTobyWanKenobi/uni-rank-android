package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * This interface is used to filter retrieved SharedRanks
 * according to a firebase query.
 */
public interface  ShareRankFilter {

    boolean filter(ShareRank sharedAggregation);

    Enums.PopularIndicatorsCategories getCategory();

}
