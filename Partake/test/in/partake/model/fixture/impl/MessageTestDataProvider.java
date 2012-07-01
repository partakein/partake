package in.partake.model.fixture.impl;

import in.partake.base.DateTime;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dto.Message;
import in.partake.model.fixture.TestDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageTestDataProvider extends TestDataProvider<Message> {
    @Override
    public Message create(long pkNumber, String pkSalt, int objNumber) {
        UUID uuid = new UUID(pkNumber, pkSalt.hashCode());
        return new Message(uuid, "subject", "body", new DateTime(objNumber), null);
    }

    @Override
    public List<Message> createSamples() {
        List<Message> array = new ArrayList<Message>();
        array.add(new Message(new UUID(0, 0), "subject", "body", new DateTime(0), null));
        array.add(new Message(new UUID(0, 1), "subject", "body", new DateTime(0), null));
        array.add(new Message(new UUID(0, 0), "subject1", "body", new DateTime(0), null));
        array.add(new Message(new UUID(0, 0), "subject", "body1", new DateTime(0), null));
        array.add(new Message(new UUID(0, 0), "subject", "body", new DateTime(1L), null));
        array.add(new Message(new UUID(0, 0), "subject", "body", new DateTime(0), new DateTime(0)));
        return array;
    }

    @Override
    public void createFixtures(PartakeConnection con, IPartakeDAOs daos) throws DAOException {
        daos.getMessageAccess().truncate(con);
    }

}
