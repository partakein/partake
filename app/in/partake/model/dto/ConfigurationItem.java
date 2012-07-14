package in.partake.model.dto;

import net.sf.json.JSONObject;

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
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
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
