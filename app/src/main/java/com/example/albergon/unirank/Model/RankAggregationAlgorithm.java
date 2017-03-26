package com.example.albergon.unirank.Model;

import java.util.Map;

/**
 * This interface models an algorithm that solves the rank aggregation problem. Such an algorithm
 * just has to implement an aggregate function that returns the desired aggregation ranking.
 * Nothing more is required by this interface, since such algorithm have many implementation differences.
 *
 * The only algorithm currently implemented is HodgeRank.
 */
public interface RankAggregationAlgorithm {

    /**
     * This method has to compute an aggregated ranking starting from an array of Indicators and a
     * map associating a weight to each of these indicators.
     *
     * @param indicators    indicators to aggregate
     * @param weightings    weights associated to indicators
     * @return              a Ranking resulting fromt he aggregation
     */
    Ranking<Integer> aggregate(Indicator[] indicators, Map<Integer, Integer> weightings);
}
