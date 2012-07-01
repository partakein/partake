package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IMessageAccess;
import in.partake.model.dto.Message;
import in.partake.model.dto.MessageEnvelope;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageEnvelopeTestDataProvider extends TestDataProvider<MessageEnvelope> {
    @Override
    public MessageEnvelope create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        MessageEnvelope envelope = new MessageEnvelope(uuid.toString(), "userMessageId", null, null, 0, null, null, null, new DateTime(objNumber), null);
        return envelope;
    }

    @Override
    public List<MessageEnvelope> createSamples() {
        List<MessageEnvelope> array = new ArrayList<MessageEnvelope>();
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 0, null, null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 1).toString(), "userMessageId", null, null, 0, null, null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId1", null, null, 0, null, null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", "hoge", null, 0, null, null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, "fuga", 0, null, null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 1, null, null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 0, new DateTime(0), null, null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 0, null, new DateTime(0), null, new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 0, null, null, new DateTime(0), new DateTime(0), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 0, null, null, null, new DateTime(1), null));
        array.add(new MessageEnvelope(new UUID(0, 0).toString(), "userMessageId", null, null, 0, null, null, null, new DateTime(0), new DateTime(0)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        IMessageAccess dao = daos.getMessageAccess();
        dao.truncate(con);

        dao.put(con, new Message(DEFAULT_MESSAGE_ID, "subject", "body", new DateTime(0L), null));
    }
}
