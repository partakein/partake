package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import net.sf.json.JSONObject;
import play.mvc.Result;

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
