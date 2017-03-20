package com.example.albergon.unirank;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.albergon.unirank.Database.DatabaseHelper;
import com.example.albergon.unirank.Model.Indicator;
import com.example.albergon.unirank.Model.University;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Created by Tobia Albergoni on 20.03.2017.
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
        University harvard = databaseHelper.getUniversity(2);

        Assert.assertEquals("Harvard University", harvard.getName());
        Assert.assertEquals("USA", harvard.getCountry());
        Assert.assertEquals(null, harvard.getAcronym());
        Assert.assertEquals(2, harvard.getId());
    }

    @Test
    public void canRetrieveUniversityWithAcronym() {
        University epfl = databaseHelper.getUniversity(122);

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
}
