package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * ShareRankFilter implementation that filters aggregations by user type
 */
public class UserTypeFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.TYPE;

    private Enums.TypesOfUsers parameter = null;
    private final String categoryString = "Type of user";

    public UserTypeFilter(Enums.TypesOfUsers type) {

        // argument check
        if(type == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = type;
    }

    @Override
    public boolean filter(ShareRank sharedAggregation) {
        // argument check
        if(sharedAggregation == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        return sharedAggregation.userType.equals(parameter.toString());
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
        return parameter.toString();
    }

    public Enums.TypesOfUsers getParameter() {
        return parameter;
    }
}
