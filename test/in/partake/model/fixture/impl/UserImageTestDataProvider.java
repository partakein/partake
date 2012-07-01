package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserImageAccess;
import in.partake.model.dto.UserImage;
import in.partake.model.fixture.TestDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ImageData のテストデータを作成します。
 * @author shinyak
 *
 */
public class UserImageTestDataProvider extends TestDataProvider<UserImage> {
    private final byte[] defaultImageContent;

    public UserImageTestDataProvider() {
        try {
            InputStream is = getClass().getResourceAsStream("images/null.png");
            defaultImageContent = Util.getContentOfInputStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getDefaultImageContent() {
        return defaultImageContent;
    }

    @Override
    public UserImage create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, ("image" + pkSalt).toString().hashCode());

        if (objNumber == 0) {
            int N = 1024 * 1024;
            byte[] data = new byte[N];
            for (int i = 0; i < N; ++i) {
                data[i] = (byte)(i % N);
            }

            return new UserImage(uuid.toString(), DEFAULT_USER_ID, "data/octet-stream", data, new DateTime(objNumber));
        } else {
            return new UserImage(uuid.toString(), DEFAULT_USER_ID, "data/octet-stream", new byte[] { 1, 2, (byte) objNumber }, new DateTime(objNumber));
        }
    }

    @Override
    public List<UserImage> createSamples() {
        List<UserImage> array = new ArrayList<UserImage>();
        array.add(new UserImage("id", "userId", "type", new byte[] { 0, 1, 2 } , new DateTime(0)));
        array.add(new UserImage("id1", "userId", "type", new byte[] { 0, 1, 2 } , new DateTime(0)));
        array.add(new UserImage("id", "userId1", "type", new byte[] { 0, 1, 2 } , new DateTime(0)));
        array.add(new UserImage("id", "userId", "type1", new byte[] { 0, 1, 2 } , new DateTime(0)));
        array.add(new UserImage("id", "userId", "type", new byte[] { 0, 1, 3 } , new DateTime(0)));
        array.add(new UserImage("id", "userId", "type", new byte[] { 0, 1, 2 } , new DateTime(1)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserImageAccess dao = daos.getImageAccess();
        dao.truncate(con);

        // IMAGE_OWNED_BY_DEFAULT_USER_ID contains DEFAULT_IMAGE_ID.
        dao.put(con, new UserImage(EVENT_FOREIMAGE_ID, EVENT_OWNER_ID, "byte/octet-stream", defaultImageContent, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserImage(EVENT_BACKIMAGE_ID, EVENT_OWNER_ID, "byte/octet-stream", defaultImageContent, TimeUtil.getCurrentDateTime()));
        dao.put(con, new UserImage(IMAGE_OWNER_IMAGE_ID, IMAGE_OWNER_ID, "byte/octet-stream", defaultImageContent, TimeUtil.getCurrentDateTime()));
        for (int i = 0; i < IMAGE_OWNED_BY_DEFAULT_USER_ID.length; ++i)
            dao.put(con, new UserImage(IMAGE_OWNED_BY_DEFAULT_USER_ID[i], DEFAULT_USER_ID, "byte/octet-stream", defaultImageContent, new DateTime(IMAGE_OWNED_BY_DEFAULT_USER_ID.length - i)));
    }
}
