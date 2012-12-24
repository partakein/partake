package in.partake.model.dto.auxiliary;

import in.partake.base.JSONable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

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

    public EnqueteQuestion(JsonNode obj) {
        this.id = UUID.fromString(obj.get("id").asText());
        this.question = obj.path("question").asText();
        this.type = EnqueteAnswerType.safeValueOf(obj.get("type").asText());
        this.options = new ArrayList<String>();

        JsonNode array = obj.get("options");
        for (int i = 0; i < array.size(); ++i)
            options.add(array.get(i).asText());
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode obj = new ObjectNode(JsonNodeFactory.instance);
        obj.put("id", id.toString());
        obj.put("question", question);
        obj.put("type", type.toString());

        ArrayNode array = obj.putArray("options");
        for (String str : options)
            array.add(str);

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
