package in.partake.controller.api.user;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.resource.UserErrorCode;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

public class GetUserAPITest extends APIControllerTest {

    @Test
    public void testGetUser() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/user/get?userId=" + DEFAULT_USER_ID);

        proxy.execute();
        assertResultOK(proxy);

        ObjectNode obj = getJSON(proxy);
        assertThat(obj.get("id").asText(), is(DEFAULT_USER_ID));
        // These values should not be public.
        assertThat(obj.get("twitterId"), is(nullValue()));
        assertThat(obj.get("lastLoginAt"), is(nullValue()));
        assertThat(obj.get("calendarId"), is(nullValue()));
        JsonNode twitter = obj.get("twitter");
        assertThat(twitter, is(notNullValue()));
        assertThat(twitter.get("screenName").asText(), is(DEFAULT_TWITTER_SCREENNAME));
        assertThat(twitter.get("profileImageURL").asText(), is("http://www.example.com/"));
        // These values should not be public.
        assertThat(twitter.get("accessToken"), is(nullValue()));
        assertThat(twitter.get("accessTokenSecret"), is(nullValue()));

        // We don't expose OpenID Linkage.
        assertThat(obj.get("openIDLinakge"), is(nullValue()));
    }

    @Test
    public void testGetUserWithInvalidUserId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/user/get?userId=" + INVALID_USER_ID);
        proxy.execute();

        assertResultInvalid(proxy, UserErrorCode.INVALID_USER_ID);
    }

    @Test
    public void testGetUserWithoutUserId() throws Exception {
        ActionProxy proxy = getActionProxy(GET, "/api/user/get");
        proxy.execute();

        assertResultInvalid(proxy, UserErrorCode.MISSING_USER_ID);
    }
}
