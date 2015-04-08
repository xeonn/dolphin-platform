package com.canoo.dolphin.server.impl;

import com.canoo.dolphin.server.impl.collections.ObservableArrayList;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestObservableArrayListSublist {

    // TODO Test for modification (incl. listeners)

    @Test
    public void sublistOnEmptyList_shouldReturnEmptyList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>();

        final List<String> sublist = list.subList(0, 0);

        assertThat(sublist, empty());
    }

    @Test
    public void sublistOnNonEmptyList_shouldReturnSubList() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        final List<String> sublist = list.subList(1, 2);

        assertThat(sublist, is(Arrays.asList("2")));
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void sublistWithNegativeStart_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.subList(-1, 0);
    }

    @Test (expectedExceptions = IndexOutOfBoundsException.class)
    public void sublistWithTooLargeEnd_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.subList(0, 4);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void sublistWithOutOfOrderIndexes_shouldThrowException() {
        final ObservableArrayList<String> list = new ObservableArrayList<>("1", "2", "3");

        list.subList(2, 1);
    }
}
