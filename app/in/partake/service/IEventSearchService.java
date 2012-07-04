package in.partake.service;

import in.partake.model.dto.Event;
import in.partake.model.dto.EventTicket;

import java.util.List;

public interface IEventSearchService {
    public void initialize() throws EventSearchServiceException;
    public void cleanUp() throws EventSearchServiceException;


    public void truncate() throws EventSearchServiceException;

    public void create(Event event, List<EventTicket> tickets) throws EventSearchServiceException;
    public void update(Event event, List<EventTicket> tickets) throws EventSearchServiceException;
    public void remove(String eventId) throws EventSearchServiceException;

    public boolean hasIndexed(String eventId) throws EventSearchServiceException;

    public List<String> search(String term, String category, String sortOrder, boolean beforeDeadlineOnly, int offset, int limit) throws EventSearchServiceException;


    public List<String> search(String term, String category, String sortOrder, boolean beforeDeadlineOnly, int maxDocument) throws EventSearchServiceException;
    public List<String> getRecent(int maxDocument) throws EventSearchServiceException;
    public List<String> getRecentByCategory(String category, int maxDocument) throws EventSearchServiceException;
    public List<String> getUpcomingByCategory(String category, int maxDocument) throws EventSearchServiceException;
}
