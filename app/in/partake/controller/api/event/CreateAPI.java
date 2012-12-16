package in.partake.controller.api.event;

import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.resource.UserErrorCode;

import java.util.Calendar;
import java.util.Collections;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;

public class CreateAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new CreateAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();

        Event embryo = new Event();
        embryo.setOwnerId(user.getId());
        embryo.setDraft(optBooleanParameter("draft", true));
        embryo.setCreatedAt(TimeUtil.getCurrentDateTime());

        // Title
        String title = getParameter("title");
        if (StringUtils.isBlank(title) || title.length() > 100)
            return renderInvalid(UserErrorCode.INVALID_PARAMETERS, Collections.singletonMap("title", "タイトルは 100 文字以下で必ず入力してください。"));
        embryo.setTitle(title);

        // beginDate
        {
            DateTime beginDate = getDateTimeParameter("beginDate");
            if (beginDate == null)
                return renderInvalid(UserErrorCode.INVALID_PARAMETERS, Collections.singletonMap("beginDate", "開始日時は必ず入力して下さい。"));

            Calendar beginCalendar = TimeUtil.calendar(beginDate.toDate());
            if (beginCalendar.get(Calendar.YEAR) < 2000 || 2100 < beginCalendar.get(Calendar.YEAR))
                return renderInvalid(UserErrorCode.INVALID_PARAMETERS, Collections.singletonMap("beginDate", "開始日時の範囲が不正です。"));

            embryo.setBeginDate(beginDate);
        }

        // endDate
        {
            DateTime endDate = getDateTimeParameter("endDate");
            if (endDate != null) {
                Calendar endCalendar = TimeUtil.calendar(endDate.toDate());
                if (endCalendar.get(Calendar.YEAR) < 2000 || 2100 < endCalendar.get(Calendar.YEAR))
                    return renderInvalid(UserErrorCode.INVALID_PARAMETERS, Collections.singletonMap("endDate", "終了日時の範囲が不正です。"));

                if (!embryo.getBeginDate().isBefore(endDate))
                    return renderInvalid(UserErrorCode.INVALID_PARAMETERS, Collections.singletonMap("endDate", "終了日時が開始日時より前になっています。"));
            }

            embryo.setEndDate(endDate);
        }

        String eventId = new CreateTransaction(embryo).execute();
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("eventId", eventId);
        return renderOK(obj);
    }
}

class CreateTransaction extends Transaction<String> {
    private Event event;

    public CreateTransaction(Event event) {
        this.event = event;
    }

    @Override
    protected String doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        String eventId = EventDAOFacade.create(con, daos, event);
        event.setId(eventId);

        EventTicket ticket = EventTicket.createDefaultTicket(daos.getEventTicketAccess().getFreshId(con), eventId);
        daos.getEventTicketAccess().put(con, ticket);

        return eventId;
    }
}
