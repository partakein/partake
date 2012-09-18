package in.partake.controller.api.event;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.TimeZone;

import in.partake.base.DateTime;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.ActionProxy;
import in.partake.controller.api.APIControllerTest;
import in.partake.model.dto.Event;
import in.partake.resource.UserErrorCode;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ModifyAPITest extends APIControllerTest {
    private TimeZone defaultTimeZone;

    @Before
    public void setTimeZone() {
        defaultTimeZone = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tokyo"));
    }

    @After
    public void resetTimeZone() {
        TimeZone.setDefault(defaultTimeZone);
    }

    @Test
    public void testToModifyWithoutLogin() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");

        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "title", "modified");

        proxy.execute();
        assertResultLoginRequired(proxy);
    }

    @Test
    public void testToModifyWithInvalidSessionToken() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");

        loginAs(proxy, EVENT_OWNER_ID);
        addInvalidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "title", "modified");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_SECURITY_CSRF);
    }

    @Test
    public void testToModifyWithInvalidEventId1() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");

        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", INVALID_EVENT_ID);
        addFormParameter(proxy, "title", "modified");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    @Test
    public void testToModifyWithInvalidEventId2() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");

        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", "non-uuid");
        addFormParameter(proxy, "title", "modified");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_EVENT_ID);
    }

    // --- title

    @Test
    public void testToModifyTitle() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "title", "modified");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getTitle(), is("modified"));
    }

    @Test
    public void testToModifyTitleEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "title", "");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "title");
    }

    @Test
    public void testToModifyTitleTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "title", Util.randomString(101));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "title");
    }

    @Test
    public void testToModifyTitleLongEnough() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "title", Util.randomString(100));

        proxy.execute();
        assertResultOK(proxy);
    }

    // --- summary

    @Test
    public void testToModifySummary() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "summary", "modified");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getSummary(), is("modified"));
    }

    @Test
    public void testToModifySummaryEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "summary", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getSummary(), is(""));
    }

    @Test
    public void testToModifySummaryTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "summary", Util.randomString(101));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "summary");
    }

    @Test
    public void testToModifySummaryLongEnough() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "summary", Util.randomString(100));

        proxy.execute();
        assertResultOK(proxy);
    }

    // --- category

    @Test
    public void testToModifyCategory() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "category", "others");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getCategory(), is("others"));
    }

    @Test
    public void testToModifyCategoryEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "category", "");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "category");
    }

    @Test
    public void testToModifyCategoryInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "category", "invalidCategory");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "category");
    }

    // --- begindate

    @Test
    public void testToModifyBeginDate() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", "2012-08-01 00:00");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getBeginDate(), is(TimeUtil.create(2012, 8, 1, 0, 0, 0)));
    }

    @Test
    public void testToModifyBeginDateFromEpoc() throws Exception {
        DateTime dt = TimeUtil.create(2012, 8, 1, 0, 0, 0);

        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", String.valueOf(dt.getTime()));

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getBeginDate(), is(dt));
    }

    @Test
    public void testToModifyBeginDateInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", "invalid");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "beginDate");
    }

    @Test
    public void testToModifyBeginDateEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", "");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "beginDate");
    }

    @Test
    public void testToModifyBeginDateInvalidRange() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", "1970-01-01 09:00");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "beginDate");
    }

    @Test
    public void testToModifyBeginDateInvalidRange2() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", "1900-01-01 09:00");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "beginDate");
    }

    @Test
    public void testToModifyBeginDateInvalidRange3() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "beginDate", "2200-01-01 09:00");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "beginDate");
    }

    // --- enddata

    @Test
    public void testToModifyEndDate() throws Exception {
        DateTime newEndDate = TimeUtil.getCurrentDateTime().nDayAfter(10).adjustByMinutes();
        
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", newEndDate.toHumanReadableFormat());
        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getEndDate(), is(newEndDate));
    }

    @Test
    public void testToModifyEndDateFromEpoc() throws Exception {
        DateTime tomorrow = TimeUtil.getCurrentDateTime().nDayAfter(1);

        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", String.valueOf(tomorrow.getTime()));

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getEndDate(), is(tomorrow));
    }

    @Test
    public void testToModifyEndDateInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", "invalid");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "endDate");
    }

    @Test
    public void testToModifyEndDateEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getEndDate(), is(nullValue()));
    }


    @Test
    public void testToModifyEndDateInvalidRange() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", "1970-01-01 09:00");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "endDate");
    }

    @Test
    public void testToModifyEndDateInvalidRange2() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", "1900-01-01 09:00");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "endDate");
    }

    @Test
    public void testToModifyEndDateInvalidRange3() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "endDate", "2200-01-01 09:00");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "endDate");
    }

    // --- url

    @Test
    public void testToModifyUrl() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "url", "http://www.example.com/hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getUrl(), is("http://www.example.com/hogehoge"));
    }

    @Test
    public void testToModifyUrlValidHttps() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "url", "https://www.example.com/hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getUrl(), is("https://www.example.com/hogehoge"));
    }

    @Test
    public void testToModifyUrlEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "url", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getUrl(), is(""));
    }

    @Test
    public void testToModifyUrlTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "url", "http://" + Util.randomString(4000));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "url");
    }

    @Test
    public void testToModifyUrlInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "url", "invalid://www.example.com/");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "url");
    }

    // --- place

    @Test
    public void testToModifyPlace() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "place", "hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getPlace(), is("hogehoge"));
    }

    @Test
    public void testToModifyPlaceEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "place", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getPlace(), is(""));
    }

    @Test
    public void testToModifyPlaceTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "place", Util.randomString(400));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "place");
    }

    // --- address

    @Test
    public void testToModifyAddress() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "address", "hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getAddress(), is("hogehoge"));
    }

    @Test
    public void testToModifyAddressEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "address", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getAddress(), is(""));
    }


    @Test
    public void testToModifyAddressTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "address", Util.randomString(301));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "address");
    }

    // --- description

    @Test
    public void testToModifyDescription() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "description", "hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getDescription(), is("hogehoge"));
    }

    @Test
    public void testToModifyDescriptionEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "description", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getDescription(), is(""));
    }

    @Test
    public void testToModifyDescritpionTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "description", Util.randomString(1000 * 1000 + 1));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "description");
    }

    // --- hashtag

    @Test
    public void testToModifyHashTag() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "hashTag", "#hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getHashTag(), is("#hogehoge"));
    }

    @Test
    public void testToModifyHashTagEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "hashTag", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getHashTag(), is(nullValue()));
    }

    @Test
    public void testToModifyHashTagTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "hashTag", "#" + Util.randomString(200));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "hashTag");
    }

    @Test
    public void testToModifyHashTagInvalid() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "hashTag", "mogemoge");

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "hashTag");
    }

    // --- passcode

    @Test
    public void testToModifyPasscode() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "passcode", "hogehoge");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getPasscode(), is("hogehoge"));
    }

    @Test
    public void testToModifyPasscodeEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "passcode", "");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getPasscode(), is(nullValue()));
    }

    @Test
    public void testToModifyPasscodeTooLong() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "passcode", Util.randomString(21));

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "passcode");
    }

    // --- foreimageid

    @Test
    public void testToModifyForeImage() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "foreImageId", EVENT_FOREIMAGE_ID);

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getForeImageId(), is(EVENT_FOREIMAGE_ID));
    }

    @Test
    public void testToModifyForeImageEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "foreImageId", "");

        proxy.execute();
        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getForeImageId(), is(nullValue()));
    }

    @Test
    public void testToModifyForeImageNotOwned() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "foreImageId", IMAGE_OWNER_IMAGE_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "foreImageId");
    }

    // backimageid

    @Test
    public void testToModifyBackImage() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "backImageId", EVENT_FOREIMAGE_ID);

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getBackImageId(), is(EVENT_FOREIMAGE_ID));
    }

    @Test
    public void testToModifyBackImageEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "backImageId", "");

        proxy.execute();
        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getBackImageId(), is(nullValue()));
    }

    @Test
    public void testToModifyBackImageNotOwned() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "backImageId", IMAGE_OWNER_IMAGE_ID);

        proxy.execute();
        assertResultInvalid(proxy, UserErrorCode.INVALID_PARAMETERS, "backImageId");
    }

    // --- relatedEventIds[]

    @Test
    public void testToModifyRelatedEventIdsEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "relatedEventIds[]", "[]");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getRelatedEventIds().size(), is(0));
    }

    // --- editorIds[]

    @Test
    public void testToModifyEditorIdsEmpty() throws Exception {
        ActionProxy proxy = getActionProxy(POST, "/api/event/modify");
        loginAs(proxy, EVENT_OWNER_ID);
        addValidSessionTokenToParameter(proxy);
        addFormParameter(proxy, "eventId", DEFAULT_EVENT_ID);
        addFormParameter(proxy, "editorIds[]", "[]");

        proxy.execute();
        assertResultOK(proxy);

        Event modified = loadEvent(DEFAULT_EVENT_ID);
        assertThat(modified.getRelatedEventIds().size(), is(0));
    }

}
