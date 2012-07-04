package in.partake.controller.action.image;

import in.partake.base.ImageUtil;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.UserThumbnail;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import play.mvc.Result;

public class ThumbnailAction extends AbstractPartakeAction {
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    private final String imageId;

    public ThumbnailAction(String imageId) {
        this.imageId = imageId;
    }

    public static Result get(String imageId) throws DAOException, PartakeException {
        return new ThumbnailAction(imageId).execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        checkIdParameterIsValid(imageId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        UserThumbnail data = new ThumbnailAccess(imageId).execute();
        if (data != null)
            return render(data.getData(), data.getType(), "inline");

        // If not found, we will generate a thumbnail.
        UserThumbnail created = new ThumbnailTransaction(imageId).execute();
        if (created != null)
            return render(created.getData(), created.getType(), "inline");

        return renderNotFound();
    }
}

class ThumbnailAccess extends DBAccess<UserThumbnail> {
    private String imageId;

    public ThumbnailAccess(String imageId) {
        this.imageId = imageId;
    }

    @Override
    protected UserThumbnail doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        UserThumbnail thumbnail = daos.getThumbnailAccess().find(con, imageId);
        if (thumbnail != null)
            return thumbnail;

        return null;
    }
}

class ThumbnailTransaction extends Transaction<UserThumbnail> {
    private String imageId;

    public ThumbnailTransaction(String imageId) {
        this.imageId = imageId;
    }

    @Override
    protected UserThumbnail doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        try {
            UserImage image = daos.getImageAccess().find(con, imageId);
            if (image == null)
                return null;

            InputStream is = new ByteArrayInputStream(image.getData());
            BufferedImage converted = ImageUtil.createThumbnail(ImageIO.read(is), 320, 240);
            is.close();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(converted, "png", os);
            os.close();

            UserThumbnail thumbnail = new UserThumbnail(image.getId(), image.getUserId(), "image/png", os.toByteArray(), TimeUtil.getCurrentDateTime());
            daos.getThumbnailAccess().put(con, thumbnail);

            return thumbnail;
        } catch (IOException e) {
            throw new PartakeException(ServerErrorCode.ERROR_IO);
        }
    }
}
