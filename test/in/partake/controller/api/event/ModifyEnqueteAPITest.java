package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Event;
import in.partake.model.dto.auxiliary.EnqueteAnswerType;
import in.partake.model.dto.auxiliary.EnqueteQuestion;

import java.util.List;

import org.junit.Test;

import in.partake.controller.ActionProxy;

public class ModifyEnqueteAPITest extends APIControllerTest {

    @Test
    public void testToModify() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modifyEnquete");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", UNPUBLISHED_EVENT_ID);

        addFormParameter(proxy, "ids[]", new String[] { "" });
        addFormParameter(proxy, "questions[]", new String[] { "test" });
        addFormParameter(proxy, "types[]", new String[] { "text" });
        addFormParameter(proxy, "options[]", new String[] { "[]" });

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(UNPUBLISHED_EVENT_ID);
        List<EnqueteQuestion> questions = modified.getEnquetes();
        assertThat(questions.size(), is(1));
        assertThat(questions.get(0).getAnswerType(), is(EnqueteAnswerType.TEXT));
        assertThat(questions.get(0).getText(), is("test"));
    }
}
