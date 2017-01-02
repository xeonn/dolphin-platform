package com.canoo.dolphin.impl;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by hendrikebbers on 30.12.16.
 */
public class IdentitySetTest {

    @Test
    public void testSize() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());

        Assert.assertTrue(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 0);


        identitySet.add(date1);
        Assert.assertFalse(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 1);

        identitySet.add(date2);
        Assert.assertFalse(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 2);

        identitySet.remove(date2);
        Assert.assertFalse(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 1);

        identitySet.clear();
        Assert.assertTrue(identitySet.isEmpty());
        Assert.assertEquals(identitySet.size(), 0);
    }

    @Test
    public void testContains() {

        IdentitySet<Date> identitySet = new IdentitySet<>();

        Date date1 = new Date();
        Date date2 = new Date(date1.getTime());

        Assert.assertFalse(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.add(date1);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.add(date2);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertTrue(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.remove(date2);
        Assert.assertTrue(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertTrue(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));

        identitySet.clear();
        Assert.assertFalse(identitySet.contains(date1));
        Assert.assertFalse(identitySet.contains(date2));
        Assert.assertTrue(identitySet.containsAll(new ArrayList<>()));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date1, date2)));
        Assert.assertFalse(identitySet.containsAll(Arrays.asList(date2, date1)));
    }

}
