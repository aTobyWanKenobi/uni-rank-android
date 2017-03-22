package com.example.albergon.unirank.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tobia Albergoni on 20.03.2017.
 */
public class Ranking<T> {

    private List<T> ranking = null;

    public Ranking(List<T> ranks) {

        // arguments check
        if(ranks == null) {
            throw new IllegalArgumentException("List for Ranking cannot be null");
        }

        this.ranking = new ArrayList<>(ranks);
    }

    public T getHead() {
        return ranking.get(0);
    }

    public List<T> getList() {
        return new ArrayList<>(ranking);
    }

    //TODO: change ranking representation?
    @Override
    public String toString() {
        return ranking.toString();
    }
}
