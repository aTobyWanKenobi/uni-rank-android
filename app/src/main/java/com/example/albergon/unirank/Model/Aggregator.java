package com.example.albergon.unirank.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */

public class Aggregator {

    private RankAggregationAlgorithm algorithm = null;
    private Map<Integer, Integer> dataset = null;
    private Ranking<Integer> generatedRanking = null;

    public Aggregator(RankAggregationAlgorithm algorithm) {
        this.algorithm = algorithm;
        this.dataset = new HashMap<>();
    }

    public void add(Integer indicator, int weight) {
        dataset.put(indicator, weight);
    }

    public Ranking aggregate() {
        //Ranking aggregatedRanking = algorithm.aggregate(dataset);
        // return aggregatedRanking;
        return null;
    }

    public Ranking getRanking() {
        return generatedRanking;
    }
}