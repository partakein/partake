package in.partake.resource;

import java.util.Locale;

import in.partake.resource.I18n;

import org.junit.Assert;
import org.junit.Test;

public class I18nTest {

    @Test
    public void testGetJapaneseResource() {
        String actual = I18n.get().getBundle(Locale.JAPANESE).getString("invalid.invalid_userid");
        Assert.assertEquals("無効な UserID です。", actual);
    }

    @Test
    public void testGetEnglishResource() {
        String actual = I18n.get().getBundle(Locale.ENGLISH).getString("invalid.invalid_userid");
        Assert.assertEquals("Invalid User ID", actual);
    }

    @Test
    public void testGetOtherResource() {
        // Try Chinese
        String actual = I18n.get().getBundle(Locale.CHINESE).getString("invalid.invalid_userid");
        String expected = I18n.get().getBundle().getString("invalid.invalid_userid");

        // Since the current implementation does not handle CHINESE, the string of the default locale should be returned.
        Assert.assertEquals(expected, actual);
    }

}
