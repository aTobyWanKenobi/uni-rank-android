package com.example.albergon.unirank.Model;

import java.util.List;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */
public interface RankAggregationAlgorithm {

    Ranking aggregate(Indicator[] indicators, Map<Integer, Integer> toAggregate);
}
