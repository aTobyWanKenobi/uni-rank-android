package com.example.albergon.unirank.Fragments;

/**
 * This interface is used to instantiate a button handler for the my rank fragment which will perform
 * the necessary callbacks to the fragment when the icons are pressed in the adapter.
 */
public interface MyRankingButtonHandler {

    void open(String name);

    void compare(String name);

    void share(String name);

    void delete(String name);
}
