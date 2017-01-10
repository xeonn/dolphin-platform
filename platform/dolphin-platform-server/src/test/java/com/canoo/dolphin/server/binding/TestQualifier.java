package com.canoo.dolphin.server.binding;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestQualifier {

    @Test
    public void testUniqueId() {
        Qualifier<String> qualifierA = Qualifier.create();
        for (int i = 0; i < 1000; i++) {
            Qualifier<String> qualifierB = Qualifier.create();
            Assert.assertNotEquals(qualifierA, qualifierB);
        }
    }

    @Test
    public void testEquals() {
        Qualifier<String> qualifierA = Qualifier.create("QualifierA");
        Qualifier<String> qualifierB = Qualifier.create("QualifierA");
        Assert.assertEquals(qualifierA, qualifierB);
    }

    @Test
    public void testName() {
        Qualifier<String> qualifierA = Qualifier.create("QualifierA");
        Qualifier<String> qualifierB = Qualifier.create("QualifierB");
        Assert.assertEquals(qualifierA.getIdentifier(), "QualifierA");
        Assert.assertEquals(qualifierB.getIdentifier(), "QualifierB");
    }
}
