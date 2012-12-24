package in.partake.controller.api.account;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import play.mvc.Result;

public class SessionTokenAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new SessionTokenAPI().execute();
    }

    @Override
    public Result doExecute() throws DAOException {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("token", context().sessionToken());

        return renderOK(obj);
    }
}
