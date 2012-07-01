package in.partake.base;


import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Test;


public class UtilTest {

    @Test
    public void testIsUUID() {
        assertThat(Util.isUUID(null), is(false));
        assertThat(Util.isUUID(""), is(false));
        assertThat(Util.isUUID("something"), is(false));
        assertThat(Util.isUUID("00000000-0000-0000-0000-000000000000"), is(true));
        assertThat(Util.isUUID("xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx"), is(false));
    }

    @Test
    public void testEnsureMin() {
        assertThat(Util.ensureMin(3, 0), is(3));
        assertThat(Util.ensureMin(-1, 0), is(0));
        assertThat(Util.ensureMin(3, Integer.MAX_VALUE), is(Integer.MAX_VALUE));

        assertThat(Util.ensureMin(3L, 0L), is(3L));
        assertThat(Util.ensureMin(-1L, 0L), is(0L));
        assertThat(Util.ensureMin(3L, Long.MAX_VALUE), is(Long.MAX_VALUE));
    }

    @Test
    public void testToJSONArray() {
        class X implements JSONable, SafeJSONable {
            private String value;

            public X(String value) {
                this.value = value;
            }

            @Override
            public JSONObject toJSON() {
                JSONObject obj = new JSONObject();
                obj.put("value", value);
                return obj;
            }

            @Override
            public JSONObject toSafeJSON() {
                JSONObject obj = new JSONObject();
                obj.put("safe", value);
                return obj;
            }
        }

        List<X> xs = new ArrayList<X>();
        xs.add(new X("0"));
        xs.add(new X("1"));
        xs.add(new X("2"));

        JSONArray array = Util.toJSONArray(xs);
        JSONArray safes = Util.toSafeJSONArray(xs);

        assertThat(array.size(), is(3));
        assertThat(array.getJSONObject(0).getString("value"), is("0"));
        assertThat(array.getJSONObject(1).getString("value"), is("1"));
        assertThat(array.getJSONObject(2).getString("value"), is("2"));

        assertThat(safes.size(), is(3));
        assertThat(safes.getJSONObject(0).getString("safe"), is("0"));
        assertThat(safes.getJSONObject(1).getString("safe"), is("1"));
        assertThat(safes.getJSONObject(2).getString("safe"), is("2"));
    }

    @Test
    public void testToParseEnqueteAnswers() {
        UUID[] ids = new UUID[5];
        for (int i = 0; i < 5; ++i)
            ids[i] = new UUID(0, i);

        JSONObject obj = JSONObject.fromObject("{ \""+ids[0].toString()+"\": [\"hoge\", \"fuga\"], " +
                "\""+ids[1].toString()+"\": [1, 2, 3], " +
                "\""+ids[2].toString()+"\": [], " +
                "\""+ids[3].toString()+"\": \"\", " +
                "\""+ids[4].toString()+"\": 3, " +
                "}");

        Map<UUID, List<String>> converted = Util.parseEnqueteAnswers(obj);

        List<String> hogefuga = new ArrayList<String>();
        hogefuga.add("hoge");
        hogefuga.add("fuga");

        assertThat(converted.get(ids[0]).size(), is(2));
        assertThat(converted.get(ids[0]), hasItem("hoge"));
        assertThat(converted.get(ids[0]), hasItem("fuga"));
        assertThat(converted.get(ids[1]).size(), is(3));
        assertThat(converted.get(ids[1]), hasItem("1"));
        assertThat(converted.get(ids[1]), hasItem("2"));
        assertThat(converted.get(ids[1]), hasItem("3"));
        assertThat(converted.get(ids[2]).size(), is(0));
        assertThat(converted.get(ids[3]), nullValue());
        assertThat(converted.get(ids[4]), nullValue());
    }

    @Test
    public void testEnsureRange() {
        assertThat(Util.ensureRange(10, 0, 100), is(10));
        assertThat(Util.ensureRange(-10, 0, 100), is(0));
        assertThat(Util.ensureRange(110, 0, 100), is(100));
        assertThat(Util.ensureRange(0, 0, 100), is(0));
        assertThat(Util.ensureRange(1000, 0, 100), is(100));
        assertThat(Util.ensureRange(Integer.MIN_VALUE, 0, Integer.MAX_VALUE), is(0));
        assertThat(Util.ensureRange(Integer.MAX_VALUE, 0, Integer.MAX_VALUE), is(Integer.MAX_VALUE));
        assertThat(Util.ensureRange(0, 1, 100), is(1));
    }

    @Test
    public void hashtagValidatorTest() {
        Assert.assertTrue(Util.isValidHashtag("#hashtag"));
        Assert.assertTrue(Util.isValidHashtag("#hash_tag"));
        Assert.assertTrue(Util.isValidHashtag("#hashtag1"));
        Assert.assertTrue(Util.isValidHashtag("#hÀshtag"));
        Assert.assertTrue(Util.isValidHashtag("＃hashtag"));
        Assert.assertTrue(Util.isValidHashtag("#hashタグ"));
        Assert.assertTrue(Util.isValidHashtag("#ﾊｯｼｭﾀｸﾞ"));
        Assert.assertTrue(Util.isValidHashtag("#À"));

        Assert.assertFalse(Util.isValidHashtag("#012"));

        Assert.assertFalse(Util.isValidHashtag("#らき☆すた"));
        Assert.assertFalse(Util.isValidHashtag("#まどか☆マギカ"));
        Assert.assertFalse(Util.isValidHashtag("#hash\\tag"));
        Assert.assertFalse(Util.isValidHashtag("#hash-tag"));

        Assert.assertFalse(Util.isValidHashtag("これは#ダメ"));
        Assert.assertFalse(Util.isValidHashtag("これも、#ダメ"));
    }

