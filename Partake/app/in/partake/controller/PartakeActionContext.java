package in.partake.controller;

import java.util.List;

import in.partake.model.UserEx;
import in.partake.resource.MessageCode;

public interface PartakeActionContext {
    public abstract UserEx loginUser();
    public abstract String sessionToken();
    public abstract String currentURL();

    public abstract String redirectURL();
    public abstract void setRedirectURL(String redirectURL);


    public abstract void addMessage(MessageCode mc);
    public abstract List<MessageCode> messages();
}

