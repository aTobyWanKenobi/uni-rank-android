package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * ShareRankFilter implementation that filters aggregations by country.
 */
public class CountryFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.COUNTRY;

    private String parameter = null;
    private final String categoryString = "Country";

    public CountryFilter(String country) {

        // argument check
        if(country == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = country;
    }

    @Override
    public boolean filter(ShareRank sharedAggregation) {

        // argument check
        if(sharedAggregation == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        return parameter.equals(sharedAggregation.country);
    }

    @Override
    public Enums.PopularIndicatorsCategories getCategory() {
        return category;
    }

    @Override
    public String getCategoryString() {
        return categoryString;
    }

    @Override
    public String getParameterString() {
        return parameter;
    }

    public String getParameter() {
        return parameter;
    }
}
