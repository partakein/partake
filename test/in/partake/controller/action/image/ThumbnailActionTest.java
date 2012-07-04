package in.partake.controller.action.image;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;

import java.util.UUID;

import org.junit.Test;

public class ThumbnailActionTest extends ActionControllerTest {

    @Test
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/images/thumbnail/" + DEFAULT_IMAGE_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        // TODO: do some test here.
    }

    @Test
    public void testToGetImageHavingNoThumbnail() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/images/thumbnail/" + IMAGE_HAVING_NO_THUMBNAIL_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        assertThat(loadThumbnail(IMAGE_HAVING_NO_THUMBNAIL_ID), is(notNullValue()));
    }

    @Test
    public void testToGetWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/images/thumbnail/invalid");

        proxy.execute();
        assertResultNotFound(proxy);
    }

    @Test
    public void testToGetNonexistentImage() throws Exception {
        UUID uuid = new UUID(0, 0);
        ActionProxy proxy = getActionProxy(GET, "/images/thumbnail/" + uuid.toString());

        proxy.execute();
        assertResultNotFound(proxy);
    }
}
