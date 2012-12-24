package in.partake.base;

import org.codehaus.jackson.node.ObjectNode;

public interface SafeJSONable {
    public ObjectNode toSafeJSON();
}
