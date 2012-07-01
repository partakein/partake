package in.partake.controller.action.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.action.ActionControllerTest;

import org.junit.Test;

public class EventSearchTest extends ActionControllerTest {

    @Test
    public void shouldUpdateThisTest() {
        assertThat(true, is(false));
    }
//
//    @Before
//    public void setUp() throws Exception {
//        super.setUp();
//        setDefaultParams();
//    }
//
//    private void setDefaultParams() {
//        request.setParameter("searchTerm", "term");
//        request.setParameter("category", "all");
//        request.setParameter("sortOrder", "score");
//    }
//
//    @Test
//    public void testLoginIsNotRequired() throws Exception {
//        ActionProxy proxy = getActionProxy(GET, "/events/search");
//        EventSearchAction controller = (EventSearchAction) proxy.getAction();
//        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
//        controller.setRequest(requestMap);
//
//        proxy.execute();
//        assertResultSuccess(proxy);
//    }
//
//    @Test
//    public void testToUseUnknownCategory() throws Exception {
//        request.setParameter("category", "unknown");
//        ActionProxy proxy = getActionProxy(GET, "/events/search");
//        EventSearchAction controller = (EventSearchAction) proxy.getAction();
//        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
//        controller.setRequest(requestMap);
//
//        proxy.execute();
//        assertResultSuccess(proxy);
//    }
//
//    /**
//     * 存在しないソート順を指定して検索した場合、スコア順にソートされて返却される
//     */
//    @Test
//    public void testToUseUnknownSortOrder() throws Exception {
//        request.setParameter("sortOrder", "unknown");
//        ActionProxy proxy = getActionProxy(GET, "/events/search");
//        EventSearchAction controller = (EventSearchAction) proxy.getAction();
//        Map<String, Object> requestMap = new HashMap<String, Object>(request.getParameterMap());
//        controller.setRequest(requestMap);
//
//        proxy.execute();
//        assertResultSuccess(proxy);
//
//        // TODO スコア順にソートされていることを確認
//    }
}
