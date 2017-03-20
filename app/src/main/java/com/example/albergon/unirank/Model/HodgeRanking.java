package com.example.albergon.unirank.Model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */

public class HodgeRanking implements RankAggregationAlgorithm {

    private Map<Indicator, Integer> dataset = null;
    private int[] items = null;
    private double[][] resultMatrix = null;


    public HodgeRanking() {

    }

    @Override
    public Ranking aggregate(Map<Indicator, Integer> toAggregate) {

        dataset = new HashMap<>(toAggregate);
        processData();

        return null;
    }

    //TODO: generate initial guess?

    private void processData() {
        createUniversityArray();
    }

    private void createUniversityArray() {

        Set<Integer> uniqueIds = new HashSet<>();

        for(Indicator i : dataset.keySet()) {
            uniqueIds.addAll(i.getIdSet());
        }

        items = new int[uniqueIds.size()];
        int index = 0;
        for(int id : uniqueIds) {
            items[index] = id;
            index++;
        }
    }

    private void createDataMatrix() {

    }

    private void pairwiseComparison() {

    }
}
