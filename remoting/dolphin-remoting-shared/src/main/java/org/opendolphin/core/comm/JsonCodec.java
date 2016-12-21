package org.opendolphin.core.comm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonCodec implements Codec {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JsonCodec.class);

    private static final Gson GSON = new Gson();

    @Override
    public String encode(List<Command> commands) {
        JsonArray ret = new JsonArray();

        for (final Command command : commands) {
            if (command == null) {
                throw new IllegalArgumentException("Command list contains a null command: " + command);
            } else {
                LOG.trace("Encoding command of type {}", command.getClass());
                JsonElement element = GSON.toJsonTree(command);
                element.getAsJsonObject().addProperty("className", command.getClass().getName());
                ret.add(element);
            }
        }
        return GSON.toJson(ret);
    }

    @Override
    public List<Command> decode(String transmitted) {
        System.out.println(transmitted);
        LOG.trace("Decoding message: {}", transmitted);
        try {
            final List<Command> commands = new ArrayList<>();
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);

            for (final JsonElement jsonElement : array) {
                final JsonObject command = (JsonObject) jsonElement;
                final String className = command.getAsJsonPrimitive("className").getAsString();
                LOG.trace("Decoding command type: {}", className);
                Class<? extends Command> commandClass = (Class<? extends Command>) Class.forName(className);
                commands.add(GSON.fromJson(jsonElement, commandClass));
            }
            LOG.trace("Decoded command list with {} commands", commands.size());
            return commands;
        } catch (Exception ex) {
            throw new JsonParseException("Illegal JSON detected", ex);
        }
    }
}
