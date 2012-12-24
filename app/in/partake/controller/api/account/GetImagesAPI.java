package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;

import java.util.List;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;

/**
 * Retrieves images which is uploaded by owners.
 *
 * @author shinyak
 *
 * Note that this may contain images someone uploaded if an event editor uploaded it.
 */
public class GetImagesAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new GetImagesAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        int offset = optIntegerParameter("offset", 0);
        offset = Util.ensureRange(offset, 0, Integer.MAX_VALUE);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 1, 100);

        GetImagesTransaction transaction = new GetImagesTransaction(user, offset, limit);
        transaction.execute();

        ArrayNode imageIds = new ArrayNode(JsonNodeFactory.instance);
        for (String imageId : transaction.getImageIds())
            imageIds.add(imageId);

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("imageIds", imageIds);
        obj.put("count", transaction.getCountImages());
        return renderOK(obj);
    }
}

class GetImagesTransaction extends DBAccess<Void> {
    private UserEx user;
    private int offset;
    private int limit;

    private List<String> imageIds;
    private int countImages;

    public GetImagesTransaction(UserEx user, int offset, int limit) {
        this.user = user;
        this.offset = offset;
        this.limit = limit;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        this.imageIds = daos.getImageAccess().findIdsByUserId(con, user.getId(), offset, limit);
        this.countImages = daos.getImageAccess().countByUserId(con, user.getId());
        return null;
    }

    public List<String> getImageIds() {
        return this.imageIds;
    }

    public int getCountImages() {
        return this.countImages;
    }
}
