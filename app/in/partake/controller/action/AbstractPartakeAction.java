package in.partake.controller.action;

import in.partake.controller.base.AbstractPartakeController;
import in.partake.resource.Constants;
import in.partake.resource.MessageCode;
import in.partake.resource.ServerErrorCode;
import in.partake.resource.UserErrorCode;

import java.util.Map;

import play.Logger;
import play.mvc.Content;
import play.mvc.Result;

public abstract class AbstractPartakeAction extends AbstractPartakeController {

    // ----------------------------------------------------------------------
    // Renderers

    protected Result render(Content content) {
        return ok(content);
    }

    protected Result render(byte[] body, String contentType, String contentDisposition) {
        response().setContentType(contentType);
        response().setHeader("Content-Disposition", contentDisposition);
        return ok(body);
    }

    @Override
    protected Result renderInvalid(UserErrorCode ec, Map<String, String> additionalInfo, Throwable e) {
        if (e != null)
            Logger.info("renderInvalid", e);

        if (ec != null)
            return renderRedirect("/invalid?errorCode=" + ec.getErrorCode());

        return  renderRedirect("/invalid");
    }

    @Override
    protected Result renderError(ServerErrorCode ec, Map<String, String> additionalInfo, Throwable e) {
        if (e != null)
            Logger.info("redirectError", e);

        if (ec != null)
            return renderRedirect("/error?errorCode=" + ec.getErrorCode());

        return renderRedirect("/error");
    }

    protected Result renderLoginRequired() {
        context().setRedirectURL(request().uri());
        return renderRedirect("/loginRequired");
    }

    // TODO: renderRedirect はなにか引数を１つ取って、それを表示できるようにするべきだなあ……
    // addActionMessage, addWarningMessage, addErrorMessage などは全部廃止。
    // 表示用の文字列は Session に入れておくしかないのかな……。
    /**
     * redirect to the specified URL.
     */
    protected Result renderRedirect(String url, MessageCode messageCode) {
        if (messageCode != null)
            flash().put(Constants.Flash.MESSAGE_ID, messageCode.getErrorCode());
        return redirect(url);
    }

    protected Result renderRedirect(String url) {
        return renderRedirect(url, null);
    }

    /**
     * show the 'forbidden' page when a user did something prohibited.
     * @return
     */
    protected Result renderForbidden() {
        return renderForbidden(null);
    }

    protected Result renderForbidden(UserErrorCode ec) {
        if (ec != null)
            return renderRedirect("/forbidden?errorCode=" + ec.getErrorCode());

        return renderRedirect("/forbidden");
    }

    /**
     * show the 'not found' page.
     * @return
     */
    protected Result renderNotFound() {
        return renderRedirect("/notfound");
    }
}
