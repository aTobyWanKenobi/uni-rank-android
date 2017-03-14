package com.example.albergon.unirank;

import com.example.albergon.unirank.Model.University;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for University class
 */
public class UniversityTest {

    @Test
    public void baseConstructorWorks() {

        University testUni = new University(0, "testName", "testCountry");

        Assert.assertEquals(0, testUni.getId());
        Assert.assertEquals("testName", testUni.getName());
        Assert.assertEquals("testCountry", testUni.getCountry());
    }

    @Test
    public void hasAcronymIsFalseWithBaseConstructor() {

        University testUni = new University(0, "testName", "testCountry");

        Assert.assertFalse(testUni.hasAcronym());
    }

    @Test
    public void acronymIsNullWithBaseConstructor() {

        University testUni = new University(0, "testName", "testCountry");

        Assert.assertEquals(null, testUni.getAcronym());
    }

    @Test(expected = IllegalArgumentException.class)
    public void baseConstructorThrowsIllegalOnNull() {
        new University(0, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void baseConstructorThrowsIllegalOnEmpty() {
        new University(0, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void baseConstructorThrowsIllegalOnNegativeId() {
        new University(-1, "testName", "testCountry");
    }

    @Test
    public void acronymConstructorWorks() {

        University testUni = new University(0, "testName", "testCountry", "testAcronym");

        Assert.assertEquals(0, testUni.getId());
        Assert.assertEquals("testName", testUni.getName());
        Assert.assertEquals("testCountry", testUni.getCountry());
        Assert.assertEquals("testAcronym", testUni.getAcronym());
    }

    @Test(expected = IllegalArgumentException.class)
    public void acronymConstructorThrowsIllegalOnNull() {
        new University(0, "testName", "testCountry", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void acronymConstructorThrowsIllegalOnEmpty() {
        new University(0, "testName", "testCountry", "");
    }

    @Test
    public void hasAcronymIsTrueWithAcronymConstructor() {

        University testUni = new University(0, "testName", "testCountry", "testAcronym");

        Assert.assertTrue(testUni.hasAcronym());
    }

    @Test
    public void equalsWorks() {

        University testUni1 = new University(0, "testName", "testCountry");
        University testUni2 = new University(0, "testName", "testCountry");
        University testUni3 = new University(0, "testName", "testCountry", "testAcronym");
        University testUni4 = new University(0, "testName", "testCountry", "testAcronym");

        Assert.assertTrue(testUni1.equals(testUni2));
        Assert.assertTrue(testUni3.equals(testUni4));

        University testUni5 = new University(1, "testName", "testCountry");
        University testUni6 = new University(0, "testName", "testCountry");
        University testUni7 = new University(1, "testName", "testCountry", "testAcronym");
        University testUni8 = new University(0, "testName", "testCountry", "testAcronym");

        Assert.assertFalse(testUni5.equals(testUni6));
        Assert.assertFalse(testUni7.equals(testUni8));
    }

    @Test
    public void hashCodeReturnsId() {

        University testUni = new University(1, "testName", "testCountry");

        Assert.assertEquals(1, testUni.hashCode());
    }

}
