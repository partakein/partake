package in.partake.base;

import java.util.HashMap;

import in.partake.base.ComparablePair;

import static org.hamcrest.Matchers.*;
import org.junit.Assert;
import org.junit.Test;

public class PairTest {

    @Test
    public void testCreateAndGet() {
        ComparablePair<Integer, Integer> pair = new ComparablePair<Integer, Integer>(1, 2);
        Assert.assertEquals(Integer.valueOf(1), pair.getFirst());
        Assert.assertEquals(Integer.valueOf(2), pair.getSecond());
    }

    @Test
    public void testEquals() {
        ComparablePair<Integer, Integer> pair1 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair2 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair3 = new ComparablePair<Integer, Integer>(1, 3);
        ComparablePair<Integer, Integer> pair4 = new ComparablePair<Integer, Integer>(2, 2);
        
        Assert.assertEquals(pair1, pair2);
        Assert.assertFalse(pair1.equals(pair3));
        Assert.assertFalse(pair1.equals(pair4));
        Assert.assertFalse(pair1.equals(null));
    }
    
    @Test
    public void testCompare() {
        ComparablePair<Integer, Integer> pair1 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair2 = new ComparablePair<Integer, Integer>(1, 2);
        ComparablePair<Integer, Integer> pair3 = new ComparablePair<Integer, Integer>(1, 3);
        ComparablePair<Integer, Integer> pair4 = new ComparablePair<Integer, Integer>(2, 2);
        ComparablePair<Integer, Integer> pair5 = new ComparablePair<Integer, Integer>(0, 2);
        
        Assert.assertThat(pair1.compareTo(pair2), is(0));
        Assert.assertThat(pair1.compareTo(pair3), is(lessThan(0)));
        Assert.assertThat(pair1.compareTo(pair4), is(lessThan(0)));
        Assert.assertThat(pair1.compareTo(pair5), is(greaterThan(0)));
    }

    @Test(expected = NullPointerException.class)
    public void testCompareToNull() {
    	new ComparablePair<Integer, Integer>(1, 2).compareTo(null);
    }
    
    @Test
    public void testForHashMap() {
        HashMap<Pair<String, String>, String> map = new HashMap<Pair<String,String>, String>();
        
        map.put(new Pair<String, String>("s1", "t1"), "v1");
        map.put(new Pair<String, String>("s1", "t2"), "v2");
        map.put(new Pair<String, String>("s2", "t1"), "v3");
        map.put(new Pair<String, String>("s2", "t2"), "v4");

        Assert.assertThat(map.get(new Pair<String, String>("s1", "t1")), is("v1"));
        Assert.assertThat(map.get(new Pair<String, String>("s1", "t2")), is("v2"));
        Assert.assertThat(map.get(new Pair<String, String>("s2", "t1")), is("v3"));
        Assert.assertThat(map.get(new Pair<String, String>("s2", "t2")), is("v4"));
    }
}
