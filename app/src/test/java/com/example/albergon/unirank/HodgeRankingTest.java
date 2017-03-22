package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.HodgeRanking;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 21.03.2017.
 */
public class HodgeRankingTest {

    @Test
    public void twoDisagreeingIndicatorsWithClearAggregation() {

        /**
         * This test uses two indicators that disagree on one ranking, but the scores are extremely
         * skewed towards one of the two items, so the algorithm should place that item on top.
         */

        HodgeRanking algorithm = new HodgeRanking();

        Map<Integer, Double> mi1 = new HashMap<>();
        mi1.put(2, 100.0);
        mi1.put(1, 10.0);
        mi1.put(3, 5.0);
        Indicator i1 = new Indicator(mi1, 1);

        Map<Integer, Double> mi2 = new HashMap<>();
        mi2.put(1, 10.0);
        mi2.put(2, 7.0);
        mi2.put(3, 2.0);
        Indicator i2 = new Indicator(mi2, 2);

        Indicator[] indicators = {i1, i2};

        Map<Integer, Integer> w = new HashMap<>();
        w.put(1, 1);
        w.put(2, 1);

        Ranking<Integer> res = algorithm.aggregate(indicators, w);

        Assert.assertEquals("[2, 1, 3]", res.toString());
    }

    @Test
    public void twoAgreeingIndicatorsShouldAggregateIntoSameRanking() {

        /**
         * This test uses two indicators that agree on the same ranking, which should be the
         * result of the aggregation.
         */

        HodgeRanking algorithm = new HodgeRanking();

        Map<Integer, Double> mi1 = new HashMap<>();
        mi1.put(1, 5.0);
        mi1.put(2, 4.0);
        mi1.put(3, 3.0);
        mi1.put(4, 2.0);
        mi1.put(5, 1.0);
        Indicator i1 = new Indicator(mi1, 1);

        Map<Integer, Double> mi2 = new HashMap<>();
        mi2.put(1, 5.0);
        mi2.put(2, 4.0);
        mi2.put(3, 3.0);
        mi2.put(4, 2.0);
        mi2.put(5, 1.0);
        Indicator i2 = new Indicator(mi2, 2);

        Indicator[] indicators = {i1, i2};

        Map<Integer, Integer> w = new HashMap<>();
        w.put(1, 1);
        w.put(2, 1);

        Ranking<Integer> res = algorithm.aggregate(indicators, w);

        Assert.assertEquals("[1, 2, 3, 4, 5]", res.toString());
    }

    @Test
    public void differentWeightingShouldBreakTie() {

        /**
         * This test uses two indicators that disagree on one ranking, but are specular in terms of
         * scores. A higher weighting on one of the two should break the tie and favor the ranking
         * of that indicator.
         */

        HodgeRanking algorithm = new HodgeRanking();

        Map<Integer, Double> mi1 = new HashMap<>();
        mi1.put(2, 10.0);
        mi1.put(1, 5.0);
        mi1.put(3, 2.0);
        Indicator i1 = new Indicator(mi1, 1);

        Map<Integer, Double> mi2 = new HashMap<>();
        mi2.put(1, 10.0);
        mi2.put(2, 5.0);
        mi2.put(3, 2.0);
        Indicator i2 = new Indicator(mi2, 2);

        Indicator[] indicators = {i1, i2};

        Map<Integer, Integer> w = new HashMap<>();
        w.put(1, 2);
        w.put(2, 1);

        Ranking<Integer> res = algorithm.aggregate(indicators, w);

        Assert.assertEquals("[2, 1, 3]", res.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void aggregateWithNullsThrowsIllegal() {
        new HodgeRanking().aggregate(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void aggregateWithIncompatibleArgsThrowsIllegal() {
        HodgeRanking algorithm = new HodgeRanking();
        Indicator[] indicators = {new Indicator(new HashMap<Integer, Double>(), 0)};
        Map<Integer, Integer> weightings = new HashMap<>();
        weightings.put(0, 1);
        weightings.put(1, 2);

        algorithm.aggregate(indicators, weightings);
    }
}
