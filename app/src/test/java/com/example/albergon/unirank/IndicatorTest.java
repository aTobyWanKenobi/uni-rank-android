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

        Map<University, Double> testEntries = new HashMap<>();
        University testUni = new University(0, "testName", "testCountry");
        testEntries.put(testUni, 100.0);

        Indicator testIndicator = new Indicator(testEntries, 0);

        Assert.assertEquals(100.0, testIndicator.scoreOf(testUni), 0.01);
        Assert.assertEquals(0, testIndicator.getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalOnNullMap() {
        new Indicator(null, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalOnNegativeIndex() {
        new Indicator(new HashMap<University, Double>(), -1);
    }

    @Test
    public void constructorCorrectlyEncapsulatesMap() {
        Map<University, Double> testEntries = new HashMap<>();
        University testUni1 = new University(0, "testName1", "testCountry1");
        testEntries.put(testUni1, 100.0);

        Indicator testIndicator = new Indicator(testEntries, 0);

        University testUni2 = new University(10, "testName2", "testCountry2");
        testEntries.put(testUni2, 50.0);

        Assert.assertEquals(0.0, testIndicator.scoreOf(testUni2), 0.01);
    }

    @Test(expected = IllegalArgumentException.class)
    public void scoreOfThrowsIllegalOnNull() {
        Indicator testIndicator = new Indicator(new HashMap<University, Double>(), 1);

        testIndicator.scoreOf(null);
    }
}
