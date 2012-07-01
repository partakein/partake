package in.partake.controller.api.debug;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.UserErrorCode;

public class InvalidAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new InvalidAPI().execute();
    }

    /**
     * 常に <code>{ "result": "error", "reason": "intentional invalid response" }</code> を返す。
     * ステータスは 400 を返す。
     */
    @Override
    protected Result doExecute() throws DAOException {
        return renderInvalid(UserErrorCode.INTENTIONAL_USER_ERROR);
    }
}
