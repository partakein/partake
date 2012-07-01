package in.partake.controller.base.permission;

import in.partake.model.UserEx;
import in.partake.model.dto.EventComment;
import in.partake.model.dto.Event;

import org.apache.commons.lang.StringUtils;

public class RemoveCommentPermission extends PartakePermission {

    public static boolean check(EventComment comment, Event event, UserEx user) {
        assert comment != null;
        assert event != null;

        if (user == null)
            return false;

        if (StringUtils.equals(comment.getUserId(), user.getId()))
            return true;

        if (StringUtils.equals(event.getOwnerId(), user.getId()))
            return true;

        if (event.isManager(user))
            return true;

        return false;
    }
}
