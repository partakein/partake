package in.partake.controller.api.debug;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;
import play.mvc.Result;

public class ErrorDBExceptionAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new ErrorDBExceptionAPI().execute();
    }

    /**
     * DAOException が不意に起こった場合のテスト。
     * @return
     */
    @Override
    protected Result doExecute() throws DAOException {
        throw new DAOException();
    }

}
