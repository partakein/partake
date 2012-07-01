package in.partake.controller;

import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.routeAndCall;
import in.partake.controller.base.AbstractPartakeController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import play.mvc.Result;
import play.test.FakeRequest;

public class ActionProxy {
    private FakeRequest request;
    private Result result;
    private Map<String, String> session;
    private Map<String, List<String>> formParams;

    public static ActionProxy get(String url) {
        return new ActionProxy(fakeRequest(GET, url));
    }

    public static ActionProxy post(String url) {
        return new ActionProxy(fakeRequest(POST, url));
    }

    private ActionProxy(FakeRequest request) {
        this.request = request;
        this.session = new HashMap<String, String>();
        this.formParams = new HashMap<String, List<String>>();
    }

    public AbstractPartakeController getAction() {
        return PartakeTestContext.getAction();
    }

    public void addSession(String key, String value) {
        session.put(key, value);
        request.withSession(key, value);
    }

    public void addFormParameter(String key, String value) {
        List<String> params;
        if (formParams.containsKey(key))
            params = formParams.get(key);
        else
            params = new ArrayList<String>();

        params.add(value);
        formParams.put(key, params);
    }

    public String session(String key) {
        return session.get(key);
    }

    public void execute() throws Exception {
        if (formParams.isEmpty())
            request.withFormUrlEncodedBody(new HashMap<String, String>());
        for (Entry<String, List<String>> entry : formParams.entrySet()) {
            String key = entry.getKey();
            for (String value : entry.getValue())
                request.withFormUrlEncodedBody(Collections.singletonMap(key, value));
        }

        if (session.isEmpty())
        	request.withSession("", "");

        result = routeAndCall(request);
    }

    public Result getResult() {
        return result;
    }
}
