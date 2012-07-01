package in.partake.model.dto.auxiliary;

import in.partake.base.JSONable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class EnqueteQuestion implements JSONable {
    private UUID id;
    private String question;
    private EnqueteAnswerType type;
    private List<String> options;

    public EnqueteQuestion(UUID id, String question, EnqueteAnswerType type, List<String> options) {
        this.id = id;
        this.question = question;
        this.type = type;
        if (options != null)
            this.options = new ArrayList<String>(options);
    }

    public EnqueteQuestion(JSONObject obj) {
        this.id = UUID.fromString(obj.getString("id"));
        this.question = obj.optString("question", "");
        this.type = EnqueteAnswerType.safeValueOf(obj.getString("type"));
        this.options = new ArrayList<String>();

        JSONArray array = obj.getJSONArray("options");
        for (int i = 0; i < array.size(); ++i)
            options.add(array.getString(i));
    }

    @Override
    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();
        obj.put("id", id.toString());
        obj.put("question", question);
        obj.put("type", type.toString());

        JSONArray array = new JSONArray();
        for (String str : options)
            array.add(str);
        obj.put("options", array);

        return obj;
    }

    public UUID getId() {
        return id;
    }

    public String getText() {
        return question;
    }

    public EnqueteAnswerType getAnswerType() {
        return type;
    }

    public List<String> getOptions() {
        if (options == null)
            return null;

        return Collections.unmodifiableList(options);
    }
}
