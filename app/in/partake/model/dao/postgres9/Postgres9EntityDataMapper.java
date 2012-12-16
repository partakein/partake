package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataMapper;
import in.partake.model.dto.PartakeModel;

import java.io.IOException;
import java.nio.charset.Charset;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

// You should override map(Postgres9Entity) or map(JSONObject).
public abstract class Postgres9EntityDataMapper<T extends PartakeModel<T>> implements DataMapper<Postgres9Entity, T> {
    static final protected Charset UTF8 = Charset.forName("utf-8");

    @Override
    public T map(Postgres9Entity entity) throws DAOException {
        if (entity == null)
            return null;
        
        ObjectNode obj;
        try {
            obj = new ObjectMapper().readValue(new String(entity.getBody(), UTF8), ObjectNode.class);
        } catch (JsonParseException e) {
            throw new IllegalArgumentException(e);
        } catch (JsonMappingException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new DAOException(e);
        }
        return map(obj);
    }
    
    public T map(ObjectNode obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Postgres9Entity unmap(T t) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
