package in.partake.service;

import in.partake.base.KeyValuePair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EventSortOrder {
    private static final List<KeyValuePair> SORTORDERS = Collections.unmodifiableList(Arrays.asList(
            new KeyValuePair("score", "マッチ度順"),
            new KeyValuePair("createdAt", "新着順"),
            new KeyValuePair("deadline", "締め切りの早い順"),
            new KeyValuePair("deadline-r", "締め切りの遅い順 "),
            new KeyValuePair("beginDate", "開始日時の早い順"),
            new KeyValuePair("beginDate-r", "開始日時の遅い順")
    ));

    public static List<KeyValuePair> getSortOrders() {
        return SORTORDERS;
    }
}
