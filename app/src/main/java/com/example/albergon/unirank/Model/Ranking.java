package com.example.albergon.unirank.Model;

import android.annotation.SuppressLint;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class models a ranking of a generic type. It simply encapsulates a sorted list and provides
 * only the necessary access methods to prevent accidental modifications.
 *
 * This class also has a static nested class which is a Comparator specific for a rank of universities
 * ids given a score for each university.
 */
public class Ranking<T> {

    private List<T> ranking = null;

    /**
     * Public constructor which correctly encapsulates the argument sorted list.
     *
     * @param ranks     sorted list that represents the ranking
     */
    public Ranking(List<T> ranks) {

        // arguments check
        if(ranks == null) {
            throw new IllegalArgumentException("List for Ranking cannot be null");
        }

        this.ranking = new ArrayList<>(ranks);
    }

    /**
     * Access method for the first position of this ranking.
     *
     * @return  the item that is at the top of this ranking
     */
    public T getHead() {
        return ranking.get(0);
    }

    /**
     * Returns the ranking as a list. Modifications to the list won't affect the Ranking object.
     *
     * @return  a list sorted accordingly to the ranking
     */
    public List<T> getList() {
        return new ArrayList<>(ranking);
    }

    //TODO: change ranking representation?
    @Override
    public String toString() {
        return ranking.toString();
    }

    /**
     * Nested static class that implements a comparator whose specific task is to order universities
     * ids based on a score associated to them. It's useful to sort lists which will be used to
     * instantiate rankings of universities ids.
     */
    public static class UniIdRankComparator implements Comparator<Integer> {

        private Map<Integer, Double> uniScores = null;

        /**
         * Public constructor, takes the universities ids and the scores associated to them.
         * This class assumes that the compare method will be called for lists whose entries appear
         * in the map stored in the object.
         *
         * @param uniScores     a map containing the scores of the universities
         */
        @SuppressLint("UseSparseArrays")
        public UniIdRankComparator(Map<Integer, Double> uniScores) {

            // arguments check
            if(uniScores == null) {
                throw new IllegalArgumentException("Scores map for comparator cannot be null");
            }

            this.uniScores = new HashMap<>(uniScores);
        }

        @Override
        public int compare(Integer o1, Integer o2) {

            // order is higher score down to lowest

            if(uniScores.get(o1) < uniScores.get(o2)) {
                return 1;
            } else if(uniScores.get(o1).equals(uniScores.get(o2))) {
                return 0;
            } else {
                return -1;
            }
        }
    }
}
