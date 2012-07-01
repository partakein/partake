package in.partake.model;

import in.partake.model.dto.EventComment;

/**
 * Comment with related data.
 * @author shinyak
 *
 */
public class EventCommentEx extends EventComment {
    private UserEx user;

    public EventCommentEx(EventComment comment, UserEx user) {
        super(comment);
        this.user = user;
    }

    public UserEx getUser() {
        return user;
    }
}
