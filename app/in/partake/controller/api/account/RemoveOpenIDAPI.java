package in.partake.controller.api.account;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.resource.UserErrorCode;

public class RemoveOpenIDAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new RemoveOpenIDAPI().execute();
    }

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        // check arguments
        String identifier = getParameter("identifier");
        if (identifier == null)
            return renderInvalid(UserErrorCode.MISSING_OPENID);

        // identifier が user と結び付けられているか検査して消去
        new RemoveOpenIDLinkageTransaction(user.getId(), identifier).execute();
        return renderOK();
    }
}

class RemoveOpenIDLinkageTransaction extends Transaction<Void> {
    private String userId;
    private String identifier;

    public RemoveOpenIDLinkageTransaction(String userId, String identifier) {
        this.userId = userId;
        this.identifier = identifier;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        UserOpenIDLink linkage = daos.getOpenIDLinkageAccess().findByOpenId(con, identifier);
        if (linkage == null || !userId.equals(linkage.getUserId()))
            throw new PartakeException(UserErrorCode.INVALID_OPENID);

        daos.getOpenIDLinkageAccess().remove(con, linkage.getId());
        return null;
    }
}
