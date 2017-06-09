package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * ShareRankFilter implementation that filters aggregations by birth year.
 */
public class BirthyearFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.BIRTHYEAR;

    private Range parameter = null;
    private final String categoryString = "BirthYear";
    private String parameterString = null;

    public BirthyearFilter(Range range) {

        // argument check
        if(range == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = range;
    }

    /**
     * Alternative constructor that additionally takes the string representation of this filter's parameter
     * as argument.
     *
     * @param range             Range parameter for this filter
     * @param parameterString   string representation of the above range
     */
    public BirthyearFilter(Range range, String parameterString) {
        this(range);
        this.parameterString = parameterString;
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

    @Override
    public String getCategoryString() {
        return categoryString;
    }

    @Override
    public String getParameterString() {
        return parameterString;
    }

    public Range getParameter() {
        return parameter;
    }

}
