package com.example.albergon.unirank.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */
public class Aggregator {

    private RankAggregationAlgorithm algorithm = null;
    private Map<Integer, Integer> weightings = null;
    private List<Indicator> indicators = null;

    public Aggregator(RankAggregationAlgorithm algorithm) {

        // check arguments
        if(algorithm == null) {
            throw new IllegalArgumentException("Cannot initialize an Aggregator with a null algorithm");
        }
        this.algorithm = algorithm;
        this.weightings = new HashMap<>();
        indicators = new ArrayList<>();
    }

    public void add(Indicator indicator, int weight) {
        indicators.add(indicator);
        weightings.put(indicator.getId(), weight);
    }

    public Ranking aggregate() {

        // transform list to array
        Indicator[] indicatorsArray = new Indicator[indicators.size()];
        indicators.toArray(indicatorsArray);

        // use algorithm to aggregate rankings
        Ranking aggregatedRanking = algorithm.aggregate(indicatorsArray, weightings);

        return aggregatedRanking;
    }
}
