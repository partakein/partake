package in.partake.controller.base.permission;

import in.partake.model.UserEx;
import in.partake.model.dto.Event;

public class EventNotificationListPermission extends PartakePermission {
    public static boolean check(Event event, UserEx user) {
        assert event != null;
        return isOwnerOrEditor(event, user);
    }
}
