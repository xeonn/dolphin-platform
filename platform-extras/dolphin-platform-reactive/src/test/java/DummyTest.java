import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created by hendrikebbers on 19.05.16.
 */
public class DummyTest {

    @Test
    public void testDummy() {
        assertThat(true, is(true));
    }
}
