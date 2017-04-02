package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.Ranking;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleBinaryOperator;

/**
 * Test suite for Ranking
 */
public class RankingTest {

    @Test
    public void constructorCorrectlyEncapsulatesList() {

        List<Integer> testList = new ArrayList<>();
        testList.add(2);
        testList.add(1);
        testList.add(3);

        Ranking<Integer> testRank = new Ranking<>(testList);

        testList.add(4);

        Assert.assertEquals(3, testRank.getList().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorThrowsIllegalOnNull() {
        new Ranking<Integer>(null);
    }

    @Test
    public void getHeadWorks() {
        List<Integer> testList = new ArrayList<>();
        testList.add(2);
        testList.add(1);
        testList.add(3);

        Ranking<Integer> testRank = new Ranking<>(testList);

        Assert.assertTrue(2 == testRank.getHead());
    }

    @Test
    public void toStringWorks() {
        List<Integer> testList = new ArrayList<>();
        testList.add(2);
        testList.add(1);
        testList.add(3);

        Ranking<Integer> testRank = new Ranking<>(testList);

        Assert.assertEquals("[2, 1, 3]", testRank.toString());
    }

    @Test
    public void uniRankComparatorWorks() {

        Map<Integer, Double> testScores = new HashMap<>();
        testScores.put(1, 10.0);
        testScores.put(2, 20.0);
        testScores.put(3, 15.0);

        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);
        testList.add(3);

        testList.sort(new Ranking.UniIdRankComparator(testScores));

        Assert.assertEquals("[2, 3, 1]", testList.toString());
    }

    @Test
    public void uniRankComparatorHandlesTies() {
        Map<Integer, Double> testScores = new HashMap<>();
        testScores.put(1, 10.0);
        testScores.put(2, 10.0);

        List<Integer> testList = new ArrayList<>();
        testList.add(1);
        testList.add(2);

        testList.sort(new Ranking.UniIdRankComparator(testScores));

        Assert.assertTrue(testList.toString().equals("[1, 2]") || testList.toString().equals("[2, 1]"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void comparatorThrowsIllegalOnNull() {
        new Ranking.UniIdRankComparator(null);
    }
}
