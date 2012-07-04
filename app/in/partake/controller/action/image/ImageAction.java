package in.partake.controller.action.image;

import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserImage;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public class ImageAction extends AbstractPartakeAction {
    static final String IMAGE_ID_PARAM_NAME = "imageId";

    private final String imageId;

    public ImageAction(String imageId) {
        this.imageId = imageId;
    }

    public static Result get(String imageId) throws DAOException, PartakeException {
        return new ImageAction(imageId).execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        checkIdParameterIsValid(imageId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);
        UserImage image = new ImageTransaction(imageId).execute();
        if (image == null)
            return renderNotFound();
        return render(image.getData(), image.getType(), "inline");
    }
}

class ImageTransaction extends DBAccess<UserImage> {
    private String imageId;

    public ImageTransaction(String imageId) {
        this.imageId = imageId;
    }

    @Override
    protected UserImage doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        return daos.getImageAccess().find(con, imageId);
    }
}
