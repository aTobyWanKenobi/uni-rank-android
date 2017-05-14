package com.example.albergon.unirank.Model;

import com.github.mikephil.charting.utils.ColorTemplate;

/**
 * This non instantiable class contains static integer fields representing colors in the format required by the chart library
 */
public class ChartColors {

    // private constructor
    private ChartColors() {}

    // general
    public static final int BLACK = ColorTemplate.rgb("#000000");
    public static final int WHITE = ColorTemplate.rgb("#FFFFFF");
    public static final int PALETTE3 = ColorTemplate.rgb("#66CCFF");

    // gender
    public static final int MALE_BLUE = ColorTemplate.rgb("#99CCFF");
    public static final int FEMALE_PINK = ColorTemplate.rgb("#FF99FF");

    // type
    public static final int HIGHSCHOOL_GREEN = ColorTemplate.rgb("#99FF99");
    public static final int UNIVERSITY_BLUE = ColorTemplate.rgb("#99FFFF");
    public static final int PARENTS_YELLOW = ColorTemplate.rgb("#FFFF99");
    public static final int OTHERS_VIOLET = ColorTemplate.rgb("#9999FF");

    // age
    public static final int KIDS_ORANGE = ColorTemplate.rgb("#FFB266");
    public static final int TEENS_MINT = ColorTemplate.rgb("#B2FF66");
    public static final int YOUNGS_RED = ColorTemplate.rgb("#FFF666");
    public static final int ADULTS_PURPLE = ColorTemplate.rgb("#6666FF");
    public static final int OLD_GREY = ColorTemplate.rgb("#C0C0C0");
}
