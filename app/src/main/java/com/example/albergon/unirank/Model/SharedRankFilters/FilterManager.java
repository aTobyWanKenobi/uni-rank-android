package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Database.FirebaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Countries;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Range;
import com.example.albergon.unirank.Model.ShareRank;
import com.example.albergon.unirank.Model.SharedRankFilters.ShareRankFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This not instantiable class provides static methods to process the shared pool of aggregations
 * retrieved from firebase in order to give results to the implemented queries
 */
public class FilterManager {

    List<ShareRankFilter> filters = null;

    // private empty constructor
    public FilterManager() {

        filters = new ArrayList<>();
    }

    public void addFilter(String category, String parameter) {


    }

    /**
     * Retrieves the total scores gathered by indicators in the shared pool in aggregations that
     * belong to a precise category.
     *
     * @param sharedPool    all shared aggregations
     * @param category      category by which to consider aggregations
     * @param parameter     parameter depending on category
     *
     * @return              array containing indicator scores
     */
    public static int[] popularIndicatorsByCategory(List<ShareRank> sharedPool, Enums.PopularIndicatorsCategories category, Object parameter) {

        // arguments check
        if(sharedPool == null || category == null || parameter == null) {
            throw new IllegalArgumentException("Arguments to filter shared pool cannot be null");
        }

        // construct a filter for ShareRanks depending on category
        ShareRankFilter categoryFilter = null;
        switch(category) {
            case GENDER:
                categoryFilter = createGenderFilter();
                break;
            case TYPE:
                categoryFilter = createUserTypeFilter();
                break;
            case BIRTHYEAR:
                categoryFilter = createBirthyearFilter();
                break;
            case TIMEFRAME:
                categoryFilter = createTimeFrameFilter();
                break;
            case COUNTRY:
                categoryFilter = createCountryFilter();
                break;
            default:
                throw new IllegalStateException("Unknown element in categories enumeration");
        }

        // iterate through shared pool, filter aggregations and collect cumulative indicator weights
        int[] indicatorsScores = new int[Tables.IndicatorsList.values().length];
        for(ShareRank sharedAggregation : sharedPool) {
            // apply filter constructed above
            if(categoryFilter.filter(parameter, sharedAggregation)) {
                // transform firebase formatted settings back into app format
                Map<Integer, Integer> settings = ShareRank.reprocessSettings(sharedAggregation.settings);
                for(Map.Entry<Integer, Integer> entries : settings.entrySet()) {
                    // add weights for each indicator
                    indicatorsScores[entries.getKey()] += entries.getValue();
                }
            }
        }

        return indicatorsScores;
    }

    /**
     * Depending on the parameter string representation and on the specified query category, this method
     * construct the appropriate Object parameter to be used as argument of popularIndicatorByCategory.
     *
     * @param currentCategory   category to which the parameter belongs
     * @param parameter         string representation of the parameter
     * @return                  Object representation of the parameter
     */
    public static Object parameterDependingOnCategory(Enums.PopularIndicatorsCategories currentCategory, String parameter) {

        // arguments check
        if(currentCategory == null || parameter == null) {
            throw new IllegalArgumentException("Arguments to parameter transformation cannot be null");
        } else if(parameter.isEmpty()) {
            throw new IllegalArgumentException("String representation of parameter cannot be empty");
        }

        // handle string parameter differently depending on category
        switch(currentCategory) {

            // parameter is enum GenderEnum
            case GENDER:

                // retrieve gender parameter
                Enums.GenderEnum gender = null;
                for(Enums.GenderEnum g : Enums.GenderEnum.values()) {
                    if(g.toString().equals(parameter)) {
                        gender = g;
                    }
                }

                return gender;

            // parameter is enum TypeOfUsers
            case TYPE:

                // retrieve type parameter
                Enums.TypesOfUsers type = null;
                for(Enums.TypesOfUsers t : Enums.TypesOfUsers.values()) {
                    if(t.toString().equals(parameter)) {
                        type = t;
                    }
                }

                return type;

            // parameter is a range of years
            case BIRTHYEAR:

                // retrieve age parameter
                Enums.UserAgeCategories age = null;
                for(Enums.UserAgeCategories a : Enums.UserAgeCategories.values()) {
                    if(a.toString().equals(parameter)) {
                        age = a;
                    }
                }

                // build correct range depending on current year
                Range yearRange = null;
                String currentDate = FirebaseHelper.generateDate();
                int currentYear = Integer.parseInt(currentDate.substring(currentDate.lastIndexOf(' ') + 1));
                switch(age) {
                    case KIDS:
                        yearRange = new Range(currentYear - 15, currentYear);
                        break;
                    case TEENS:
                        yearRange = new Range(currentYear - 20, currentYear - 15);
                        break;
                    case YOUNGS:
                        yearRange = new Range(currentYear - 27, currentYear - 20);
                        break;
                    case ADULTS:
                        yearRange = new Range(currentYear - 50, currentYear - 27);
                        break;
                    case OLD:
                        yearRange = new Range(1900, currentYear - 50);
                        break;
                    default:
                        throw new IllegalStateException("Unknown element in age enumeration");
                }

                return yearRange;

            // parameter is enum TimeFrame
            case TIMEFRAME:

                // retrieve time frame parameter
                Enums.TimeFrame timeFrame = null;
                for(Enums.TimeFrame t : Enums.TimeFrame.values()) {
                    if(t.toString().equals(parameter)) {
                        timeFrame = t;
                    }
                }

                return timeFrame;

            // parameter is country code string
            case COUNTRY:

                // retrieve country parameter
                String countryCode = null;
                for(Map.Entry<String, String> entry : Countries.countryMap.entrySet()) {
                    if(entry.getValue().equals(parameter)) {
                        countryCode = entry.getKey();
                    }
                }

                return countryCode;

            default:
                throw new IllegalStateException("Unknown element in categories enumeration");
        }

    }
}
