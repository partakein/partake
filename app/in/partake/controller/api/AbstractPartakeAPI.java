package in.partake.controller.api;

import in.partake.controller.base.AbstractPartakeController;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;

import play.mvc.Result;

public abstract class AbstractPartakeAPI extends AbstractPartakeController {
    // TODO(mayah): UTF8 should be shared around the project, I believe.
    private static final Charset UTF8 = Charset.forName("utf-8");

    // ----------------------------------------------------------------------
    //

    protected void addHeader(String key, String value) {
        response().getHeaders().put(key, value);
    }

    // ----------------------------------------------------------------------
    // Rendering

    /**
     * JSON object をレスポンスとして返す。
     * @param obj
     * @return
     */
    protected Result renderJSON(ObjectNode obj) {
        return renderJSON(obj, OK);
    }

    protected Result renderJSON(ObjectNode obj, int status) {
        return renderJSONWith(obj, status, "application/json; charset=utf-8");
    }

    protected Result renderJSONWith(ObjectNode obj, int status, String contentType) {
        assert obj != null;
        response().setContentType(contentType);
        response().setHeader("Cache-Control", "no-cache");
        return status(status, obj.toString().getBytes(UTF8));
    }

    /**
     * <code>{ "result": "ok" }</code> をレスポンスとして返す。
     * with status code 200.
     * @return
     */
    protected Result renderOK() {
        return renderOK(new ObjectNode(JsonNodeFactory.instance));
    }

    /**
     * obj に result: ok を追加して返す。obj に result が既に含まれていれば RuntimeException を投げる。
     * @param obj
     * @return
     */
    protected Result renderOK(ObjectNode obj) {
        if (obj.has("result"))
            throw new RuntimeException("obj should not contain result");

        obj.put("result", "ok");
        return renderJSON(obj);
    }

    @Deprecated
    protected Result renderOKWith(ObjectNode obj, String contentType) {
        if (obj.has("result"))
            throw new RuntimeException("obj should not contain result");
        obj.put("result", "ok");
        return renderJSONWith(obj, OK, contentType);
    }

    /**
     * <code>{ "result": "error", "reason": reason }</code> をレスポンスとして返す。
     * ステータスコードは 500 を返す。
     */
    @Override
    protected Result renderError(ServerErrorCode errorCode, Map<String, String> additionalInfo, Throwable e) {
        assert errorCode != null;

        final String reasonString = errorCode.toString() + ":" + errorCode.getReasonString();
        if (e != null) { Logger.error(reasonString, e); }
        else { Logger.error(reasonString); }

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("result", "error");
        obj.put("reason", errorCode.getReasonString());
        if (additionalInfo != null) {
            ObjectNode info = new ObjectNode(JsonNodeFactory.instance);
            for (Entry<String, String> entry : additionalInfo.entrySet())
                info.put(entry.getKey(), entry.getValue());
            obj.put("additional", info);
        }

        return renderJSON(obj, INTERNAL_SERVER_ERROR);
    }

    @Override
    protected Result renderInvalid(UserErrorCode ec, Map<String, String> additionalInfo, Throwable e) {
        assert ec != null;

        if (e != null)
            Logger.info("renderInvalid", e);

        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("result", "invalid");
        obj.put("reason", ec.getReasonString());
        if (additionalInfo != null) {
            ObjectNode info = new ObjectNode(JsonNodeFactory.instance);
            for (Entry<String, String> entry : additionalInfo.entrySet())
                info.put(entry.getKey(), entry.getValue());
            obj.put("additional", info);
        }

        return renderJSON(obj, BAD_REQUEST);
    }

    protected Result renderLoginRequired() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("result", "auth");
        obj.put("reason", "login is required");

        addHeader("WWW-Authenticate", "OAuth");
        return renderJSON(obj, UNAUTHORIZED);
    }

    protected Result renderForbidden() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("result", "forbidden");
        obj.put("reason", "forbidden action");

        return renderJSON(obj, FORBIDDEN);
    }

    protected Result renderForbidden(UserErrorCode ec) {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("result", "forbidden");
        obj.put("reason", ec.getReasonString());
        obj.put("errorCode", ec.toString());

        return renderJSON(obj, FORBIDDEN);
    }

    protected Result renderNotFound() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("result", "notfound");
        obj.put("reason", "not found");
        return renderJSON(obj, NOT_FOUND);
    }
}
