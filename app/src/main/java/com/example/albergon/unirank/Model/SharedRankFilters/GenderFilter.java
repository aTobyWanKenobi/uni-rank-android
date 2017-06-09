package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * ShareRankFilter implementation that filters aggregations by gender
 */
public class GenderFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.GENDER;

    private Enums.GenderEnum parameter = null;
    private final String categoryString = "Gender";
    private String parameterString = null;

    public GenderFilter(Enums.GenderEnum gender) {

        // argument check
        if(gender == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = gender;
    }

    public GenderFilter(Enums.GenderEnum gender, String parameterString) {
        this(gender);
        this.parameterString = parameterString;
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

    @Override
    public String getCategoryString() {
        return categoryString;
    }

    @Override
    public String getParameterString() {
        return parameterString;
    }

    public Enums.GenderEnum getParameter() {
        return parameter;
    }
}
