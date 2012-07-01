package in.partake.controller.api.debug;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import play.mvc.Result;

public class SuccessAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new SuccessAPI().execute();
    }

    /**
     * 常に <code>{ "result": "ok" }</code> を返す。
     * HTTP status は 200 を返す。
     * @return
     */
    @Override
    protected Result doExecute() throws DAOException {
        return renderOK();
    }
}
