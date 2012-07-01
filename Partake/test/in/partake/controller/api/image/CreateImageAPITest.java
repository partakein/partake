package in.partake.controller.api.image;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserImage;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;

import in.partake.controller.ActionProxy;

public class CreateImageAPITest extends APIControllerTest {

    @Before
    public void setUp() throws Exception {
        super.setUp();

        new Transaction<Void>() {
            @Override
            protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                daos.getImageAccess().truncate(con);
                return null;
            }
        }.execute();
    }

    @Test
    public void testToCreate() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, DEFAULT_USER_ID);
        api.setFileContentType("image/png");
        api.setFile(new File("src/test/resources/images/musangas.png"));

        proxy.execute();
        assertResultOK(proxy);

        JSONObject json = getJSON(proxy);
        String imageId = json.getString("imageId");

        assertThat(imageId, is(json.getJSONArray("imageIds").getString(0)));

        UserImage imageData = loadImage(imageId);
        assertThat(imageData.getUserId(), is(DEFAULT_USER_ID));
    }

    @Test
    public void testToCreateWithPJpeg() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        addValidSessionTokenToParameter(proxy);
        loginAs(proxy, DEFAULT_USER_ID);
        api.setFileContentType("image/pjpeg");
        api.setFile(new File("src/test/resources/images/musangas.jpg"));

        proxy.execute();
        assertResultOK(proxy);

        JSONObject json = getJSON(proxy);
        String imageId = json.getString("imageId");

        UserImage imageData = loadImage(imageId);
        assertThat(imageData.getType(), is("image/jpeg"));
    }

    @Test
    public void testToCreateWithLimit() throws Exception {
        String eventId1 = storeImage();
        TimeUtil.waitForTick();
        String eventId2 = storeImage();
        TimeUtil.waitForTick();

        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        addValidSessionTokenToParameter(proxy);
        addParameter(proxy, "limit", "10");
        loginAs(proxy, DEFAULT_USER_ID);
        api.setFileContentType("image/png");
        api.setFile(new File("src/test/resources/images/musangas.png"));
        proxy.execute();
        String eventId3 = getJSON(proxy).getString("imageId");

        assertResultOK(proxy);
        JSONArray array = getJSON(proxy).getJSONArray("imageIds");
        assertThat(array.size(), is(3));

        List<String> expect = Arrays.asList(new String[] { eventId3, eventId2, eventId1});
        List<String> actual = Arrays.asList(new String[] { array.getString(0), array.getString(1), array.getString(2) });
        assertThat(actual, is(expect));
    }

    @Test
    public void testToCreateWithIOException() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        addValidSessionTokenToParameter(proxy);

        loginAs(proxy, DEFAULT_USER_ID);
        api.setFileContentType("image/png");
        api.setFile(new File("no-image"));

        proxy.execute();
        assertResultError(proxy, ServerErrorCode.ERROR_IO);
    }

    @Test
    public void testToCreateWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        addValidSessionTokenToParameter(proxy);

        api.setFileContentType("image/png");
        api.setFile(new File("src/test/resources/images/musangas.png"));

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToCreateWithoutValidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        loginAs(proxy, DEFAULT_USER_ID);

        api.setFileContentType("image/png");
        api.setFile(new File("src/test/resources/images/musangas.png"));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToCreateWithoutFile() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        loginAs(proxy, DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);

        api.setFileContentType("image/png");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_NOIMAGE);
    }

    @Test
    public void testToCreateWithoutFileContentType() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        loginAs(proxy, DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);

        api.setFile(new File("src/test/resources/images/musangas.png"));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_IMAGE_CONTENTTYPE);
    }

    @Test
    public void testToCreateWithInvalidFileContentType() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/image/create");
        CreateImageAPI api = (CreateImageAPI) proxy.getAction();
        loginAs(proxy, DEFAULT_USER_ID);
        addValidSessionTokenToParameter(proxy);

        api.setFileContentType("text/html");
        api.setFile(new File("src/test/resources/images/musangas.png"));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_IMAGE_CONTENTTYPE);
    }

    private String storeImage() throws Exception {
        return new Transaction<String>() {
            @Override
            protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
                try {
                    File file = new File("src/test/resources/images/musangas.png");
                    byte[] data = Util.getContentOfFile(file);
                    String imageId = daos.getImageAccess().getFreshId(con);
                    UserImage imageData = new UserImage(imageId, DEFAULT_USER_ID, "image/png", data, TimeUtil.getCurrentDateTime());
                    daos.getImageAccess().put(con, imageData);
                    return imageId;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }.execute();
    }
}
