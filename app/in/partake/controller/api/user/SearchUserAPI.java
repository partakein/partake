package in.partake.controller.api.user;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.UserTwitterLink;
import in.partake.resource.UserErrorCode;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class SearchUserAPI extends AbstractPartakeAPI {

    public static Result get() throws DAOException, PartakeException {
        return new SearchUserAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        String userNamePrefix = getParameter("userNamePrefix");
        if (StringUtils.isBlank(userNamePrefix))
            return renderInvalid(UserErrorCode.MISSING_USERNAME);

        int limit = optIntegerParameter("limit", 10);
        limit = Util.ensureRange(limit, 1, 100);

        List<UserEx> users = new SearchUserAccess(userNamePrefix, limit).execute();

        JSONArray array = Util.toSafeJSONArray(users);

        JSONObject obj = new JSONObject();
        obj.put("users", array);
        return renderOK(obj);
    }
}

class SearchUserAccess extends DBAccess<List<UserEx>> {
    private String userNamePrefix;
    private int limit;

    public SearchUserAccess(String userNamePrefix, int limit) {
        this.userNamePrefix = userNamePrefix;
        this.limit = limit;
    }

    @Override
    protected List<UserEx> doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        List<UserTwitterLink> links = daos.getTwitterLinkageAccess().findByScreenNamePrefix(con, userNamePrefix, limit);
        List<UserEx> users = new ArrayList<UserEx>();
        for (UserTwitterLink link : links) {
            UserEx user = UserDAOFacade.getUserEx(con, daos, link.getUserId());
            if (user == null)
                continue;
            users.add(user);
        }
        return users;
    }
}
