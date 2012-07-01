package in.partake.model.daoutil;

import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dto.PartakeModel;

import java.util.ArrayList;
import java.util.List;

public class DAOUtil {
    private DAOUtil() {
        // Prevents from instantiation.
    }

    public static <T> List<T> convertToList(DataIterator<T> it) throws DAOException {
        try {
            List<T> result = new ArrayList<T>();
            while (it.hasNext()) {
                T t = it.next();
                if (t == null)
                    continue;
                result.add(t);
            }

            return result;
        } finally {
            it.close();
        }
    }

    public static <T extends PartakeModel<T>> List<T> freeze(List<T> list) throws DAOException {
        for (T t : list)
            t.freeze();
        return list;
    }
}
