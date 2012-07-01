package in.partake.view;

import static in.partake.view.util.Helper.escapeTwitterResponse;
import static in.partake.view.util.Helper.h;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.view.util.Helper;

import org.junit.Assert;
import org.junit.Test;

public class ViewHelperTest {

    @Test
    public void testToEscapeHTML() {
        Assert.assertEquals("", h(""));
        Assert.assertEquals("", h(null));

        Assert.assertEquals(" ", h(" "));
        Assert.assertEquals("test", h("test"));

        Assert.assertEquals("&amp;", h("&"));
        Assert.assertEquals("&lt;", h("<"));
        Assert.assertEquals("&gt;", h(">"));
        Assert.assertEquals("&quot;", h("\""));
        Assert.assertEquals("&apos;", h("\'"));

        Assert.assertEquals("", h(Character.toString('\0')));      // NUL
        Assert.assertEquals("", h(Character.toString('\u202E')));  // RLO

        Assert.assertEquals("\t", h("\t"));
        Assert.assertEquals("\r", h("\r"));
        Assert.assertEquals("\n", h("\n"));
        Assert.assertEquals("\r\n", h("\r\n"));

        Assert.assertEquals("@screen_name", h("@screen_name"));
        Assert.assertEquals("#hashtag", h("#hashtag"));
        Assert.assertEquals("&amp;&lt;tag&gt;", h("&<tag>"));
        Assert.assertEquals("漢字＆ひらがな", h("漢字＆ひらがな"));
        Assert.assertEquals("サロゲートペア→𠮟", h("サロゲートペア→𠮟"));
        Assert.assertEquals("double &quot;quoted&quot;", h("double \"quoted\""));
        Assert.assertEquals("&lt;script&gt;&lt;/script&gt;", h("<script></script>"));
    }


    @Test
    public void testToCleanupHTML() throws Exception {
        String dirty = "<script>alert('hoge')</script>";
        String sanity = Helper.cleanupHTML(dirty);

        assertThat(sanity.contains("script"), is(false));
    }

    @Test
    public void testEscapeTwitterResponse() {
        Assert.assertEquals("", escapeTwitterResponse(""));
        Assert.assertEquals("", escapeTwitterResponse(null));

        Assert.assertEquals(" ", escapeTwitterResponse(" "));
        Assert.assertEquals("test", escapeTwitterResponse("test"));

        Assert.assertEquals("&amp;", escapeTwitterResponse("&"));
        Assert.assertEquals("&lt;", escapeTwitterResponse("<"));	// Twitterは返さないはずだけど脆弱性につながると嫌だし念の為にテスト
        Assert.assertEquals("&gt;", escapeTwitterResponse(">"));	// Twitterは返さないはずだけど脆弱性につながると嫌だし念の為にテスト
        Assert.assertEquals("&lt;", escapeTwitterResponse("&lt;"));
        Assert.assertEquals("&gt;", escapeTwitterResponse("&gt;"));
        Assert.assertEquals("&quot;", escapeTwitterResponse("\""));
        Assert.assertEquals("&apos;", escapeTwitterResponse("\'"));

        Assert.assertEquals("", escapeTwitterResponse(Character.toString('\0')));    	// NUL
        Assert.assertEquals("", escapeTwitterResponse(Character.toString('\u202E')));	// RLO

        Assert.assertEquals("\t", escapeTwitterResponse("\t"));
        Assert.assertEquals("\r", escapeTwitterResponse("\r"));
        Assert.assertEquals("\n", escapeTwitterResponse("\n"));
        Assert.assertEquals("\r\n", escapeTwitterResponse("\r\n"));

        Assert.assertEquals("@screen_name", escapeTwitterResponse("@screen_name"));
        Assert.assertEquals("#hashtag", escapeTwitterResponse("#hashtag"));
        Assert.assertEquals("&amp;&lt;tag&gt;", escapeTwitterResponse("&<tag>"));
        Assert.assertEquals("漢字＆ひらがな", escapeTwitterResponse("漢字＆ひらがな"));
        Assert.assertEquals("サロゲートペア→𠮟", escapeTwitterResponse("サロゲートペア→𠮟"));
        Assert.assertEquals("double &quot;quoted&quot;", escapeTwitterResponse("double \"quoted\""));
        Assert.assertEquals("&lt;script&gt;&lt;/script&gt;", escapeTwitterResponse("<script></script>"));
    }
}
