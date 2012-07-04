package in.partake.controller.action.event;

import in.partake.controller.ActionProxy;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Before;
import org.junit.Test;

public class EventSearchTest extends ActionControllerTest {
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void testLoginIsNotRequired() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/search?searchTerm=term&category=all&sortOrder=score");

        proxy.execute();
        assertResultSuccess(proxy);
    }

    @Test
    public void testToUseUnknownCategory() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/search?searchTerm=term&category=unknown&sortOrder=score");

        proxy.execute();
        assertResultSuccess(proxy);
    }

    /**
     * 存在しないソート順を指定して検索した場合、スコア順にソートされて返却される
     */
    @Test
    public void testToUseUnknownSortOrder() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/events/search?searchTerm=term&category=all&sortOrder=unknown");

        proxy.execute();
        assertResultSuccess(proxy);

        // TODO スコア順にソートされていることを確認
    }
}
