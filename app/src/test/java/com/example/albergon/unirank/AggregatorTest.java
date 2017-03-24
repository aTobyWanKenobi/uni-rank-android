package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.Aggregator;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.RankAggregationAlgorithm;
import com.example.albergon.unirank.Model.Ranking;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test suite for Aggregator
 */
public class AggregatorTest {

    private static class TestAlgorithm implements RankAggregationAlgorithm {

        @Override
        public Ranking aggregate(Indicator[] indicators, final Map<Integer, Integer> toAggregate) {

            // just return the indicators id in order of weight

            List<Indicator> iList = Arrays.asList(indicators);

            iList.sort(new Comparator<Indicator>() {
                @Override
                public int compare(Indicator o1, Indicator o2) {
                    if(toAggregate.get(o1.getId()) < toAggregate.get(o2.getId())) {
                        return 1;
                    } else if(toAggregate.get(o1.getId()) == toAggregate.get(o2.getId())) {
                        return 0;
                    } else {
                        return -1;
                    }
                }
            });

            return new Ranking(iList);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalOnNull() {
        new Aggregator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addThrowsIllegalOnNull() {
        Aggregator testAggregator = new Aggregator(new TestAlgorithm());

        testAggregator.add(null, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addThrowsIllegalOnNegativeWeight() {
        Aggregator testAggregator = new Aggregator(new TestAlgorithm());

        Map<Integer, Double> testMap = new HashMap<>();
        testMap.put(1, 2.0);

        testAggregator.add(new Indicator(testMap, 1), 0);
    }

    @Test
    public void aggregatorWorksWithTestAlgorithm() {
        Aggregator testAggregator = new Aggregator(new TestAlgorithm());

        Map<Integer, Double> testMap = new HashMap<>();
        testMap.put(1, 2.0);

        Indicator i1 = new Indicator(testMap, 1);
        Indicator i2 = new Indicator(testMap, 2);
        Indicator i3 = new Indicator(testMap, 3);

        testAggregator.add(i1, 1);
        testAggregator.add(i2, 2);
        testAggregator.add(i3, 3);

        Ranking<Indicator> result = testAggregator.aggregate();

        Assert.assertEquals(3, result.getHead().getId());
        Assert.assertEquals(2, result.getList().get(1).getId());
        Assert.assertEquals(1, result.getList().get(2).getId());
    }

}
