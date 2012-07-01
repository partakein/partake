package in.partake.base;

import in.partake.model.dto.Event;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;

import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.LastModified;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Sequence;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Url;
import net.fortuna.ical4j.model.property.Version;

public class CalendarUtil {
    private static final TimeZone JST_TIMEZONE;
    static {
        TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
        JST_TIMEZONE = registry.getTimeZone("Asia/Tokyo");
    }

    public static Calendar createCalendarSkeleton() {
        Calendar calendar = new Calendar();

        calendar.getProperties().add(new ProdId("-//Events Calendar//iCal4j 1.0//EN"));
        calendar.getProperties().add(Version.VERSION_2_0);
        calendar.getProperties().add(CalScale.GREGORIAN);
        calendar.getComponents().add(JST_TIMEZONE.getVTimeZone());

        return calendar;
    }

    public static void addToCalendar(Calendar calendar, Event event) {
        DateTime beginDate = new DateTime(event.getBeginDate().getTime());
        beginDate.setTimeZone(JST_TIMEZONE);

        VEvent vEvent;
        if (event.getEndDate() != null) {
            DateTime endDate = new DateTime(event.getEndDate().getTime());
            endDate.setTimeZone(JST_TIMEZONE);
            vEvent = new VEvent(beginDate, endDate, event.getTitle());
        } else {
            vEvent = new VEvent(beginDate, event.getTitle());
        }

        // set unique identifier
        vEvent.getProperties().add(new Uid(event.getId()));

        // Description
        vEvent.getProperties().add(new Description(event.getEventURL()));

        // URL
        if (event.getUrl() != null && !event.getUrl().isEmpty()) {
            try {
                vEvent.getProperties().add(new Url(new URI(event.getUrl())));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        // modified
        DateTime modifiedAt = null;
        if (event.getModifiedAt() != null) {
            modifiedAt = new DateTime(event.getModifiedAt().getTime());
        } else if (event.getCreatedAt() != null){
            modifiedAt = new DateTime(event.getCreatedAt().getTime());
        }
        if (modifiedAt != null) {
            modifiedAt.setTimeZone(JST_TIMEZONE);
            vEvent.getProperties().add(new LastModified(modifiedAt));
        }

        // sequence
        vEvent.getProperties().add(new Sequence(event.getRevision()));

        calendar.getComponents().add(vEvent);
    }

    public static InputStream outputCalendar(Calendar calendar) throws IOException, ValidationException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new CalendarOutputter().output(calendar, baos);
        byte[] data = baos.toByteArray();

        return new ByteArrayInputStream(data);
    }
}
