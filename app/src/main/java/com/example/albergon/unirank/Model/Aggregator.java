package com.example.albergon.unirank.Model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class helps with the creation of a rank aggregation by allowing to set up the problem and
 * the inputs to the algorithm.
 *
 * It allows for multiple implementations of a solution to the aggregation problem, since it works
 * with any algorithm that implements the RankAggregationAlgorithm interface.
 */
public class Aggregator {

    private RankAggregationAlgorithm algorithm = null;
    private Map<Integer, Integer> weightings = null;
    private List<Indicator> indicators = null;
    private Ranking<Integer> result = null;

    /**
     * Public constructor that instantiates the aggregator with the desired aggregation algorithm.
     *
     * @param algorithm     the rank aggregation algorithm that will be used by this aggregator
     */
    @SuppressLint("UseSparseArrays")
    public Aggregator(RankAggregationAlgorithm algorithm) {

        // check arguments
        if(algorithm == null) {
            throw new IllegalArgumentException("Cannot initialize an Aggregator with a null algorithm");
        }

        // initialize structures
        this.algorithm = algorithm;
        this.weightings = new HashMap<>();
        indicators = new ArrayList<>();
    }

    /**
     * Setup method that allows to add an Indicator with a specific weight to be used in the aggregation.
     *
     * @param indicator     Indicator to be added to the set that will be aggregated
     * @param weight        weight to be assigned to the Indicator
     */
    public void add(Indicator indicator, int weight) {

        // arguments check
        if(indicator == null) {
            throw new IllegalArgumentException("Cannot add a null Indicator to an Aggregator");
        } else if(weight <= 0) {
            throw new IllegalArgumentException("An Indicator must have a positive weight in an aggregation");
        }

        // update structures
        indicators.add(indicator);
        weightings.put(indicator.getId(), weight);
    }

    /**
     * This method invokes the aggregation procedure of the encapsulated algorithm and returns the
     * generated aggregated ranking,
     *
     * @return  aggregated Ranking generated by the algorithm
     */
    public Ranking<Integer> aggregate() {

        // transform list to array
        Indicator[] indicatorsArray = new Indicator[indicators.size()];
        indicators.toArray(indicatorsArray);

        // use algorithm to aggregate rankings
        Ranking<Integer> result = algorithm.aggregate(indicatorsArray, weightings);
        this.result = result;

        return result;
    }

    /**
     * Getter for the settings of an aggregation, which are returned as a map that binds indicators
     * ids with their respective weight.
     *
     * @return  a Map of indicators ids and weights
     */
    public Map<Integer, Integer> getSettings() {
        return new HashMap<>(weightings);
    }

    public Ranking<Integer> getResult() {
        return result;
    }

}
