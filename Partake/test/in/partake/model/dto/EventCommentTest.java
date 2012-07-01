package in.partake.model.dto;

import in.partake.app.PartakeTestApp;
import in.partake.base.DateTime;
import in.partake.model.fixture.TestDataProvider;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author skypencil (@eller86)
 */
public final class EventCommentTest extends AbstractPartakeModelTest<EventComment> {
    private EventComment[] samples;

    @Override
    protected EventComment copy(EventComment t) {
        return new EventComment(t);
    }

    @Override
    protected TestDataProvider<EventComment> getTestDataProvider() {
        return PartakeTestApp.getTestService().getTestDataProviderSet().getCommentDataProvider();
    }

    @Before
    public void createSamples() {
        samples = new EventComment[] {
            new EventComment("id1", "eventId1", "userId1", "comment1", false, new DateTime(0)),
            new EventComment("id2", "eventId2", "userId2", "comment2", true, new DateTime(1)),
        };
    }

    @Test
    public void testCopyConstructor() {
        for (EventComment source : samples) {
            Assert.assertEquals(source.getId(), new EventComment(source).getId());
            Assert.assertEquals(source.getEventId(), new EventComment(source).getEventId());
            Assert.assertEquals(source.getUserId(), new EventComment(source).getUserId());
            Assert.assertEquals(source.getComment(), new EventComment(source).getComment());
            Assert.assertEquals(source.getCreatedAt(), new EventComment(source).getCreatedAt());
        }
    }

    @Test
    public void testCopyConstructorByReflection() throws IllegalArgumentException, IllegalAccessException {
        for (EventComment source : samples) {
            EventComment copy = new EventComment(source);

            for (Field field : EventComment.class.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    field.setAccessible(true);
                    Assert.assertEquals(field.get(source), field.get(copy));
                }
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCopyConstructorByNullValue() {
        new EventComment((EventComment) null);
    }

    @Test
    public void testCopyConstructorByFlozenInstance() {
        EventComment source = getTestDataProvider().create();
        Assert.assertFalse(source.isFrozen());

        source.freeze();
        Assert.assertTrue(source.isFrozen());

        Assert.assertFalse(new EventComment(source).isFrozen());
    }
}
