package in.partake.controller.action.event;

import in.partake.base.Pair;
import in.partake.base.PartakeException;
import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;
import in.partake.model.EventTicketHolderList;
import in.partake.model.UserEx;
import in.partake.model.UserTicketEx;
import in.partake.model.dao.DAOException;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import play.mvc.Result;
import au.com.bytecode.opencsv.CSVWriter;

public class ShowParticipantsCSVAction extends AbstractPartakeAction {
    String eventId;

    public static Result get(String eventId) throws DAOException, PartakeException {
        ShowParticipantsCSVAction action = new ShowParticipantsCSVAction();
        action.eventId = eventId;
        return action.execute();
    }


    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        checkIdParameterIsValid(eventId, UserErrorCode.INVALID_NOTFOUND, UserErrorCode.INVALID_NOTFOUND);

        ParticipantsListTransaction transaction = new ParticipantsListTransaction(user, eventId);
        transaction.execute();

        EventEx event = transaction.getEvent();
        List<Pair<EventTicket, EventTicketHolderList>> ticketAndHolders = transaction.getTicketAndHolders();
        Map<String, List<String>> userTicketInfoMap = transaction.getUserTicketInfoMap();

        try {
            byte[] body = createCSVInputStream(event, ticketAndHolders, userTicketInfoMap);
            return render(body, "text/csv; charset=UTF-8", "attachment");
        } catch (IOException e) {
            return renderError(ServerErrorCode.ERROR_IO, e);
        }
    }

    private byte[] createCSVInputStream(EventEx event, List<Pair<EventTicket, EventTicketHolderList>> ticketAndHolders, Map<String, List<String>> userTicketInfoMap) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CSVWriter writer = new CSVWriter(new OutputStreamWriter(baos, Charset.forName("UTF-8")));

        writeHeader(writer, event);
        for (int i = 0; i < ticketAndHolders.size(); ++i) {
            EventTicket ticket = ticketAndHolders.get(i).getFirst();
            EventTicketHolderList list = ticketAndHolders.get(i).getSecond();
            writeTicket(writer, event, ticket, list, i, userTicketInfoMap);
        }

        writer.flush();
        writer.close();

        return baos.toByteArray();
    }

    private void writeHeader(CSVWriter writer, EventEx event) {
        List<String> headers = new ArrayList<String>();
        headers.add("チケット名");
        headers.add("順番");
        headers.add("名前");
        headers.add("予約状況");
        headers.add("コメント");
        headers.add("登録日時");
        headers.add("出欠状況");
        if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) {
            for (EnqueteQuestion question : event.getEnquetes())
                headers.add(question.getText());
        }

        writer.writeNext(headers.toArray(new String[0]));
    }

    private void writeTicket(CSVWriter writer, EventEx event, EventTicket ticket, EventTicketHolderList holderList, int ticketIndex, Map<String, List<String>> userTicketInfoMap) {
        int order = 0;
        for (UserTicketEx userTicket : holderList.getEnrolledParticipations()) {
            List<String> body = new ArrayList<String>();
            body.add(ticket.getName());
            body.add(String.valueOf(++order));
            body.add(userTicket.getUser().getScreenName());
            body.add(userTicket.getStatus().toHumanReadableString(false));
            body.add(userTicket.getComment());
            body.add(userTicket.getAppliedAt().toHumanReadableFormat());
            body.add(userTicket.getAttendanceStatus().toHumanReadableString());
            if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) {
                for (EnqueteQuestion question : event.getEnquetes()) {
                    List<String> values = userTicket.getEnqueteAnswers().get(question.getId());
                    body.add(values != null ? StringUtils.join(values.iterator(), ',') : "");
                }
            }

            writer.writeNext(body.toArray(new String[0]));
        }

        for (UserTicketEx userTicket : holderList.getSpareParticipations()) {
            List<String> body = new ArrayList<String>();
            body.add(ticket.getName());
            body.add(String.valueOf(++order));
            body.add(userTicket.getUser().getScreenName());
            body.add(userTicket.getStatus().toHumanReadableString(false));
            body.add(userTicket.getComment());
            body.add(userTicket.getAppliedAt().toHumanReadableFormat());
            body.add(userTicket.getAttendanceStatus().toHumanReadableString());
            if (event.getEnquetes() != null && !event.getEnquetes().isEmpty()) {
                for (EnqueteQuestion question : event.getEnquetes()) {
                    List<String> values = userTicket.getEnqueteAnswers().get(question.getId());
                    body.add(values != null ? StringUtils.join(values.iterator(), ',') : "");
                }
            }

            writer.writeNext(body.toArray(new String[0]));
        }
    }
}
