package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.HodgeRanking;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tobia Albergoni on 21.03.2017.
 */

public class HodgeRankingTest {

    @Test
    public void test1() {

        HodgeRanking algo = new HodgeRanking();

        Map<Integer, Double> mi1 = new HashMap<>();
        mi1.put(1, 10.0);
        mi1.put(2, 5.0);
        mi1.put(3, 2.0);
        Indicator i1 = new Indicator(mi1, 1);

        Map<Integer, Double> mi2 = new HashMap<>();
        mi2.put(1, 40.0);
        mi2.put(2, 15.0);
        mi2.put(3, 10.0);
        Indicator i2 = new Indicator(mi2, 2);

        Indicator[] indicators = {i1, i2};

        Map<Integer, Integer> w = new HashMap<>();
        w.put(1, 2);
        w.put(2, 1);

        Ranking<Integer> res = algo.aggregate(indicators, w);

        System.out.println(res);
    }
}
