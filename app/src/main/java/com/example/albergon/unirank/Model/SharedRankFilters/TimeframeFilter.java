package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.ShareRank;

/**
 * ShareRankFilter implementation that filters aggregations by upload date
 */
public class TimeframeFilter implements ShareRankFilter {

    private final Enums.PopularIndicatorsCategories category = Enums.PopularIndicatorsCategories.TIMEFRAME;

    private Enums.TimeFrame parameter = null;
    private final String categoryString = "Upload date";
    private String parameterString = null;

    public TimeframeFilter(Enums.TimeFrame timeframe) {

        // argument check
        if(timeframe == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        this.parameter = timeframe;
    }

    public TimeframeFilter(Enums.TimeFrame timeframe, String parameterString) {
        this(timeframe);
        this.parameterString = parameterString;
    }

    @Override
    public boolean filter(ShareRank sharedAggregation) {

        // argument check
        if(sharedAggregation == null) {
            throw new IllegalArgumentException("Filter arguments cannot be null");
        }

        String currentDate = FirebaseHelper.generateDate();
        String sharedDate = sharedAggregation.date;
        boolean belongs;

        switch(parameter) {
            case MONTH:
                // keep month and year from dates so that we can check for month equality
                belongs = currentDate.split(" ", 2)[1].equals(sharedDate.split(" ", 2)[1]);
                break;
            case YEAR:
                // cast years and compare
                belongs =   Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(' ') + 1)) ==
                        Integer.parseInt(sharedDate.substring(sharedDate.lastIndexOf(' ') + 1));
                break;
            default:
                throw new IllegalStateException("Unknown element in TimeFrame enumeration");
        }

        return belongs;
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

    public Enums.TimeFrame getParameter() {
        return parameter;
    }
}
