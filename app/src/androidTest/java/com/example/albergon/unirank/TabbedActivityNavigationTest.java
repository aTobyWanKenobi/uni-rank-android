package com.example.albergon.unirank;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test suite for tabbed navigation
 */
@RunWith(AndroidJUnit4.class)
public class TabbedActivityNavigationTest  {

    @Rule
    public ActivityTestRule<TabbedActivity> activityTestRule = new ActivityTestRule<TabbedActivity>(
            TabbedActivity.class
    );

    @Test
    public void changeTabToGeneration() {

        onView(withId(R.id.create_tab)).perform(click());

        onView(withId(R.id.create_tab))
                .check(matches(isDisplayed()));
    }
}
