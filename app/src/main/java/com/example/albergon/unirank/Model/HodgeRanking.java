package com.example.albergon.unirank.Model;

import android.annotation.SuppressLint;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import flanagan.math.Minimization;
import flanagan.math.MinimizationFunction;

/**
 * This class implements the HodgeRank rank aggregation algorithm described by Jiang et al.(2008) in
 * their \"Statistical ranking and combinatorial Hodge theory\" paper. It implements the interface
 * RankAggregationAlgorithm.
 *
 * The only public class method is the aggregate function, which returns the desired rank aggregation,
 * the rest of the implementation is detailed in the private methods documentation.
 *
 * Note that this class uses Michael Thomas Flanagan's Java Scientific Library for the resolution of
 * the minimization problem (www.ee.ucl.ac.uk/~mflanaga)
 */
public class HodgeRanking implements RankAggregationAlgorithm {

    private Map<Integer, Integer> weightings = null;
    private Indicator[] indicators = null;
    private int numItems = 0;

    @SuppressLint("UseSparseArrays")
    @Override
    public Ranking<Integer> aggregate(Indicator[] indicators, Map<Integer, Integer> weightings) {

        // check arguments
        if(indicators == null || weightings == null) {
            throw new IllegalArgumentException("Indicators and weightings cannot be null");
        } else if(weightings.size() != indicators.length) {
            throw new IllegalArgumentException("Each indicator must have a weight");
        } else if(!checkDataCoherence(indicators, weightings)){
            throw new IllegalArgumentException("Indicators and weighting ids don't match");
        }

        // store data to aggregate
        this.weightings = new HashMap<>(weightings);
        this.indicators = Arrays.copyOf(indicators, indicators.length);

        // if only one data source, return the ranking it induces
        if(indicators.length == 1) {
            return aggregateSingleIndicator();
        }

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
        return constructRanking(minimizationResult, uniqueUniversities);
    }

    /**
     * This utility methods checks that the input aggregation parameters are coherent, i.e. every
     * Indicator in the array has a corresponding weight assigned.
     *
     * @param indicators    indicators array
     * @param weightings    weight map
     * @return              true if the two inputs are coherent, false otherwise
     */
    private boolean checkDataCoherence(Indicator[] indicators, Map<Integer, Integer> weightings) {

        for(Indicator i : indicators) {
            if(!weightings.keySet().contains(i.getId())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the ranking induced by a single indicator.
     *
     * @return  ranking generated by the indicator data
     */
    private Ranking<Integer> aggregateSingleIndicator() {

        return indicators[0].getRanking();
    }

    /**
     * Computes the set of unique items present in the aggregation from the data contained in all
     * indicators. Items ids are then returned as an array.
     *
     * @return  array containing all the distinct items ids of the aggregation
     */
    private Integer[] retrieveUniqueItems() {

        // create unique universities set
        Set<Integer> uniqueIds = new HashSet<>();
        for(Indicator i : indicators) {
            uniqueIds.addAll(i.getIdSet());
        }

        // store set in array
        Integer[] toRet = new Integer[uniqueIds.size()];
        int index = 0;
        for(int id : uniqueIds) {
            toRet[index] = id;
            index++;
        }

        return toRet;
    }

    /**
     * This processing method perform the pairwise comparison between items described by the algorithm.
     * For each indicator, this method returns the result of a weighted pairwise comparison between
     * all pairs of items in the indicator.
     *
     * The result is then a matrix containing this data for each indicator. Note that each column of
     * the result is a skew-symmetric matrix stored in a row-major order.
     *
     * We also keep track of the number of pairwise comparisons for each pair across indicators.
     *
     * The arguments array are filled with the corresponding data. The array returned by the method
     * is instead an initial estimate of the aggregate result useful to save computation time during
     * the optimisation problem.
     *
     * @param items             unique items to compare
     * @param result            result matrix to fill as explained above
     * @param numComparisons    matrix in which the number of comparisons for each pair are stored
     * @return                  an initial estimate for the minimization problem
     */
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

                    // compare items only if both appear in the indicator
                    if(scoreOfI != 0.0 && scoreOfJ != 0.0) {
                        // pairwise comparison
                        result[arrayIndex][n] = weightings.get(currentIndicator.getId())*(scoreOfJ - scoreOfI);
                        numComparisons[arrayIndex] += 1;
                    } else {
                        // otherwise set comparison result to zero
                        result[arrayIndex][n] = 0;
                    }
                }
            }
        }
        long endTime = System.currentTimeMillis();

