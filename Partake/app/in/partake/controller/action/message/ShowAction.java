package in.partake.controller.action.message;

import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.UserMessageEx;
import in.partake.model.access.DBAccess;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.MessageDAOFacade;
import in.partake.model.dto.UserReceivedMessage;
import in.partake.resource.UserErrorCode;

import java.util.UUID;

import play.mvc.Result;

public class ShowAction extends AbstractPartakeAction {
    private String messageId;
    private UserMessageEx message;

    public static Result get(String messageId) throws DAOException, PartakeException {
        ShowAction action = new ShowAction();
        action.messageId = messageId;
        return action.execute();
    }

    public Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        UUID messageId = getValidUUIDParameter("messageId", UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        ShowActionTransaction transaction = new ShowActionTransaction(user, messageId);
        message = transaction.execute();

        return render(views.html.messages.show.render(context(), message));
    }

    public UserMessageEx getMessage() {
        return message;
    }
}

class ShowActionTransaction extends DBAccess<UserMessageEx> {
    private UserEx user;
    private UUID messageId;

    public ShowActionTransaction(UserEx user, UUID messageId) {
        this.user = user;
        this.messageId = messageId;
    }

    @Override
    protected UserMessageEx doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        UserMessageEx message = MessageDAOFacade.findUserReceivedMessage(con, daos, messageId);
        if (message == null)
            throw new PartakeException(UserErrorCode.INVALID_NOTFOUND);

        if (!user.getId().equals(message.getReceiverId()))
            throw new PartakeException(UserErrorCode.FORBIDDEN_MESSAGE_SHOW);

        if (!message.isOpened()) {
            UserReceivedMessage newMessage = new UserReceivedMessage(message);
            newMessage.setOpened(true);
            newMessage.setModifiedAt(TimeUtil.getCurrentDateTime());
            daos.getUserReceivedMessageAccess().put(con, newMessage);
        }

        return message;
    }
}
