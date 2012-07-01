package in.partake.controller.api.debug;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.UserEx;
import in.partake.model.dao.DAOException;

public class SuccessIfLoginAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new SuccessIfLoginAPI().execute();
    }

    /**
     * login していれば success と同じ挙動をする。
     * そうでなければ loginRequired を返し、HTTP status は 401 を返す。
     * 401 は WWW-Authentication をふくまねばならないので、とりあえず OAuth を入れておく。
     * @return
     */
    @Override
    protected Result doExecute() throws DAOException {
        UserEx user = getLoginUser();
        if (user != null) {
            return renderOK();
        } else {
            return renderLoginRequired();
        }
    }
}
