package in.partake.controller.action.image;

import in.partake.app.PartakeTestApp;
import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

import play.test.Helpers;

public class ImageActionTest extends ActionControllerTest {

    @Test
    public void testToGetImage() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/images/" + DEFAULT_IMAGE_ID);

        proxy.execute();
        assertResultSuccess(proxy);

        byte[] array = PartakeTestApp.getTestService().getTestDataProviderSet().getImageProvider().getDefaultImageContent();
        Assert.assertArrayEquals(array, Helpers.contentAsBytes(proxy.getResult()));
   }

    @Test
    public void testToGetWithInvalidId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/images/invalid");

        proxy.execute();
        assertResultNotFound(proxy);
    }

    @Test
    public void testToGetNonexistentImage() throws Exception {
        UUID uuid = new UUID(0, 0);
        ActionProxy proxy = getActionProxy(GET, "/images/" + uuid.toString());

        proxy.execute();
        assertResultNotFound(proxy);
    }
}
