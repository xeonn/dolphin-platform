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

import static com.canoo.dolphin.impl.codec.CommandConstants.*;

public class  CreatePresentationModelEncoder implements CommandEncoder<CreatePresentationModelCommand> {

    @Override
    public JsonObject encode(CreatePresentationModelCommand command) {
        Assert.requireNonNull(command, "command");

        final JsonObject jsonCommand = new JsonObject();
        jsonCommand.addProperty(PM_ID, command.getPmId());
        jsonCommand.addProperty(PM_TYPE, command.getPmType());

        final JsonArray jsonArray = new JsonArray();
        for (final Map<String, Object> attribute : command.getAttributes()) {
            final JsonObject jsonAttribute = new JsonObject();
            jsonAttribute.addProperty(ATTRIBUTE_NAME, String.valueOf(attribute.get("propertyName")));
            jsonAttribute.addProperty(ATTRIBUTE_ID, String.valueOf(attribute.get("id")));
            final Object value = attribute.get("value");
            if (value != null) {
                jsonAttribute.addProperty(ATTRIBUTE_VALUE, String.valueOf(attribute.get("value")));
            }
            final Object tag = attribute.get("tag");
            if (tag != null && !Tag.VALUE.getName().equals(tag)) {
                jsonAttribute.addProperty(ATTRIBUTE_TAG, tag.toString());
            }
            jsonArray.add(jsonAttribute);
        }
        jsonCommand.add(PM_ATTRIBUTES, jsonArray);
        jsonCommand.addProperty(ID, "CreatePresentationModel");

        return jsonCommand;
    }

    @Override
    public CreatePresentationModelCommand decode(JsonObject jsonObject) {
        Assert.requireNonNull(jsonObject, "jsonObject");

        try {
            final CreatePresentationModelCommand command = new CreatePresentationModelCommand();

            command.setPmId(jsonObject.getAsJsonPrimitive(PM_ID).getAsString());
            command.setPmType(jsonObject.getAsJsonPrimitive(PM_TYPE).getAsString());
            command.setClientSideOnly(false);

            final JsonArray jsonArray = jsonObject.getAsJsonArray(PM_ATTRIBUTES);
            final List<Map<String, Object>> attributes = new ArrayList<>();
            for (final JsonElement jsonElement : jsonArray) {
                final JsonObject attribute = jsonElement.getAsJsonObject();
                final HashMap<String, Object> map = new HashMap<>();
                map.put("propertyName", attribute.getAsJsonPrimitive(ATTRIBUTE_NAME).getAsString());
                map.put("id", attribute.getAsJsonPrimitive(ATTRIBUTE_ID).getAsString());
                final String value = attribute.has(ATTRIBUTE_VALUE)? attribute.getAsJsonPrimitive(ATTRIBUTE_VALUE).getAsString() : null;
                map.put("value", value);
                map.put("baseValue", value);
                map.put("qualifier", null);
                map.put("tag", attribute.has(ATTRIBUTE_TAG)? Tag.tagFor.get(attribute.getAsJsonPrimitive(ATTRIBUTE_TAG).getAsString()) : Tag.VALUE);
                attributes.add(map);
            }
            command.setAttributes(attributes);

            return command;
        } catch (IllegalStateException | ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
