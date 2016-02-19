/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.impl.codec;

import com.canoo.dolphin.util.Assert;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.opendolphin.core.comm.ValueChangedCommand;

public class ValueChangedCommandEncoder implements CommandEncoder<ValueChangedCommand> {

    @Override
    public JsonObject encode(ValueChangedCommand command) {
        Assert.requireNonNull(command, "command");

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
        Assert.requireNonNull(jsonObject, "jsonObject");

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
        } else if (value.isBoolean()) {
            return value.getAsBoolean();
        } else if (value.isNumber()) {
            try {
                final double d = value.getAsDouble();
                if (d - Math.floor(d) > 1e-6) {
                    return d;
                }
                final long l = value.getAsLong();
                if (l > (long) Integer.MAX_VALUE) {
                    return l;
                }
                return value.getAsInt();
            } catch (NumberFormatException ex) {
                throw new JsonParseException("Illegal JSON detected");
            }
        }
        throw new JsonParseException("Currently only String, Boolean, or Number are allowed as primitives");
    }
}
