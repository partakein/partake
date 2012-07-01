package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.model.fixture.TestDataProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import net.sf.json.JSONObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author skypencil (@eller86)
 */
public final class EventTest extends AbstractPartakeModelTest<Event> {
    @Override
    protected Event copy(Event t) {
        return new Event(t);
    }

    @Override
    protected TestDataProvider<Event> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getEventProvider();
    }

    Event[] samples;

    @Before
    public void createSamples() {
        DateTime now = TimeUtil.getCurrentDateTime();
        samples = new Event[] {
                new Event("id", "title", "summary", "category",
                        now, now, "url", "place",
                        "address", "description", "#hashTag", "ownerId",
                        "foreImageId", "backImageId", "passcode", false,
                        null, new ArrayList<String>(), null,
                        now, now, -1),
                new Event("id2", "title2", "summary2", "category2",
                        now, now, "url2", "place2",
                        "address2", "description2", "#hashTag2", "ownerId2",
                        "foreImageId2", "backImageId2", "passcode2", false,
                        null, new ArrayList<String>(), null,
                        now, now, 1)
        };
    }

    @Test
    public void testCopyConstructor() {
        for (Event source : samples) {
            // Event class doesn't override #equals() method.
            // Assert.assertEquals(source, new Event(source));

            Assert.assertEquals(source.getId(), new Event(source).getId());
            Assert.assertEquals(source.getTitle(), new Event(source).getTitle());
            Assert.assertEquals(source.getSummary(), new Event(source).getSummary());
            Assert.assertEquals(source.getCategory(), new Event(source).getCategory());
            Assert.assertEquals(source.getBeginDate(), new Event(source).getBeginDate());
            Assert.assertEquals(source.getEndDate(), new Event(source).getEndDate());
            Assert.assertEquals(source.getUrl(), new Event(source).getUrl());
            Assert.assertEquals(source.getPlace(), new Event(source).getPlace());
            Assert.assertEquals(source.getAddress(), new Event(source).getAddress());
            Assert.assertEquals(source.getDescription(), new Event(source).getDescription());
            Assert.assertEquals(source.getHashTag(), new Event(source).getHashTag());
            Assert.assertEquals(source.getOwnerId(), new Event(source).getOwnerId());
            Assert.assertEquals(source.getForeImageId(), new Event(source).getForeImageId());
            Assert.assertEquals(source.getBackImageId(), new Event(source).getBackImageId());
            Assert.assertEquals(source.getPasscode(), new Event(source).getPasscode());
            Assert.assertEquals(source.isDraft(), new Event(source).isDraft());
            Assert.assertEquals(source.getCreatedAt(), new Event(source).getCreatedAt());
            Assert.assertEquals(source.getModifiedAt(), new Event(source).getModifiedAt());
            Assert.assertEquals(source.getRevision(), new Event(source).getRevision());
        }
    }

    @Test
    public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
        for (Event source : samples) {
            Event copy = new Event(source);

            for (Field field : Event.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Assert.assertEquals(field.get(source), field.get(copy));
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyConstructorByNullValue() {
        new Event((Event) null);
    }

    @Test
    public void testCopyConstructorByFlozenInstance() {
        Event source = new Event();
        Assert.assertFalse(source.isFrozen());

        source.freeze();
        Assert.assertTrue(source.isFrozen());

        Assert.assertFalse(new Event(source).isFrozen());
    }

    @Test
    public void testToJsonWhenBeginDateExistsAndEndDateIsNull() {
        Event event = new Event();
        event.setBeginDate(new DateTime(0L));
        JSONObject json = event.toSafeJSON();
        Assert.assertEquals("1970-01-01 09:00", json.getString("beginDate"));
        Assert.assertFalse(json.containsKey("endDate"));
    }
}
