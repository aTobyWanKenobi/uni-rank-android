package com.example.albergon.unirank.Model;

import java.util.Map;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */
public interface RankAggregationAlgorithm {

    Ranking aggregate(Map<Indicator, Integer> toAggregate);
}
