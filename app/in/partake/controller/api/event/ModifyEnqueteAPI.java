package in.partake.controller.api.event;

import in.partake.base.PartakeException;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EnqueteAnswerType;
import in.partake.model.dto.auxiliary.EnqueteQuestion;
import in.partake.resource.UserErrorCode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import play.mvc.Result;

public class ModifyEnqueteAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new ModifyEnqueteAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();

        String[] questionIds = getParameters("ids[]");
        String[] questions = getParameters("questions[]");
        String[] types = getParameters("types[]");
        String[] options = getParameters("options[]");

        if (questions == null)
            return renderInvalid(UserErrorCode.MISSING_ENQUETE_QUESTION);
        if (types == null)
            return renderInvalid(UserErrorCode.MISSING_ENQUETE_TYPE);
        if (options == null)
            return renderInvalid(UserErrorCode.MISSING_ENQUETE_OPTION);
        if (questions.length != types.length || questions.length != options.length)
            return renderInvalid(UserErrorCode.INVALID_ENQUETE_PARAMS);

        new ModifyEnqueteTransaction(user, eventId, questionIds, questions, types, options).execute();
        return renderOK();
    }
}

class ModifyEnqueteTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private String[] ids;
    private String[] questions;
    private String[] types;
    private String[] options;

    public ModifyEnqueteTransaction(UserEx user, String eventId, String[] ids, String[] questions, String[] types, String[] options) {
        this.user = user;
        this.eventId = eventId;
        this.ids = ids;
        this.questions = questions;
        this.types = types;
        this.options = options;
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        Event event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        List<EnqueteQuestion> enquetes = new ArrayList<EnqueteQuestion>();
        try {
            for (int i = 0; i < questions.length; ++i) {
                List<String> optionValues = new ArrayList<String>();
                ArrayNode array;
                    array = new ObjectMapper().readValue(options[i], ArrayNode.class);
                for (int j = 0; j < array.size(); ++j)
                    optionValues.add(array.get(j).asText());
    
                UUID enqueteId = Util.isUUID(ids[i]) ? UUID.fromString(ids[i]) : UUID.randomUUID();
                EnqueteQuestion question = new EnqueteQuestion(
                        enqueteId, questions[i], EnqueteAnswerType.safeValueOf(types[i]), optionValues);
                enquetes.add(question);
            }
        } catch (JsonParseException e) {
            throw new IllegalArgumentException(e);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            // I can not find Exception which suits in this situation,
            // so I use RuntimeException. 2012/Dec/16 Kengo TODA
            throw new RuntimeException(e);
        }

        Event copied = new Event(event);
        copied.setEnquetes(enquetes);
        daos.getEventAccess().put(con, copied);

        return null;
    }
}
