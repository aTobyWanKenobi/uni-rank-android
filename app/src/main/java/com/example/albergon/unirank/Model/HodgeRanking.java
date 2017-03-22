package com.example.albergon.unirank.Model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import flanagan.math.Minimization;
import flanagan.math.MinimizationFunction;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */
public class HodgeRanking implements RankAggregationAlgorithm {

    private Map<Integer, Integer> weightings = null;
    private Indicator[] indicators = null;
    private int numItems = 0;

    @Override
    public Ranking aggregate(Indicator[] indicators, Map<Integer, Integer> weightings) {

        // check arguments
        if(indicators == null || weightings == null) {
            throw new IllegalArgumentException("Indicators and weightings cannot be null");
        } else if(weightings.size() != indicators.length) {
            throw new IllegalArgumentException("Each indicator must have a weight");
        }

        // store data to aggregate
        this.weightings = new HashMap<>(weightings);
        this.indicators = Arrays.copyOf(indicators, indicators.length);

        // retrieve unique universities ids
        Integer[] uniqueUniversities = retrieveUniqueItems();

        // initialize other structures
        numItems = uniqueUniversities.length;
        int[] numComparisons = new int[numItems*numItems];
        double[][] resultMatrix = new double[numItems*numItems][indicators.length];

        // process data
        double[] initialEstimate = pairwiseComparison(uniqueUniversities, resultMatrix, numComparisons);
        double[] aggregatedScores = aggregateScoresForMinimization(numComparisons, resultMatrix);

        // perform minimization
        MinimizationFunction costFunction = new HodgeRankCostFunction(aggregatedScores);
        double[] minimizationResult = minimize(costFunction, initialEstimate);

        // use results to construct ranking
        Ranking<Integer> resultingAggregation = constructRanking(minimizationResult, uniqueUniversities);

        return resultingAggregation;
    }

    private Integer[] retrieveUniqueItems() {

        // create unique universities set
        Set<Integer> uniqueIds = new HashSet<>();
        for(Indicator i : indicators) {
            uniqueIds.addAll(i.getIdSet());
        }

        Integer[] toRet = new Integer[uniqueIds.size()];
        int index = 0;
        for(int id : uniqueIds) {
            toRet[index] = id;
            index++;
        }

        return toRet;
    }

    private double[] pairwiseComparison(Integer[] items, double[][] result, int[] numComparisons) {

        double[] estimate = new double[numItems];
        int[] estimateCount = new int[numItems];

        for(int n = 0; n < indicators.length; n++) {
            Indicator currentIndicator = indicators[n];
            for(int i = 0; i < numItems; i++) {

                // keep track of total "aggregated score" of item as an estimate for minimizer
                estimate[i] += weightings.get(currentIndicator.getId())*currentIndicator.scoreOf(items[i]);
                if(estimate[i] != 0.0) {
                    estimateCount[i] += 1;
                }

                for(int j = 0; j < numItems; j++) {

                    int arrayIndex = i * numItems + j;
                    double scoreOfI = currentIndicator.scoreOf(items[i]);
                    double scoreOfJ = currentIndicator.scoreOf(items[j]);

                    if(scoreOfI != 0.0 && scoreOfJ != 0.0) {
                        result[arrayIndex][n] = weightings.get(currentIndicator.getId())*(scoreOfJ - scoreOfI);
                        numComparisons[arrayIndex] += 1;
                    } else {
                        result[arrayIndex][n] = 0;
                    }
                }
            }
        }

        for(int i = 0; i < numItems; i++) {
            estimate[i] = estimate[i]/estimateCount[i];
        }

        return estimate;
    }

    private double[] aggregateScoresForMinimization(int[] numComparisons, double[][] resultMatrix) {

        double[] aggregatedScores = new double[numItems*numItems];

        for(int pair = 0; pair < numItems*numItems; pair++) {
            double rowSum = 0;
            for (int indicator = 0; indicator < indicators.length; indicator++) {
                rowSum += resultMatrix[pair][indicator];
            }
            aggregatedScores[pair] = rowSum / numComparisons[pair];
        }

        return aggregatedScores;
    }

    //TODO: set step parameters
    private double[] minimize(MinimizationFunction toMinimize, double[] start) {

        // use Flanagan's library Nelder-Mead implementation
        Minimization minimizer = new Minimization();
        minimizer.nelderMead(toMinimize, start);
        double[] resToRet = minimizer.getParamValues();

        return resToRet;
    }

    private Ranking<Integer> constructRanking(double[] minimizationResult, Integer[]items) {

        // associate computed scores with ids
        Map<Integer, Double> uniScores = new HashMap<>();
        for(int i = 0; i < minimizationResult.length; i++) {
            uniScores.put(items[i], minimizationResult[i]);
        }

        // sort ids accordingly
        List<Integer> sortedIds = Arrays.asList(items);
        //noinspection Since15
        sortedIds.sort(new RankComparator(uniScores));

        return new Ranking<>(sortedIds);
    }

    private class RankComparator implements Comparator<Integer> {

        private Map<Integer, Double> uniScores = null;

        public RankComparator(Map<Integer, Double> uniScores) {

            this.uniScores = new HashMap<>(uniScores);
        }

        @Override
        public int compare(Integer o1, Integer o2) {
            if(uniScores.get(o1) < uniScores.get(o2)) {
                return 1;
            } else if(uniScores.get(o1) == uniScores.get(o2)) {
                return 0;
            } else {
                return -1;
            }
        }
    }

    private class HodgeRankCostFunction implements MinimizationFunction {

        private double[] aggregatedScores = null;

        public HodgeRankCostFunction(double[] aggregatedScores) {
            this.aggregatedScores = Arrays.copyOf(aggregatedScores, aggregatedScores.length);
        }

        @Override
        public double function(double[] doubles) {
            double sum = 0;
            for(int i = 0; i < doubles.length; i++) {
                for(int j = 0; j < doubles.length; j++) {
                    sum += Math.pow(doubles[j] - doubles[i] - aggregatedScores[i * doubles.length + j], 2);
                }
            }
            return sum;
        }
    }
}
