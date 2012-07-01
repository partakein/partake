package in.partake.controller.api.debug;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import in.partake.resource.ServerErrorCode;

public class ErrorDBAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new ErrorDBAPI().execute();
    }

    /**
     * データベースエラー。
     * ステータスは 500 を返す。
     */
    @Override
    protected Result doExecute() throws DAOException {
        return renderError(ServerErrorCode.DB_ERROR);
    }

}
