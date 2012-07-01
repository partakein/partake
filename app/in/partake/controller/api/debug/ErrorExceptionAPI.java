package in.partake.controller.api.debug;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.dao.DAOException;

public class ErrorExceptionAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new ErrorExceptionAPI().execute();
    }

    /**
     * RuntimeException が不意に起こった場合の対応をテスト
     * @return
     */
    @Override
    protected Result doExecute() throws DAOException {
        throw new RuntimeException("Some Error");
    }
}
