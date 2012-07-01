package in.partake.controller.api.account;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;
import in.partake.session.PartakeSession;
import net.sf.json.JSONObject;

public class SessionTokenAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new SessionTokenAPI().execute();
    }

    @Override
    public Result doExecute() throws DAOException {
        JSONObject obj = new JSONObject();
        obj.put("token", context().sessionToken());

        return renderOK(obj);
    }
}
