package in.partake.controller.api.account;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.UserOpenIDLink;
import in.partake.model.dto.UserPreference;

import java.util.List;

import net.sf.json.JSONObject;
import play.mvc.Result;

public class GetAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new GetAPI().execute();
    }

    @Override
    public Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();

        GetAPITransaction transaction = new GetAPITransaction(user.getId());
        transaction.execute();

        JSONObject obj = user.toSafeJSON();
        obj.put("preference", transaction.getPreference().toSafeJSON());
        obj.put("openIds", Util.toJSONArray(transaction.getOpenIds()));
        return renderOK(obj);
    }
}

class GetAPITransaction extends DBAccess<Void> {
    private String userId;
    private UserPreference preference;
    private List<UserOpenIDLink> openIds;

    public GetAPITransaction(String userId) {
        this.userId = userId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        preference = daos.getUserPreferenceAccess().find(con, userId);
        if (preference == null)
            preference = UserPreference.getDefaultPreference(userId);

        openIds = daos.getOpenIDLinkageAccess().findByUserId(con, userId);
        return null;
    }

    public UserPreference getPreference() {
        return preference;
    }

    public List<UserOpenIDLink> getOpenIds() {
        return openIds;
    }
}
