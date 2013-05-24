package in.partake.controller.api.event;

import in.partake.app.PartakeApp;
import in.partake.base.DateTime;
import in.partake.base.PartakeException;
import in.partake.base.TimeUtil;
import in.partake.base.Util;
import in.partake.controller.api.AbstractPartakeAPI;
import in.partake.controller.base.permission.EventEditPermission;
import in.partake.model.IPartakeDAOs;
import in.partake.model.UserEx;
import in.partake.model.access.Transaction;
import in.partake.model.dao.DAOException;
import in.partake.model.dao.PartakeConnection;
import in.partake.model.daofacade.EventDAOFacade;
import in.partake.model.daofacade.UserDAOFacade;
import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;
import in.partake.model.dto.UserImage;
import in.partake.model.dto.auxiliary.EventCategory;
import in.partake.resource.UserErrorCode;
import in.partake.service.IEventSearchService;
import in.partake.view.util.Helper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.mvc.Result;

public class ModifyAPI extends AbstractPartakeAPI {

    public static Result post() throws DAOException, PartakeException {
        return new ModifyAPI().execute();
    }

    @Override
    protected Result doExecute() throws DAOException, PartakeException {
        UserEx user = ensureLogin();
        ensureValidSessionToken();
        String eventId = getValidEventIdParameter();
        Map<String, String[]> params = getFormParameters();

        ModifyTransaction transaction = new ModifyTransaction(user, eventId, params);
        transaction.execute();

        Event event = transaction.getEvent();
        List<EventTicket> tickets = transaction.getEventTickets();

        // If the event is already published, We update event search index.
        IEventSearchService searchService = PartakeApp.getEventSearchService();
        if (!event.isSearchable())
            searchService.remove(eventId);
        else if (searchService.hasIndexed(eventId))
            searchService.update(event, tickets);
        else
            searchService.create(event, tickets);

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.putAll(transaction.getJSONObject());
        return renderOK(obj);
    }
}

class ModifyTransaction extends Transaction<Void> {
    private UserEx user;
    private String eventId;
    private Map<String, String[]> params;

    private Event event;
    private List<EventTicket> tickets;
    private ObjectNode json;

    public ModifyTransaction(UserEx user, String eventId, Map<String, String[]> params) {
        this.user = user;
        this.eventId = eventId;
        this.params = params;

        // When event information is changed, you can add the editted value here to return to the client.
        // For example, you can filter script tag for event description, and so on.
        this.json = new ObjectNode(JsonNodeFactory.instance);
    }

    @Override
    protected Void doExecute(PartakeConnection con, IPartakeDAOs daos) throws DAOException, PartakeException {
        event = daos.getEventAccess().find(con, eventId);
        if (event == null)
            throw new PartakeException(UserErrorCode.INVALID_EVENT_ID);
        if (!EventEditPermission.check(event, user))
            throw new PartakeException(UserErrorCode.FORBIDDEN_EVENT_EDIT);

        event = new Event(event);
        updateEvent(con, daos);

        event.setModifiedAt(TimeUtil.getCurrentDateTime());
        EventDAOFacade.modify(con, daos, event);

        tickets = daos.getEventTicketAccess().findEventTicketsByEventId(con, eventId);

        return null;
    }

    private void updateEvent(PartakeConnection con, IPartakeDAOs daos) throws PartakeException, DAOException {
        if (params.containsKey("title")) {
            String title = getString("title");
            if (StringUtils.isBlank(title) || title.length() > 100)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "title", "タイトルは 100 文字以下で必ず入力してください。");
            else
                event.setTitle(title);
        }

