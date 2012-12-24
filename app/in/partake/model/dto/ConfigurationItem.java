package in.partake.model.dto;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

public class ConfigurationItem extends PartakeModel<ConfigurationItem> {
    private String key;
    private String value;

    public ConfigurationItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String getPrimaryKey() {
        return key;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put(key, value);
        return obj;
    }

    public String key() {
        return key;
    }

    public String value() {
        return value;
    }

}
