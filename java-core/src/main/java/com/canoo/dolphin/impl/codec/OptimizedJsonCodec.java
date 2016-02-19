package com.canoo.dolphin.impl.codec;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import org.opendolphin.core.comm.Codec;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.comm.JsonCodec;
import org.opendolphin.core.comm.ValueChangedCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptimizedJsonCodec implements Codec {

    private static final Gson GSON = new Gson();

    private static final Map<Class<? extends Command>, CommandEncoder<?>> ENCODERS = new HashMap<>();
    private static final Map<String, CommandEncoder<?>> DECODERS = new HashMap<>();
    static {
        final CreatePresentationModelEncoder createPresentationModelEncoder = new CreatePresentationModelEncoder();
        ENCODERS.put(CreatePresentationModelCommand.class, createPresentationModelEncoder);
        DECODERS.put("CreatePresentationModel", createPresentationModelEncoder);

        final ValueChangedCommandEncoder valueChangedCommandEncoder = new ValueChangedCommandEncoder();
        ENCODERS.put(ValueChangedCommand.class, valueChangedCommandEncoder);
        DECODERS.put("ValueChanged", valueChangedCommandEncoder);
    }

    private final Codec fallBack = new JsonCodec();

    @Override
    @SuppressWarnings("unchecked")
    public String encode(List<Command> commands) {
        final StringBuilder builder = new StringBuilder("[");
        for (final Command command : commands) {
            final CommandEncoder encoder = ENCODERS.get(command.getClass());
            if (encoder != null) {
                final JsonObject jsonObject = encoder.encode(command);
                GSON.toJson(jsonObject, builder);
            } else {
                final String result = fallBack.encode(Collections.singletonList(command));
                builder.append(result.substring(1, result.length()-1));
            }
            builder.append(",");
        }
        if (! commands.isEmpty()) {
            final int length = builder.length();
            builder.delete(length-1, length);
        }
        builder.append("]");
        return builder.toString();
    }

    @Override
    public List<Command> decode(String transmitted) {
        try {
            final List<Command> commands = new ArrayList<>();
            final JsonArray array = (JsonArray) new JsonParser().parse(transmitted);

            for (final JsonElement jsonElement : array) {
                final JsonObject command = (JsonObject) jsonElement;
                final String id = command.getAsJsonPrimitive("id").getAsString();
                final CommandEncoder<?> encoder = DECODERS.get(id);
                if (encoder != null) {
                    commands.add(encoder.decode(command));
                } else {
                    commands.addAll(fallBack.decode("[" + command.toString() + "]"));
                }
            }
            return commands;
        } catch (ClassCastException | NullPointerException ex) {
            throw new JsonParseException("Illegal JSON detected");
        }
    }

}
