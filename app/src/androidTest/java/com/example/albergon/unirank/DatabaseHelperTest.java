package com.example.albergon.unirank;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Database.Tables;
import com.example.albergon.unirank.Model.Enums;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.Ranking;
import com.example.albergon.unirank.Model.SaveRank;
import com.example.albergon.unirank.Model.Settings;
import com.example.albergon.unirank.Model.University;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Test suite for DatabaseHelper
 */
public class DatabaseHelperTest {

    DatabaseHelper databaseHelper = null;

    @Before
    public void setUp() {
        Context testContext = InstrumentationRegistry.getTargetContext();
        databaseHelper = DatabaseHelper.getInstance(testContext);
    }

    @Test
    public void createAndOpenDatabaseAllowsAccess() {
        Assert.assertTrue(databaseHelper.databaseExists());
    }

    @Test
    public void canRetrieveUniversity() {
        University harvard = databaseHelper.retrieveUniversity(3);

        Assert.assertEquals("Harvard University", harvard.getName());
        Assert.assertEquals("USA", harvard.getCountry());
        Assert.assertEquals(null, harvard.getAcronym());
        Assert.assertEquals(3, harvard.getId());
    }

    @Test
    public void canRetrieveUniversityWithAcronym() {
        University epfl = databaseHelper.retrieveUniversity(14);

        Assert.assertEquals("EPFL", epfl.getAcronym());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUniversityThrowsIllegal() {
        databaseHelper.retrieveUniversity(-1);
    }

    @Test(expected = NoSuchElementException.class)
    public void getUniversityThrowsNoSuch() {
        databaseHelper.retrieveUniversity(59999);
    }

    @Test
    public void getIndicatorWorks() {
        Indicator academicReputation = databaseHelper.retrieveIndicator(0);

        Assert.assertEquals(0, academicReputation.getId());
        Assert.assertEquals(academicReputation.getIdSet().size(), academicReputation.getSize());
    }

    @Test
    public void saveAndRetrieveAggregation() {

        databaseHelper.deleteSavedAggregation("TestName");

        List<Integer> rankList = new ArrayList<>();
        rankList.add(12);
        rankList.add(3);

        Map<Integer, Integer> settings = new HashMap<>();
        settings.put(1, 3);
        settings.put(3, 2);
        settings.put(2, 2);

        Ranking<Integer> ranking = new Ranking<Integer>(rankList);

        SaveRank testSave = new SaveRank("TestName", "TestDate", settings, ranking);

        databaseHelper.saveAggregation(testSave);

        SaveRank save = databaseHelper.retrieveSave("TestName");

        Assert.assertEquals("TestName", save.getName());
        Assert.assertEquals("TestDate", save.getDate());

        Assert.assertEquals(3, (int) save.getSettings().get(1));
        Assert.assertEquals(2, (int) save.getSettings().get(2));

        databaseHelper.deleteSavedAggregation("TestName");
    }


    @Test
    public void saveAndRetrieveSettingsWorks() {

        Settings toSave = new Settings("CHE", Enums.GenderEnum.MALE, 1994, Enums.TypesOfUsers.HighSchoolStudent);

        databaseHelper.saveSettings(toSave, true);

        Settings retrieved = databaseHelper.retriveSettings(true);

        Assert.assertEquals(toSave.getCountryCode(), retrieved.getCountryCode());
        Assert.assertEquals(toSave.getGender(), retrieved.getGender());
        Assert.assertEquals(toSave.getYearOfBirth(), retrieved.getYearOfBirth());
        Assert.assertEquals(toSave.getType(), retrieved.getType());
    }
}
