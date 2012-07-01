package in.partake.controller.base.permission;

import in.partake.model.UserEx;
import in.partake.model.dto.Event;

import org.apache.commons.lang.StringUtils;

public class EventRemovePermission extends PartakePermission {

    public static boolean check(Event event, UserEx user) {
        assert event != null;
        assert user != null;

        if (StringUtils.equals(event.getOwnerId(), user.getId()))
            return true;

        return false;
    }
}
