package com.example.albergon.unirank.Model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.lbfgs4j.*;
import com.github.lbfgs4j.liblbfgs.Function;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */
public class HodgeRanking implements RankAggregationAlgorithm {

    private Map<Integer, Integer> weightings = null;
    private Indicator[] indicators = null;

    //TODO: inline in methods and pass as arguments
    // data processing
    private Integer[] items = null;
    private int[] numComparisons = null;
    private int numItems = 0;
    private double[][] resultMatrix = null;

    // minimization
    double[] aggregatedScores = null;
    double[] minimizationResult = null;

    public HodgeRanking() {

    }

    @Override
    public Ranking aggregate(Indicator[] indicators, Map<Integer, Integer> weightings) {

        this.weightings = new HashMap<>(weightings);
        this.indicators = Arrays.copyOf(indicators, indicators.length);

        processData();
        return minimize();
    }

    //TODO: generate initial guess?

    private void processData() {

        setupArrays();
        pairwiseComparison();
        prepareMinimizationArguments();
    }

    private void setupArrays() {

        // create unique universities array
        Set<Integer> uniqueIds = new HashSet<>();

        for(Indicator i : indicators) {
            uniqueIds.addAll(i.getIdSet());
        }

        items = new Integer[uniqueIds.size()];
        int index = 0;
        for(int id : uniqueIds) {
            items[index] = id;
            index++;
        }

        // initialize other structures
        numItems = items.length;
        numComparisons = new int[numItems*numItems];
        resultMatrix = new double[numItems*numItems][indicators.length];
    }

    private void pairwiseComparison() {

        for(int n = 0; n < indicators.length; n++) {
            Indicator currentIndicator = indicators[n];
            for(int i = 0; i < numItems; i++) {
                for(int j = 0; j < numItems; j++) {

                    int arrayIndex = i * numItems + j;
                    double scoreOfI = currentIndicator.scoreOf(items[i]);
                    double scoreOfJ = currentIndicator.scoreOf(items[j]);

                    if(scoreOfI != 0.0 && scoreOfJ != 0.0) {
                        resultMatrix[arrayIndex][n] = weightings.get(currentIndicator.getId())*(scoreOfJ - scoreOfI);
                        numComparisons[arrayIndex] += 1;
                    } else {
                        resultMatrix[arrayIndex][n] = 0;
                    }
                }
            }
        }
    }

    private void prepareMinimizationArguments() {

        aggregatedScores = new double[numItems*numItems];

        for(int pair = 0; pair < numItems*numItems; pair++) {
            double rowSum = 0;
            for(int indicator = 0; indicator < indicators.length; indicator++) {
                rowSum += resultMatrix[pair][indicator];
            }
            aggregatedScores[pair] = rowSum/numComparisons[pair];
        }

        /*
        for(double i : aggregatedScores) {
            System.out.println(i);
        }
        System.out.println();
        */

    }

    private Ranking<Integer> minimize(){

        prepareMinimizationArguments();
        Function toMinimize = new Function() {
            @Override
            public int getDimension() {
                return numItems;
            }

            @Override
            public double valueAt(double[] x) {
                double sum = 0;
                for(int i = 0; i < x.length; i++) {
                    for(int j = 0; j < x.length; j++) {
                        sum += Math.pow(x[j] - x[i] - aggregatedScores[i * x.length + j], 2);
                    }
                }
                return sum;
            }

            @Override
            public double[] gradientAt(double[] x) {

                double[] gradient = new double[x.length];

                for(int k = 0; k < x.length; k++) {
                    int gradientSum = 0;
                    for(int i = 0; i < x.length; i++) {
                        gradientSum +=  2*x[i] - 2*x[k] - 2*aggregatedScores[k*x.length + i] +
                                        2*x[k] - 2*x[i] -2*aggregatedScores[i*x.length + k];
                    }
                    gradient[k] = gradientSum;
                }
                return gradient;
            }
        };

        LbfgsMinimizer minimizer = new LbfgsMinimizer();
        minimizationResult = minimizer.minimize(toMinimize);


        for(double i : minimizationResult) {
            System.out.println(i);
        }


        List<Integer> sortedIds = Arrays.asList(items);
        //noinspection Since15
        sortedIds.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                if(minimizationResult[o1] > minimizationResult[o2]) {
                    return 1;
                } else if(minimizationResult[o1] == minimizationResult[o2]) {
                    return 0;
                } else {
                    return -1;
                }
            }
        });

        return new Ranking<Integer>(sortedIds);
    }
}
