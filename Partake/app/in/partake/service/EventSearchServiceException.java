package in.partake.service;

import in.partake.base.PartakeException;
import in.partake.resource.ServerErrorCode;

public class EventSearchServiceException extends PartakeException {
    private static final long serialVersionUID = 1L;

    public EventSearchServiceException() {
        super(ServerErrorCode.EVENT_SEARCH_SERVICE_ERROR);
    }
    
    public EventSearchServiceException(Throwable t) {
        super(ServerErrorCode.EVENT_SEARCH_SERVICE_ERROR, t);
    }
}
