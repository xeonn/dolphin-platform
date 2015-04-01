package com.canoo.dolphin.collections;

import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class TestListChangeEvent {

    @Test
    public void newElementsButNoRemovedElements_shouldReturnIsAddedOnly() {
        final ObservableArrayList<String> source = new ObservableArrayList<>("Hello", "World");
        final ListChangeEvent<String> event = new ListChangeEvent<String>(source) {
            @Override
            public List<Change> getChanges() {
                return Collections.singletonList(new Change(0, 1, Collections.<String>emptyList()));
            }
        };

        final ListChangeEvent<String>.Change change = event.getChanges().get(0);

        assertThat(change.isAdded(), is(true));
        assertThat(change.isRemoved(), is(false));
        assertThat(change.isReplaced(), is(false));
        assertThat(change.getAddedElements(), is(Arrays.asList("Hello")));
    }

    @Test
    public void noNewElementsButRemovedElements_shouldReturnIsRemovedOnly() {
        final ObservableArrayList<String> source = new ObservableArrayList<>("Hello", "World");
        final ListChangeEvent<String> event = new ListChangeEvent<String>(source) {
            @Override
            public List<Change> getChanges() {
                return Collections.singletonList(new Change(1, 1, Collections.singletonList("Goodbye")));
            }
        };

        final ListChangeEvent<String>.Change change = event.getChanges().get(0);

        assertThat(change.isAdded(), is(false));
        assertThat(change.isRemoved(), is(true));
        assertThat(change.isReplaced(), is(false));
        assertThat(change.getAddedElements(), empty());
    }

    @Test
    public void newElementsAndRemovedElements_shouldReturnIsReplacedOnly() {
        final ObservableArrayList<String> source = new ObservableArrayList<>("Hello", "World");
        final ListChangeEvent<String> event = new ListChangeEvent<String>(source) {
            @Override
            public List<Change> getChanges() {
                return Collections.singletonList(new Change(0, 1, Collections.singletonList("Goodbye")));
            }
        };

        final ListChangeEvent<String>.Change change = event.getChanges().get(0);

        assertThat(change.isAdded(), is(false));
        assertThat(change.isRemoved(), is(false));
        assertThat(change.isReplaced(), is(true));
        assertThat(change.getAddedElements(), is(Arrays.asList("Hello")));
    }
}
