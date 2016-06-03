import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Until this module do not contain any "real" tests we need to add a dummy test for the build
 */
public class DummyTest {

    @Test
    public void testDummy() {
        assertThat(true, is(true));
    }
}
