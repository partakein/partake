package in.partake.model.daofacade;

import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.User;

import org.apache.commons.lang.StringUtils;

public class ImageDAOFacade {
    public static boolean checkImageOwner(PartakeConnection con, IPartakeDAOs daos, String imageId, User user) throws DAOException {
        // TODO: We don't need to get all image from DB.
        UserImage data = daos.getImageAccess().find(con, imageId);
        if (data == null)
            return false;

        return StringUtils.equals(user.getId(), data.getUserId());
    }
}
