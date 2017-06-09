package com.example.albergon.unirank.Model.SharedRankFilters;

import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.ShareRank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class provides methods to process the shared pool of aggregations
 * retrieved from firebase in order to give results to the implemented queries
 */
public class FilterManager {

    private List<ShareRankFilter> filters = null;

    public FilterManager(List<ShareRankFilter> filters) {
        this.filters = new ArrayList<>(filters);

    }

    public FilterManager(ShareRankFilter... readyFilters) {
        filters = Arrays.asList(readyFilters);
    }

    /**
     * Retrieves the total scores gathered by indicators in the shared pool in aggregations are not
     * filtered out by the current list
     *
     * @param sharedPool    all shared aggregations
     *
     * @return              array containing indicator scores
     */
    public int[] filter(List<ShareRank> sharedPool) {

        // arguments check
        if(sharedPool == null) {
            throw new IllegalArgumentException("Arguments to filter shared pool cannot be null");
        }

        // iterate through shared pool, filter aggregations and collect cumulative indicator weights
        int[] indicatorsScores = new int[Tables.IndicatorsList.values().length];
        for(ShareRank sharedAggregation : sharedPool) {
            // apply filter constructed above
            if(allFiltersAnd(sharedAggregation)) {
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
     * Combine all filters in a unique conjunction and return the corresponding boolean result
     *
     * @param aggregation   aggregation to filter
     * @return              boolean depending if the aggregation was filtered out or not
     */
    private boolean allFiltersAnd(ShareRank aggregation) {

        boolean isOk = true;
        for(ShareRankFilter filter : filters) {
            isOk = isOk && filter.filter(aggregation);
            if(!isOk) {
                return false;
            }
        }

        return isOk;
    }
}
