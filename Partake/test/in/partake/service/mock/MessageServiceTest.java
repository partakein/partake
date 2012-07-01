//package in.partake.service.mock;
//
//import in.partake.base.TimeUtil;
//import in.partake.model.dao.DAOException;
//import in.partake.model.dao.DataIterator;
//import in.partake.model.dao.PartakeConnection;
//import in.partake.model.dao.access.IEventAccess;
//import in.partake.model.dao.mock.MockConnection;
//import in.partake.model.dao.mock.MockConnectionPool;
//import in.partake.model.daofacade.deprecated.DeprecatedMessageDAOFacade;
//import in.partake.model.dto.Event;
//
//import java.lang.reflect.Method;
//import java.util.Date;
//import java.util.NoSuchElementException;
//import java.util.TimeZone;
//
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//
//import static org.mockito.Mockito.*;
//
//
//public class MessageServiceTest extends MockServiceTestBase {
//
//    @Test
//    public void testNeedsToSendWhenLastSentDateIsNull() throws Exception {
//        Assert.assertTrue(needsToSend(
//                TimeUtil.create(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()),
//                null));
//
//        Assert.assertTrue(needsToSend(
//                TimeUtil.create(2000, 1, 10,  0,  0,  0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 23, 59, 59, TimeZone.getDefault()),
//                null));
//
//        Assert.assertFalse(needsToSend(
//                TimeUtil.create(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1, 10, 0, 0, 1, TimeZone.getDefault()),
//                null));
//    }
//
//    @Test
//    public void testNeedsToSendWhenLastSentDateIsNotNull() throws Exception {
//        Assert.assertTrue(needsToSend(
//                TimeUtil.create(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 0, 0, 0, TimeZone.getDefault())));
//
//        Assert.assertTrue(needsToSend(
//                TimeUtil.create(2000, 1, 10,  0,  0,  1, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1, 10,  0,  0,  0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 22, 59, 59, TimeZone.getDefault())));
//
//        Assert.assertFalse(needsToSend(
//                TimeUtil.create(2000, 1, 10,  0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9,  0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 23, 0, 1, TimeZone.getDefault())));
//
//        Assert.assertFalse(needsToSend(
//                TimeUtil.create(2000, 1, 10, 0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 0, 0, 0, TimeZone.getDefault()),
//                TimeUtil.create(2000, 1,  9, 0, 0, 1, TimeZone.getDefault())));
//
//    }
//
//
//    private boolean needsToSend(Date now, Date targetDate, Date lastSent) throws Exception {
//        Method method = DeprecatedMessageDAOFacade.class.getDeclaredMethod("needsToSend", Date.class, Date.class, Date.class);
//        method.setAccessible(true);
//        Object args[] = { now, targetDate, lastSent };
//        Boolean result = (Boolean)method.invoke(DeprecatedMessageDAOFacade.class, args);
//        return result;
//    }
//
//    @Test
//    @Ignore("We should discuss which is correct.")
//    public void sendRemindersEmpty() throws DAOException {
//        MockConnectionPool pool = (MockConnectionPool) getPool();
//        MockConnection mockCon = mock(MockConnection.class);
//        pool.prepareConnection(mockCon);
//
//        DeprecatedMessageDAOFacade service = DeprecatedMessageDAOFacade.get();
//        IEventAccess eventAccess = getFactory().getEventAccess();
//
//        @SuppressWarnings("unchecked")
//        DataIterator<Event> iter = mock(DataIterator.class);
//        doReturn(iter).when(eventAccess).getIterator(mockCon);
//        doReturn(false).when(iter).hasNext();
//        doThrow(new NoSuchElementException()).when(iter).next();
//
//        try {
//            service.sendReminders();
//        } catch (DAOException e) {
//            Assert.fail();
//        }
//
//        verify(eventAccess, times(1)).getIterator(mockCon);
//        verify(mockCon, times(1)).beginTransaction();	// FIXME 変更がない場合は呼ばれない実装になっている、テストが正か変更が正か？
//        verify(mockCon, never()).rollback();
//        verify(mockCon, times(1)).invalidate();
//        verify(mockCon, times(1)).commit();
//    }
//
//    @Test
//    @Ignore("We should discuss which is correct.")
//    public void sendRemindersWithException() throws DAOException {
//        MockConnectionPool pool = (MockConnectionPool) getPool();
//        MockConnection mockCon = mock(MockConnection.class);
//        pool.prepareConnection((MockConnection) mockCon);
//
//        DeprecatedMessageDAOFacade service = DeprecatedMessageDAOFacade.get();
//        DAOException injectedException = new DAOException();
//        doThrow(injectedException).when(getFactory().getEventAccess()).getIterator(any(PartakeConnection.class));
//        doThrow(new DAOException()).when(mockCon).rollback();
//
//        try {
//            service.sendReminders();
//            Assert.fail();
//        } catch (DAOException thrownException) {
//            Assert.assertSame(injectedException, thrownException);
//        }
//
//        verify(mockCon, times(1)).beginTransaction();
//        verify(mockCon, times(1)).invalidate();
//        verify(mockCon, never()).commit();
//    }
//}
