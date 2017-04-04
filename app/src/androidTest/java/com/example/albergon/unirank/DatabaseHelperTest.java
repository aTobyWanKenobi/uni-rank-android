package com.example.albergon.unirank;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.SaveRank;
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
        databaseHelper = new DatabaseHelper(testContext);
        try {
            databaseHelper.createDatabase();
        } catch (IOException e) {

        }
        databaseHelper.openDatabase();
    }

    @Test
    public void createAndOpenDatabaseAllowsAccess() {
        Assert.assertTrue(databaseHelper.databaseExists());
    }

    @Test
    public void canRetrieveUniveristy() {
        University harvard = databaseHelper.getUniversity(3);

        Assert.assertEquals("Harvard University", harvard.getName());
        Assert.assertEquals("USA", harvard.getCountry());
        Assert.assertEquals(null, harvard.getAcronym());
        Assert.assertEquals(3, harvard.getId());
    }

    @Test
    public void canRetrieveUniversityWithAcronym() {
        University epfl = databaseHelper.getUniversity(14);

        Assert.assertEquals("EPFL", epfl.getAcronym());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getUniversityThrowsIllegal() {
        databaseHelper.getUniversity(-1);
    }

    @Test(expected = NoSuchElementException.class)
    public void getUniversityThrowsNoSuch() {
        databaseHelper.getUniversity(59999);
    }

    @Test
    public void getIndicatorWorks() {
        Indicator academicReputation = databaseHelper.getIndicator(0);

        Assert.assertEquals(0, academicReputation.getId());
        Assert.assertEquals(academicReputation.getIdSet().size(), academicReputation.getSize());
    }

    @Test
    public void saveAndRetrieveAggregation() {

        List<Integer> rankList = new ArrayList<>();
        rankList.add(12);
        rankList.add(3);

        Map<Integer, Integer> settings = new HashMap<>();
        settings.put(1, 3);
        settings.put(2, 2);

        SaveRank testSave = new SaveRank("TestName", "TestDate", settings, rankList);

        databaseHelper.saveAggregation(testSave);

        List<SaveRank> savings = databaseHelper.fetchAllSaves();

        Assert.assertEquals("TestName", savings.get(0).getName());
        Assert.assertEquals("TestDate", savings.get(0).getDate());
    }
}