        // further process estimate
        for(int i = 0; i < numItems; i++) {
            estimate[i] = estimate[i]/estimateCount[i];
        }

        return estimate;
    }

    /**
     * This processing method takes the results of the pairwise comparison and further processes them
     * to prepare the correct inputs to the minimization procedure.
     *
     * @param numComparisons    number of comparison performed for each pair
     * @param resultMatrix      result of the pairwise comparison for each indicator
     * @return                  aggregated result for each pair
     */
    private double[] aggregateScoresForMinimization(int[] numComparisons, double[][] resultMatrix) {

        double[] aggregatedScores = new double[numItems*numItems];

        // for each pair, sum the results of the comparisons for each indicator
        for(int pair = 0; pair < numItems*numItems; pair++) {
            double rowSum = 0;
            for (int indicator = 0; indicator < indicators.length; indicator++) {
                rowSum += resultMatrix[pair][indicator];
            }
            // then normalize by dividing for the number of comparisons
            aggregatedScores[pair] = rowSum / numComparisons[pair];
        }

        return aggregatedScores;
    }

    /**
     * This method implements the minimization of the cost function described in HodgeRank. It uses
     * the Nelder-Mead method (or downhill simplex method) provided by Flanagan's library cited in
     * the class header.
     *
     * The result is an array of aggregated scores for items that generate an acyclic difference
     * graph and thus allow to determine an unambiguous ranking for items.
     *
     * @param toMinimize    cost function to minimize
     * @param start         initial estimate
     * @return              aggregated scores for each item
     */
    private double[] minimize(MinimizationFunction toMinimize, double[] start) {

        long startTime = System.currentTimeMillis();

        // use Flanagan's library Nelder-Mead implementation
        Minimization minimizer = new Minimization();
        minimizer.nelderMead(toMinimize, start);

        long endTime = System.currentTimeMillis();

        // FIXME: remove debug printing
        System.out.println("Duration minimization : " + (endTime - startTime) + " ms");

        return minimizer.getParamValues();
    }

    /**
     * This method constructs the final aggregated ranking given the results of the minimization process-
     *
     * @param minimizationResult    array of aggregated scores resulting from minimization
     * @param items                 items array
     * @return                      aggregated ranking of ids
     */
    private Ranking<Integer> constructRanking(double[] minimizationResult, Integer[]items) {

        // associate computed scores with ids
        @SuppressLint("UseSparseArrays") Map<Integer, Double> uniScores = new HashMap<>();
        for(int i = 0; i < minimizationResult.length; i++) {
            uniScores.put(items[i], minimizationResult[i]);
        }

        // sort ids accordingly
        List<Integer> sortedIds = Arrays.asList(items);
        //noinspection Since15
        sortedIds.sort(new Ranking.UniIdRankComparator(uniScores));

        return new Ranking<>(sortedIds);
    }

    /**
     * This private nested class extends the MinimizationFunction interface to represent the cost
     * function described in the HodgeRank paper and which has to be minimized
     */
    private class HodgeRankCostFunction implements MinimizationFunction {

        private double[] aggregatedScores = null;

        public HodgeRankCostFunction(double[] aggregatedScores) {
            this.aggregatedScores = Arrays.copyOf(aggregatedScores, aggregatedScores.length);
        }

        @Override
        public double function(double[] doubles) {

            // cost function described at page 212 of Jiang paper
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
