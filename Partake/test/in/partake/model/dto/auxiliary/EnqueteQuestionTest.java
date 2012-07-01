package in.partake.model.dto.auxiliary;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class EnqueteQuestionTest {

    @Test
    public void constructorShouldnotThrowNullPointerException() throws Exception {
        UUID id = UUID.randomUUID();
        EnqueteQuestion q = new EnqueteQuestion(id, "text", EnqueteAnswerType.TEXT, null);

        assertThat(q.getId(), is(id));
        assertThat(q.getText(), is("text"));
        assertThat(q.getAnswerType(), is(EnqueteAnswerType.TEXT));
        assertThat(q.getOptions(), is(nullValue()));
    }
}