    @Test
    public void shortenAlphabetTest() {
        Assert.assertEquals("ABCAB", Util.shorten("ABCAB", 6));
        Assert.assertEquals("ABCABC", Util.shorten("ABCABC", 6));
        Assert.assertEquals("ABC...", Util.shorten("ABCABCD", 6));
        Assert.assertEquals("ABC...", Util.shorten("ABCABCDE", 6));

        Assert.assertEquals("", Util.shorten("ABCABC", 0));
        Assert.assertEquals(".", Util.shorten("ABCABC", 1));
        Assert.assertEquals("..", Util.shorten("ABCABC", 2));
        Assert.assertEquals("...", Util.shorten("ABCABC", 3));
    }

    @Test
    public void shortenJapaneseTest() {
        Assert.assertEquals("日本語", Util.shorten("日本語", 6));
        Assert.assertEquals("日本語...", Util.shorten("日本語は難しい", 6));
        Assert.assertEquals("日本語...", Util.shorten("日本語難しすぎ", 6));
        Assert.assertEquals("日本語...", Util.shorten("日本語aほえほえ", 6));
    }

    @Test
    public void shortenSurrogatePairTest() {
        Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.shorten("𠮟𠮟𠮟𠮟𠮟𠮟", 6));
        Assert.assertEquals("𠮟𠮟𠮟...", Util.shorten("𠮟𠮟𠮟𠮟𠮟𠮟𠮟", 6));
        Assert.assertEquals("a𠮟𠮟...", Util.shorten("a𠮟𠮟𠮟𠮟𠮟𠮟𠮟", 6));
    }

    @Test(expected = NullPointerException.class)
    public void shortenNullValueTest() {
        Util.shorten(null, 0);
    }

    @Test
    public void shortenNegativeValueTest() {
        Assert.assertEquals("", Util.shorten("", -1));
    }

    @Test
    public void testToRemoveHash() {
        Assert.assertEquals(null, Util.removeURLFragment(null));
        Assert.assertEquals("", Util.removeURLFragment(""));
        Assert.assertEquals("abc", Util.removeURLFragment("abc"));
        Assert.assertEquals("日本語", Util.removeURLFragment("日本語"));
        Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeURLFragment("𠮟𠮟𠮟𠮟𠮟𠮟"));
        Assert.assertEquals("", Util.removeURLFragment("#hoge"));
        Assert.assertEquals("", Util.removeURLFragment("#日本語"));
        Assert.assertEquals("", Util.removeURLFragment("#𠮟𠮟𠮟𠮟𠮟𠮟"));
        Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeURLFragment("𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟"));
        Assert.assertEquals("𠮟𠮟𠮟𠮟𠮟𠮟", Util.removeURLFragment("𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟#𠮟𠮟𠮟𠮟𠮟𠮟"));
    }

    @Test
    public void removeTagsTest() {
        Assert.assertEquals("abc", Util.removeTags("abc"));
        Assert.assertEquals("abc", Util.removeTags("<p>abc</p>"));
        Assert.assertEquals("abc", Util.removeTags("abc<br />"));
        Assert.assertEquals("abc", Util.removeTags("ab<br />c"));
        Assert.assertEquals("abc", Util.removeTags("<br />abc"));
        Assert.assertEquals("abc", Util.removeTags("abc<br>"));
        Assert.assertEquals("abc", Util.removeTags("<br>abc"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment -->"));
        Assert.assertEquals("abc", Util.removeTags("<!-- comment -->abc"));
        Assert.assertEquals("ab\r\nc", Util.removeTags("<p>ab\r\nc</p>"));
        Assert.assertEquals("ab\r\nc", Util.removeTags("<p \r\n>ab\r\nc</p>"));
        Assert.assertEquals("ab\r\nc", Util.removeTags("<p>ab\r\nc</p \r\n>"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment << hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment <> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >< hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >\n> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >>\n hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>\n> hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment >\n>\n hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>>\n hoge -->"));
        Assert.assertEquals("abc", Util.removeTags("abc<!-- comment \n>\n>\n hoge -->"));
    }

    @Test
    public void testEncodeURIComponent() {
        Assert.assertEquals("", Util.encodeURIComponent(""));
        Assert.assertEquals("%20!%22%23%24%25%26'()*%2B%2C-.%2F%3B%3F%3A%40%3D~", Util.encodeURIComponent(" !\"#$%&'()*+,-./;?:@=~"));
        Assert.assertEquals("Thyme%20%26time%3Dagain", Util.encodeURIComponent("Thyme &time=again"));
        Assert.assertEquals("%2521", Util.encodeURIComponent("%21"));
    }
}
