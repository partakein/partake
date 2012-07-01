package in.partake.controller.api.image;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IUserImageAccess;
import in.partake.model.dto.UserImage;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;
import play.mvc.Result;

/**
 * Upload images, and gets recent image ids if necessary.
 * @author shinyak
 *
 */
public class CreateImageAPI extends AbstractPartakeAPI {

    private File file;
    private String contentType;

    public static Result post() throws DAOException, PartakeException {
        return new CreateImageAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        if (file == null)
            return renderInvalid(UserErrorCode.INVALID_NOIMAGE);
        if (contentType == null)
            return renderInvalid(UserErrorCode.INVALID_IMAGE_CONTENTTYPE);

        // IE sends jpeg file using contentType = "image/pjpeg". We should handle this here.
        if ("image/pjpeg".equals(contentType))
            contentType = "image/jpeg";

        if (!Util.isImageContentType(contentType))
            return renderInvalid(UserErrorCode.INVALID_IMAGE_CONTENTTYPE);

        int limit = optIntegerParameter("limit", 1);
        limit = Util.ensureRange(limit, 1, 100);

        List<String> imageIds = new CreateImageAPITransaction(user, file, contentType, limit).execute();

        JSONObject obj = new JSONObject();
        obj.put("imageId", imageIds.get(0));
        obj.put("imageIds", imageIds);

        // We should return text/plain or text/html for MS-IE here.
        // TODO: Should use Accept header instead of this.
        if (optBooleanParameter("ensureTextPlain", false))
            return renderOKWith(obj, "text/plain");
        else
            return renderOK(obj);

    }

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileContentType(String contentType) {
        this.contentType = contentType;
    }
}

class CreateImageAPITransaction extends Transaction<List<String>> {
    private UserEx user;
    private File file;
    private String contentType;
    private int limit;

    CreateImageAPITransaction(UserEx user, File file, String contentType, int limit) {
        this.user = user;
        this.file = file;
        this.contentType = contentType;
        this.limit = limit;
    }

    // TODO: We should not load image in memory here. However, sending image from DB directly will cause
    // another problem, e.g. DoS.
    public List<String> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        IUserImageAccess dao = daos.getImageAccess();

        byte[] foreImageByteArray;
        try {
            foreImageByteArray = Util.getContentOfFile(file);
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.ERROR_IO);
        }

        String imageId = dao.getFreshId(con);
        UserImage imageEmbryo = new UserImage(imageId, user.getId(), contentType, foreImageByteArray, TimeUtil.getCurrentDateTime());
        dao.put(con, imageEmbryo);

        if (limit == 1)
            return Collections.singletonList(imageId);
        else
            return dao.findIdsByUserId(con, user.getId(), 0, limit);
    }
}
