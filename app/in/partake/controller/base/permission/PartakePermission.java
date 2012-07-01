package in.partake.controller.base.permission;

import in.partake.model.UserEx;
import in.partake.model.dto.Event;

import org.apache.commons.lang.StringUtils;

public abstract class PartakePermission {
    protected static boolean isOwnerOrEditor(Event event, UserEx user) {
        if (user == null)
            return false;

        if (StringUtils.equals(event.getOwnerId(), user.getId()))
            return true;

        if (event.isManager(user))
            return true;

        return false;
    }
}
