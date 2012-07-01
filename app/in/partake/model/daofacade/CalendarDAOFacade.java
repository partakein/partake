package in.partake.model.daofacade;

import in.partake.base.CalendarUtil;
import in.partake.model.IPartakeDAOs;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.DataIterator;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.dao.access.IEventAccess;
import in.partake.model.dao.aux.EventFilterCondition;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EventCategory;
import net.fortuna.ical4j.model.Calendar;

public class CalendarDAOFacade {

    /**
     * Adds all events whose category name is <code>categoryName</code> to the <code>calendar</code>.
     * @param con
     * @param categoryName
     * @param calendar
     * @throws DAOException
     */
    // TODO: Consider the method name again.
    public static void addCalendarByCategoryName(PartakeConnection con, IPartakeDAOs daos, String categoryName, Calendar calendar) throws DAOException {
        IEventAccess dao = daos.getEventAccess();

        DataIterator<Event> it = dao.getIterator(con, EventFilterCondition.PUBLISHED_PUBLIC_EVENT_ONLY);
        try {
            while (it.hasNext()) {
                Event event = it.next();
                assert event != null;
                if (event == null)
                    continue;

                assert event.isSearchable();
                if (!event.isSearchable())
                    continue;

                if (!EventCategory.getAllEventCategory().equals(categoryName) && !categoryName.equals(event.getCategory()))
                    continue;

                CalendarUtil.addToCalendar(calendar, event);
            }
        } finally {
            it.close();
        }
    }
}
