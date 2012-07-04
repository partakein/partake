package play.test;

import in.partake.base.Pair;

import java.util.ArrayList;
import java.util.List;

import play.libs.Scala;
import scala.Tuple2;

/**
 * This class supports FakeRequest operations in play framework.
 * Since FakeRequest seems buggy, we use this class for workaround.
 * @author shinyak
 *
 */
public class FakeRequestOperation {

    @SuppressWarnings(value = "unchecked")
    public static void addSession(FakeRequest request, List<Pair<String, String>> sessions) {
        List<Tuple2<String, String>> newSessions = new ArrayList<Tuple2<String,String>>();
        for (Pair<String, String> pair: sessions)
            newSessions.add(Scala.Tuple(pair.getFirst(), pair.getSecond()));

        request.fake = request.fake.withSession(Scala.varargs(newSessions.toArray()));
    }
}
