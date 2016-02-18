package com.canoo.dolphin.impl.codec;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.opendolphin.core.Tag;
import org.opendolphin.core.comm.CreatePresentationModelCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class  CreatePresentationModelEncoder implements CommandEncoder<CreatePresentationModelCommand> {

    @Override
    public JsonObject encode(CreatePresentationModelCommand command) {
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty("p", command.getPmId());
        jsonCommand.addProperty("t", command.getPmType());

        final JsonArray jsonArray = new JsonArray();
        for (final Map<String, Object> attribute : command.getAttributes()) {
            final JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty("n", String.valueOf(attribute.get("propertyName")));
            jsonAttribute.addProperty("i", String.valueOf(attribute.get("id")));
            final Object value = attribute.get("value");
            if (value != null) {
                jsonAttribute.addProperty("v", String.valueOf(attribute.get("value")));
            }
            jsonArray.add(jsonAttribute);
        }
        jsonCommand.add("a", jsonArray);

        return jsonCommand;
    }

    @Override
    public CreatePresentationModelCommand decode(JsonObject jsonObject) {
        try {
            final CreatePresentationModelCommand command = new CreatePresentationModelCommand();

            command.setPmId(jsonObject.getAsJsonPrimitive("p").getAsString());
            command.setPmType(jsonObject.getAsJsonPrimitive("t").getAsString());
            command.setClientSideOnly(false);

            final JsonArray jsonArray = jsonObject.getAsJsonArray("a");
            final List<Map<String, Object>> attributes = new ArrayList<>();
            for (final JsonElement jsonElement : jsonArray) {
                final JsonObject attribute = jsonElement.getAsJsonObject();
                final HashMap<String, Object> map = new HashMap<>();
                map.put("propertyName", attribute.getAsJsonPrimitive("n").getAsString());
                map.put("id", attribute.getAsJsonPrimitive("i").getAsString());
                final String value = attribute.has("v")? attribute.getAsJsonPrimitive("v").getAsString() : null;
                map.put("value", value);
                map.put("baseValue", value);
                map.put("qualifier", null);
                map.put("tag", Tag.VALUE);
                attributes.add(map);
            }
            command.setAttributes(attributes);

            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
