package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.University;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Test suite for Indicator
 */
public class IndicatorTest {

    @Test
    public void constructorAndScoreOfWork() {

        Map<Integer, Double> testEntries = new HashMap<>();
        testEntries.put(1, 100.0);

        Indicator testIndicator = new Indicator(testEntries, 0);

        Assert.assertEquals(100.0, testIndicator.scoreOf(1), 0.01);
        Assert.assertEquals(0, testIndicator.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalOnNullMap() {
        new Indicator(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalOnNegativeIndex() {
        new Indicator(new HashMap<Integer, Double>(), -1);
    }

    @Test
    public void constructorCorrectlyEncapsulatesMap() {
        Map<Integer, Double> testEntries = new HashMap<>();
        testEntries.put(1, 100.0);

        Indicator testIndicator = new Indicator(testEntries, 0);

        testEntries.put(2, 50.0);

        Assert.assertEquals(0.0, testIndicator.scoreOf(2), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scoreOfThrowsIllegalOnNull() {
        Indicator testIndicator = new Indicator(new HashMap<Integer, Double>(), 1);

        testIndicator.scoreOf(-1);
    }

    @Test
    public void getSizeWorks() {

        Map<Integer, Double> testEntries = new HashMap<>();
        testEntries.put(1, 2.0);
        testEntries.put(2, 4.0);

        Indicator testIndicator = new Indicator(testEntries, 1);

        Assert.assertEquals(2, testIndicator.getSize());
    }

    @Test
    public void getIdSetWorks() {

        Map<Integer, Double> testEntries = new HashMap<>();
        testEntries.put(1, 0.0);
        testEntries.put(2, 0.0);

        Indicator testIndicator = new Indicator(testEntries, 1);

        Assert.assertEquals(2, testIndicator.getIdSet().size());
        Assert.assertTrue(testIndicator.getIdSet().contains(1));
        Assert.assertTrue(testIndicator.getIdSet().contains(2));
        Assert.assertFalse(testIndicator.getIdSet().contains(3));
    }

    @Test
    public void getRankingWorks() {
        Map<Integer, Double> testEntries = new HashMap<>();
        testEntries.put(1, 2.0);
        testEntries.put(2, 4.0);

        Indicator testIndicator = new Indicator(testEntries, 1);

        Assert.assertEquals("[2, 1]", testIndicator.getRanking().toString());
    }
}
