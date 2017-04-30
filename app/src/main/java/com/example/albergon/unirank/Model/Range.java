package com.example.albergon.unirank.Model;

/**
 * This immutable class models a range of integer values and provides ways to check if values
 * fall within the range. The range is inclusive for the minimum and exclusive for the maximum.
 */
public class Range {

    private final int min;
    private final int max;

    public Range(int min, int max) {

        this.min = min;
        this.max = max;
    }

    /**
     * Check if a value belongs to the range. Inclusive for the lower bound and exclusive for the
     * upper bound;
     *
     * @param value     integer to check
     * @return          whether value falls within the range
     */
    public boolean within(int value) {
        return (value >= min && value < max);
    }
}