        if (params.containsKey("summary")) {
            String summary = getString("summary");
            if (summary != null && summary.length() > 100)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "summary", "概要は 100 文字以下で入力してください。");
            else
                event.setSummary(summary);
        }

        if (params.containsKey("category")) {
            String category = getString("category");
            if (category == null || !EventCategory.isValidCategoryName(category))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "category", "カテゴリーは正しいものを必ず入力してください。");
            else
                event.setCategory(category);
        }

        if (params.containsKey("beginDate")) {
            DateTime beginDate = getDateTime("beginDate");
            if (beginDate == null)
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "beginDate", "開始日時は必ず入力してください。");

            Calendar beginCalendar = TimeUtil.calendar(beginDate.toDate());
            if (beginCalendar.get(Calendar.YEAR) < 2000 || 2100 < beginCalendar.get(Calendar.YEAR))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "beginDate", "開始日時の範囲が不正です。");
            else
                event.setBeginDate(beginDate);

            json.put("eventDuration", Helper.readableDuration(event.getBeginDate(), event.getEndDate()));
        }

        // TODO: What happend if beginDate is set after endDate is set and endDate <= beginDate. It should be an error.
        if (params.containsKey("endDate")) {
            String endDateStr = getString("endDate");
            if (StringUtils.isBlank(endDateStr))
                event.setEndDate(null);
            else {
                DateTime endDate = TimeUtil.parseForEvent(endDateStr);
                if (endDate == null)
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "endDate", "終了日時が不正です。");
                else {
                    Calendar endCalendar = TimeUtil.calendar(endDate.toDate());
                    if (endCalendar.get(Calendar.YEAR) < 2000 || 2100 < endCalendar.get(Calendar.YEAR))
                        throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "endDate", "終了日時の範囲が不正です。");
                    else if (event.getBeginDate() != null && endDate.isBefore(event.getBeginDate()))
                        throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "endDate", "終了日時が開始日時より前になっています。");
                    else
                        event.setEndDate(endDate);
                }
            }
            json.put("eventDuration", Helper.readableDuration(event.getBeginDate(), event.getEndDate()));
        }

        if (params.containsKey("url")) {
            String urlStr = getString("url");
            if (StringUtils.isBlank(urlStr))
                event.setUrl(urlStr);
            else if (3000 < urlStr.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "url", "URL が長すぎます。");
            else if (!urlStr.startsWith("http://") && !urlStr.startsWith("https://"))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "url", "URL が不正です。");
            else {
                try {
                    new URL(urlStr);  // Confirms URL is not malformed.
                    event.setUrl(urlStr);
                } catch (MalformedURLException e) {
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "url", "URL が不正です。");
                }
            }
        }

        if (params.containsKey("place")) {
            String place = getString("place");
            if (place != null && 300 < place.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "place", "場所が長すぎます");
            else
                event.setPlace(place);
        }

        if (params.containsKey("address")) {
            String address = getString("address");
            if (address != null && 300 < address.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "address", "住所が長すぎます。");
            else
                event.setAddress(address);
        }

        if (params.containsKey("description")) {
            String description = getString("description");
            if (description != null && 1000000 < description.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "description", "説明は 1000000 文字以下で入力してください。");
            else
                event.setDescription(description);
        }

        if (params.containsKey("hashTag")) {
            String hashTag = getString("hashTag");
            if (StringUtils.isBlank(hashTag))
                event.setHashTag(null);
            else if (100 < hashTag.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "hashTag", "ハッシュタグは１００文字以内で記述してください。");
            else if (!Util.isValidHashtag(hashTag))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "hashTag", "ハッシュタグは # から始まる英数字や日本語が指定できます。記号は使えません。");
            else
                event.setHashTag(hashTag);
        }

        if (params.containsKey("passcode")) {
            String passcode = getString("passcode");
            if (StringUtils.isBlank(passcode))
                event.setPasscode(null);
            else if (20 < passcode.length())
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "passcode", "パスコードは20文字以下で記入してください。");
            else
                event.setPasscode(passcode);
        }

        if (params.containsKey("foreImageId")) {
            String foreImageId = getString("foreImageId");
            if (StringUtils.isBlank(foreImageId) || "null".equals(foreImageId))
                event.setForeImageId(null);
            else if (!Util.isUUID(foreImageId))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "foreImageId", "画像IDが不正です。");
            else {
                // Check foreImageId is owned by the owner.
                UserImage image = daos.getImageAccess().find(con, foreImageId);
                if (image == null)
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "foreImageId", "画像IDが不正です。");
                if (!user.getId().equals(image.getUserId()))
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "foreImageId", "あなたが所持していない画像の ID が指定されています。");

                // OK.
                event.setForeImageId(foreImageId);
            }
        }

        if (params.containsKey("backImageId")) {
            String backImageId = getString("backImageId");
            if (StringUtils.isBlank(backImageId) || "null".equals(backImageId))
                event.setBackImageId(null);
            else if (!Util.isUUID(backImageId))
                throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "backImageId", "画像IDが不正です。");
            else {
                // Check foreImageId is owned by the owner.
                UserImage image = daos.getImageAccess().find(con, backImageId);
                if (image == null)
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "backImageId", "画像IDが不正です。");
                if (!user.getId().equals(image.getUserId()))
                    throw new PartakeException(UserErrorCode.INVALID_PARAMETERS, "backImageId", "あなたが所持していない画像の ID が指定されています。");

                // OK.
                event.setBackImageId(backImageId);
            }
        }

        if (params.containsKey("relatedEventIds[]")) {
            Set<String> visitedIds = new HashSet<String>();
            String[] relatedEventIds = getStrings("relatedEventIds[]");

            List<String> eventIds = new ArrayList<String>();
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            for (String relatedEventId : relatedEventIds) {
                if (!Util.isUUID(relatedEventId))
                    continue;

                if (eventId.equals(relatedEventId))
                    continue;

                if (visitedIds.contains(relatedEventId))
                    continue;

                visitedIds.add(relatedEventId);

                Event relatedEvent = daos.getEventAccess().find(con, relatedEventId);
                if (relatedEvent == null)
                    continue;

                eventIds.add(relatedEventId);

                ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
                obj.put("id", relatedEvent.getId());
                obj.put("title", relatedEvent.getTitle());
                array.add(obj);
            }
            event.setRelatedEventIds(eventIds);

            // OK. We want to return event.id and event.title.
            json.put("relatedEvents", array);
        }

        if (params.containsKey("editorIds[]")) {
            ArrayNode array = new ArrayNode(JsonNodeFactory.instance);
            List<String> editorIds = new ArrayList<String>();
            Set<String> visitedIds = new HashSet<String>();
            for (String editorId : getStrings("editorIds[]")) {
                // Skips invalid users here.
                if (!Util.isUUID(editorId))
                    continue;

                if (visitedIds.contains(editorId))
                    continue;
                visitedIds.add(editorId);

                UserEx editor = UserDAOFacade.getUserEx(con, daos, editorId);
                if (editor == null)
                    continue;

                // OK.
                editorIds.add(editor.getId());
                array.add(editor.toSafeJSON());
            }

            event.setEditorIds(editorIds);
            json.put("editors", array);
        }
    }

    private String getString(String key) {
        Object obj = params.get(key);
        if (obj instanceof String)
            return (String) obj;
        else if (obj instanceof String[] && ((String[]) obj).length > 0)
            return ((String[]) obj)[0];
        else
            return null;
    }

    private String[] getStrings(String key) {
        Object obj = params.get(key);
        if (obj instanceof String)
            return new String[] { (String) obj };
        else if (obj instanceof String[])
            return (String[]) obj;
        else
            return null;
    }

    private DateTime getDateTime(String key) {
        String value = getString(key);
        if (value == null)
            return null;

        DateTime date = TimeUtil.parseForEvent(value);
        if (date != null)
            return date;

        return null;
    }

    public Event getEvent() {
        return event;
    }

    public List<EventTicket> getEventTickets() {
        return tickets;
    }

    public ObjectNode getJSONObject() {
        return json;
    }
}
