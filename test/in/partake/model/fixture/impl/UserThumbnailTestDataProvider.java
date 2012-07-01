package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.base.Util;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserThumbnailAccess;
import in.partake.model.dto.UserThumbnail;
import in.partake.model.fixture.TestDataProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserThumbnailTestDataProvider extends TestDataProvider<UserThumbnail> {
    private final byte[] defaultImageContent;

    public UserThumbnailTestDataProvider() {
        try {
            InputStream is = getClass().getResourceAsStream("images/null.png");
            defaultImageContent = Util.getContentOfInputStream(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserThumbnail create(long pkNumber, String pkSalt, int objNumber) {
        UUID id = new UUID(pkNumber, pkSalt.hashCode());
        return new UserThumbnail(id.toString(), "userId", "image/png", new byte[] { 0, 1, (byte) objNumber } , new DateTime(0L));
    }

    @Override
    public List<UserThumbnail> createSamples() {
        List<UserThumbnail> array = new ArrayList<UserThumbnail>();
        array.add(new UserThumbnail("id", "userId", "image/png", new byte[] { 0, 1, 2 }, new DateTime(0L)));
        array.add(new UserThumbnail("id1", "userId", "image/png", new byte[] { 0, 1, 2 }, new DateTime(0L)));
        array.add(new UserThumbnail("id", "userId1", "image/png", new byte[] { 0, 1, 2 }, new DateTime(0L)));
        array.add(new UserThumbnail("id", "userId", "image/jpeg", new byte[] { 0, 1, 2 }, new DateTime(0L)));
        array.add(new UserThumbnail("id", "userId", "image/png", new byte[] { 0, 1, 3 }, new DateTime(0L)));
        array.add(new UserThumbnail("id", "userId", "image/png", new byte[] { 0, 1, 2 }, new DateTime(1L)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IUserThumbnailAccess dao = daos.getThumbnailAccess();
        dao.truncate(con);

        dao.put(con, new UserThumbnail(DEFAULT_IMAGE_ID, DEFAULT_USER_ID, "byte/octet-stream", defaultImageContent, new DateTime(0L)));
    }
}
