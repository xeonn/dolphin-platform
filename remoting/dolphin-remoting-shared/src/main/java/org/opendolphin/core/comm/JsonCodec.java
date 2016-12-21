package org.opendolphin.core.comm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JsonCodec implements Codec {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(JsonCodec.class);

    private final Gson GSON;

    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public JsonCodec() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject element = new JsonObject();
                element.addProperty(Date.class.toString(), new SimpleDateFormat(ISO8601_FORMAT).format(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Float.class, new JsonSerializer<Float>() {
            @Override
            public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject element = new JsonObject();
                element.addProperty(Float.class.toString(), Float.toString(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Float.TYPE, new JsonSerializer<Float>() {
            @Override
            public JsonElement serialize(Float src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject element = new JsonObject();
                element.addProperty(Float.class.toString(), Float.toString(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Double.class, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject element = new JsonObject();
                element.addProperty(Double.class.toString(), Double.toString(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Double.TYPE, new JsonSerializer<Double>() {
            @Override
            public JsonElement serialize(Double src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject element = new JsonObject();
                element.addProperty(Double.class.toString(), Double.toString(src));
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(BigDecimal.class, new JsonSerializer<BigDecimal>() {
            @Override
            public JsonElement serialize(BigDecimal src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject element = new JsonObject();
                element.addProperty(BigDecimal.class.toString(), src.toString());
                return element;
            }
        });
        gsonBuilder.registerTypeAdapter(Integer.class, new JsonDeserializer<Integer>() {
            @Override
            public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                System.out.println("TADA");
                return json.getAsInt();
            }
        });
        gsonBuilder.registerTypeAdapter(Integer.TYPE, new JsonDeserializer<Integer>() {
            @Override
            public Integer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                System.out.println("TADA");
                return json.getAsInt();
            }
        });
        gsonBuilder.registerTypeAdapterFactory(new TypeAdapterFactory() {
            @Override
            public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
                System.out.println(type.getRawType());
                return null;
            }
        });


        GSON = gsonBuilder.serializeNulls().create();
    }



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
