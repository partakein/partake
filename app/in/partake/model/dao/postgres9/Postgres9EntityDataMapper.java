package in.partake.model.dao.postgres9;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataMapper;
import in.partake.model.dto.PartakeModel;

import java.nio.charset.Charset;

import net.sf.json.JSONObject;

// You should override map(Postgres9Entity) or map(JSONObject).
public abstract class Postgres9EntityDataMapper<T extends PartakeModel<T>> implements DataMapper<Postgres9Entity, T> {
    static final protected Charset UTF8 = Charset.forName("utf-8");

    @Override
    public T map(Postgres9Entity entity) throws DAOException {
        if (entity == null)
            return null;
        
        JSONObject obj = JSONObject.fromObject(new String(entity.getBody(), UTF8));
        return map(obj);
    }
    
    public T map(JSONObject obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Postgres9Entity unmap(T t) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
