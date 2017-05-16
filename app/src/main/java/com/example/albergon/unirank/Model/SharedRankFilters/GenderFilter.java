package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * Filter aggregations by gender
 */
public class GenderFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.GENDER;

    private Enums.GenderEnum parameter = null;

    public GenderFilter(Enums.GenderEnum gender) {

        // argument check
        if(gender == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = gender;
    }

    @Override
    public boolean filter(ShareRank sharedAggregation) {

        // argument check
        if(sharedAggregation == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        return sharedAggregation.gender.equals(parameter.toString());
    }

    @Override
    public Enums.PopularIndicatorsCategories getCategory() {
        return category;
    }

    public Enums.GenderEnum getParameter() {
        return parameter;
    }
}
