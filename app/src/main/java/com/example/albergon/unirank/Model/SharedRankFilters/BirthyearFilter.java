package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * Filter aggregations by birthyear
 */
public class BirthyearFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.BIRTHYEAR;

    private Range parameter = null;

    public BirthyearFilter(Range range) {

        // argument check
        if(range == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = range;
    }

    @Override
    public boolean filter(ShareRank sharedAggregation) {

        // argument check
        if(sharedAggregation == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        return parameter.within(sharedAggregation.birthYear);
    }

    @Override
    public Enums.PopularIndicatorsCategories getCategory() {
        return category;
    }

    public Range getParameter() {
        return parameter;
    }

}
