package in.partake.controller.api.admin;

import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.ConfigurationItem;
import in.partake.resource.ConfigurationKeyConstants;
import in.partake.resource.UserErrorCode;
import play.mvc.Result;

public class AdminModifySettingAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new AdminModifySettingAPI().execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        ensureAdmin();
        ensureValidSessionToken();

        String key = getFormParameter("key");
        if (!ConfigurationKeyConstants.configurationkeySet.contains(key))
            return renderInvalid(UserErrorCode.INVALID_ADMIN_SETTING_KEY);

        String value = getFormParameter("value");

        new ModifySettingTransaction(key, value).execute();

        return renderOK();
    }
}

class ModifySettingTransaction extends Transaction<Void> {
    private String key;
    private String value;

    ModifySettingTransaction(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        ConfigurationItem item = new ConfigurationItem(key, value);
        daos.getConfiguraitonItemAccess().put(con, item);
        return null;
    }
}
