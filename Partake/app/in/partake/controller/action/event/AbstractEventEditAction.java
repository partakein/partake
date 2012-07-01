package in.partake.controller.action.event;

import in.partake.controller.action.AbstractPartakeAction;
import in.partake.model.EventEx;

public abstract class AbstractEventEditAction extends AbstractPartakeAction {
    protected EventEx event;

    public EventEx getEvent() {
        return event;
    }
}
