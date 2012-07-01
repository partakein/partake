package in.partake.base;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class KeyValuePairTest {

    @Test
    public void testCreateAndGet() {
        KeyValuePair pair = new KeyValuePair("key", "value");
        
        assertThat(pair.getKey(), is("key"));
        assertThat(pair.getValue(), is("value"));
    }
}
