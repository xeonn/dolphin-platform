package com.canoo.dolphin.server.mbean.beans;

import com.canoo.dolphin.collections.ObservableList;
import com.canoo.dolphin.impl.ReflectionHelper;
import com.canoo.dolphin.mapping.Property;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Field;

/**
 * Created by hendrikebbers on 15.03.16.
 */
public class ModelJsonSerializer {

    public static JsonObject toJson(Object dolphinModel) {
        if(dolphinModel == null) {
            return null;
        }

        JsonObject jsonObject = new JsonObject();

        for(Field field : ReflectionHelper.getInheritedDeclaredFields(dolphinModel.getClass())) {
            if(ReflectionHelper.isProperty(field.getType())) {
                Property property = (Property) ReflectionHelper.getPrivileged(field, dolphinModel);
                Object value = property.get();
                if(value == null) {
                    jsonObject.add(field.getName(), null);
                } else if(Number.class.isAssignableFrom(value.getClass()) || Double.TYPE.isAssignableFrom(value.getClass()) || Float.TYPE.isAssignableFrom(value.getClass()) || Long.TYPE.isAssignableFrom(value.getClass()) || Integer.TYPE.isAssignableFrom(value.getClass())) {
                    jsonObject.add(field.getName(), new JsonPrimitive((Number) value));
                } else if(String.class.isAssignableFrom(value.getClass())) {
                    jsonObject.add(field.getName(), new JsonPrimitive((String) value));
                } else if(Boolean.class.isAssignableFrom(value.getClass()) || Boolean.TYPE.isAssignableFrom(value.getClass())) {
                    jsonObject.add(field.getName(), new JsonPrimitive((Boolean) value));
                } else {
                    jsonObject.add(field.getName(), toJson(value));
                }
            } else if(ReflectionHelper.isObservableList(field.getType())) {
                ObservableList list = (ObservableList) ReflectionHelper.getPrivileged(field, dolphinModel);
                JsonArray jsonArray = new JsonArray();
                for(Object value : list) {
                    if(value == null) {
                        //TODO
                        //jsonArray.add(null);
                    } else if(Number.class.isAssignableFrom(value.getClass()) || Double.TYPE.isAssignableFrom(value.getClass()) || Float.TYPE.isAssignableFrom(value.getClass()) || Long.TYPE.isAssignableFrom(value.getClass()) || Integer.TYPE.isAssignableFrom(value.getClass())) {
                        jsonArray.add(new JsonPrimitive((Number) value));
                    } else if(String.class.isAssignableFrom(value.getClass())) {
                        jsonArray.add(new JsonPrimitive((String) value));
                    } else if(Boolean.class.isAssignableFrom(value.getClass()) || Boolean.TYPE.isAssignableFrom(value.getClass())) {
                        jsonArray.add(new JsonPrimitive((Boolean) value));
                    } else {
                        jsonArray.add(toJson(value));
                    }
                }
                jsonObject.add(field.getName(), jsonArray);
            }

        }
        return jsonObject;
    }
}
