package in.partake.base;

import org.codehaus.jackson.node.ObjectNode;

public interface JSONable {
    public ObjectNode toJSON();
}
