package com.canoo.dolphin.impl.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.opendolphin.core.comm.ValueChangedCommand;

public class ValueChangedCommandEncoder implements CommandEncoder<ValueChangedCommand> {

    @Override
    public JsonObject encode(ValueChangedCommand command) {
        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty("a", command.getAttributeId());
        if (command.getOldValue() != null) {
            jsonCommand.add("o", encodeValue(command.getOldValue()));
        }
        if (command.getNewValue() != null) {
            jsonCommand.add("n", encodeValue(command.getNewValue()));
        }
        jsonCommand.addProperty("id", "ValueChanged");
        return jsonCommand;
    }

    private static JsonElement encodeValue(Object value) {
        if (value instanceof String) {
            return new JsonPrimitive((String) value);
        }
        if (value instanceof Number) {
            return new JsonPrimitive((Number) value);
        }
        if (value instanceof Boolean) {
            return new JsonPrimitive((Boolean) value);
        }
        throw new IllegalStateException("Only String, Number, and Boolean are allowed currently");
    }

    @Override
    public ValueChangedCommand decode(JsonObject jsonObject) {
        try {
            final ValueChangedCommand command = new ValueChangedCommand();

            command.setNewValue(decodeValue(jsonObject.get("n")));
            command.setOldValue(decodeValue(jsonObject.get("o")));
            command.setAttributeId(jsonObject.get("a").getAsString());

            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }    }

    private static Object decodeValue(JsonElement jsonElement) {
        if (jsonElement == null || jsonElement.isJsonNull()) {
            return null;
        }
        if (! jsonElement.isJsonPrimitive()) {
            throw new JsonParseException("Illegal JSON detected");
        }
        final JsonPrimitive value = (JsonPrimitive) jsonElement;

        if (value.isString()) {
            return value.getAsString();
        } else if (value.isNumber()) {
            return value.getAsNumber();
        }
        return value.getAsBoolean();
    }
}
