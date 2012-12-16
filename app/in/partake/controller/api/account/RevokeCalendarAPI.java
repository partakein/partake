package in.partake.controller.api.account;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserCalendarLink;

public class RevokeCalendarAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new RevokeCalendarAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        String newCalendarId = new RevokeCalendarAPITransaction(user).execute();

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("calendarId", newCalendarId);
        return renderOK(obj);
    }
}

class RevokeCalendarAPITransaction extends Transaction<String> {
    private UserEx user;

    public RevokeCalendarAPITransaction(UserEx user) {
        this.user = user;
    }

    protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        daos.getCalendarAccess().removeByUserId(con, user.getId());

        // 新しくカレンダー id を作成して保存
        String calendarId = daos.getCalendarAccess().getFreshId(con);
        UserCalendarLink embryo = new UserCalendarLink(calendarId, user.getId());
        daos.getCalendarAccess().put(con, embryo);

        return calendarId;
    }

}
