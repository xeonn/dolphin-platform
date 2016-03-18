package com.canoo.dolphin.server.context;

import com.canoo.dolphin.server.DolphinSession;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Created by hendrikebbers on 18.03.16.
 */
public class DolphinSessionImplTest {

    @Test
    public void testid() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //then:
        Assert.assertEquals("test-id", dolphinSession.getId());
    }

    @Test
    public void testAddAttribute() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");

        //then:
        Assert.assertEquals(1, dolphinSession.getAttributeNames().size());
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute"));
        Assert.assertEquals("Hello Dolphin Session", dolphinSession.getAttribute("test-attribute"));
    }

    @Test
    public void testNullAttribute() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertNull(dolphinSession.getAttribute("test-attribute"));
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //then:
        dolphinSession.getAttributeNames().add("att");
    }

    @Test(expectedExceptions = UnsupportedOperationException.class)
    public void testImmutableAttributeSet2() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");

        //then:
        dolphinSession.getAttributeNames().remove("test-attribute");
    }

    @Test
    public void testRemoveAttribute() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //when:
        dolphinSession.setAttribute("test-attribute", "Hello Dolphin Session");
        dolphinSession.removeAttribute("test-attribute");

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertNull(dolphinSession.getAttribute("test-attribute"));
    }

    @Test
    public void testMultipleAttributes() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //when:
        dolphinSession.setAttribute("test-attribute1", "Hello Dolphin Session");
        dolphinSession.setAttribute("test-attribute2", "Yeah!");
        dolphinSession.setAttribute("test-attribute3", "Dolphin Platform");

        //then:
        Assert.assertEquals(3, dolphinSession.getAttributeNames().size());
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute1"));
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute2"));
        Assert.assertTrue(dolphinSession.getAttributeNames().contains("test-attribute3"));
        Assert.assertEquals("Hello Dolphin Session", dolphinSession.getAttribute("test-attribute1"));
        Assert.assertEquals("Yeah!", dolphinSession.getAttribute("test-attribute2"));
        Assert.assertEquals("Dolphin Platform", dolphinSession.getAttribute("test-attribute3"));
    }

    @Test
    public void testInvalidate() {
        //given:
        DolphinSession dolphinSession = new DolphinSessionImpl("test-id");

        //when:
        dolphinSession.setAttribute("test-attribute1", "Hello Dolphin Session");
        dolphinSession.setAttribute("test-attribute2", "Yeah!");
        dolphinSession.setAttribute("test-attribute3", "Dolphin Platform");
        dolphinSession.invalidate();

        //then:
        Assert.assertEquals(0, dolphinSession.getAttributeNames().size());
        Assert.assertFalse(dolphinSession.getAttributeNames().contains("test-attribute1"));
        Assert.assertFalse(dolphinSession.getAttributeNames().contains("test-attribute2"));
        Assert.assertFalse(dolphinSession.getAttributeNames().contains("test-attribute3"));
        Assert.assertNull(dolphinSession.getAttribute("test-attribute1"));
        Assert.assertNull(dolphinSession.getAttribute("test-attribute2"));
        Assert.assertNull(dolphinSession.getAttribute("test-attribute3"));
    }
}
