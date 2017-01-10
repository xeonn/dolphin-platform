package com.canoo.dolphin.server.event;

import org.testng.Assert;
import org.testng.annotations.Test;

public class TestTopic {

    @Test
    public void testUniqueId() {
        Topic<String> topicA = Topic.create();
        for (int i = 0; i < 1000; i++) {
            Topic<String> topicB = Topic.create();
            Assert.assertNotEquals(topicA, topicB);
        }
    }

    @Test
    public void testEquals() {
        Topic<String> topicA = Topic.create("TopicA");
        Topic<String> topicB = Topic.create("TopicA");
        Assert.assertEquals(topicA, topicB);
    }

    @Test
    public void testName() {
        Topic<String> topicA = Topic.create("TopicA");
        Topic<String> topicB = Topic.create("TopicB");
        Assert.assertEquals(topicA.getName(), "TopicA");
        Assert.assertEquals(topicB.getName(), "TopicB");
    }


}
