package in.partake.controller;

import static play.test.Helpers.GET;
import static play.test.Helpers.POST;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.routeAndCall;
import in.partake.base.Pair;
import in.partake.controller.base.AbstractPartakeController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import play.mvc.Result;
import play.test.FakeRequest;
import play.test.FakeRequestOperation;
import play.test.Helpers;

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
        else {
            // Since request.withFormUrlEncodedBody takes Map<String, String>, we cannot add multiple values for one key.
            // It should be Map<String, String[]>, I believe. However, it is converted to (String, String)* and converted
            // to (String, Seq[String]) in scala.
            // So if we can add multiple keys that are actually the equivalent keys, we can add multiple values actually.
            // So IdnetityHashMap really works well here :-)  ... This is just a hack.
            IdentityHashMap<String, String> map = new IdentityHashMap<String, String>();
            for (Entry<String, List<String>> entry : formParams.entrySet()) {
                for (String value : entry.getValue()) {
                    String key = new String(entry.getKey());
                    map.put(key, value);
                }
            }

            request.withFormUrlEncodedBody(map);
        }

        if (session.isEmpty())
            request.withSession("", "");
        else {
            List<Pair<String, String>> sessions = new ArrayList<Pair<String,String>>();
            for (Entry<String, String> entry: session.entrySet())
                sessions.add(new Pair<String, String>(entry.getKey(), entry.getValue()));
            FakeRequestOperation.addSession(request, sessions);
        }

        result = routeAndCall(request);

        // Ensure that response has been done to keep backward compatibility:
        //   from Play! 2.1.x, it might be AsyncResult. To ensure that request
        //   has been done, we need to call Helpers.xxx()
        Helpers.status(result);
    }

    public Result getResult() {
        return result;
    }
}
