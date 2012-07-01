package in.partake.controller.api.event;

import play.mvc.Result;
import in.partake.base.PartakeException;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.RemoveCommentPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.EventComment;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

public class RemoveCommentAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new RemoveCommentAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String commentId = getValidCommentIdParameter();

        new RemoveCommentTransaction(user, commentId).execute();
        return renderOK();
    }
}

class RemoveCommentTransaction extends Transaction<Void> {
    private String commentId;
    private UserEx user;

    public RemoveCommentTransaction(UserEx user, String commentId) {
        this.user = user;
        this.commentId = commentId;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        EventComment comment = daos.getCommentAccess().find(con, commentId);
        if (comment == null)
            throw new PartakeException(UserErrorCode.INVALID_COMMENT_ID);

        Event event = daos.getEventAccess().find(con, comment.getEventId());
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_COMMENT_ID);

        if (!RemoveCommentPermission.check(comment, event, user))
            throw new PartakeException(UserErrorCode.COMMENT_REMOVAL_FORBIDDEN);

        daos.getCommentAccess().remove(con, commentId);
        return null;
    }
}
