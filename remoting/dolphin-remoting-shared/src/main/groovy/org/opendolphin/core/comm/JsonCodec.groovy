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
package org.opendolphin.core.comm

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.opendolphin.core.BaseAttribute
import org.opendolphin.core.Tag

import java.text.SimpleDateFormat
import java.util.logging.Logger

class JsonCodec implements Codec {

    private static final Logger LOG = Logger.getLogger(JsonCodec.class.getName());

    public static final String DATE_TYPE_KEY = Date.toString();

    public static final String BIGDECIMAL_TYPE_KEY = BigDecimal.toString();

    public static final String FLOAT_TYPE_KEY = Float.toString();

    public static final String DOUBLE_TYPE_KEY = Double.toString();

    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ"

    @Override
    public String encode(List<Command> commands) {
        def content = commands.collect { Command cmd ->
            LOG.finest("encoding command " + cmd);
            Map entry = cmd.properties;
            ['class', 'metaClass'].each { entry.remove it }
            entry.put("className", cmd.getClass().getName());
            entry.each { key, value ->              // prepare against invalid entries
                if (value instanceof List) {        // some commands may have collective values
                    for (Map entryMap in value) {
                        for (Map.Entry mapEntry : entryMap) {
                            entryMap.put(mapEntry.getKey(), encodeBaseValue(mapEntry.getValue()));
                        }
                    }
                } else if (value instanceof Map) {  // DataCommand has map content
                    for (Map.Entry mapEntry : value) {
                        ((Map) value).put(mapEntry.getKey(), encodeBaseValue(mapEntry.getValue()));
                    }
                } else {
                    entry.put(key, encodeBaseValue(value));
                }
            }
            entry
        }
        JsonBuilder builder = new JsonBuilder(content);
        return builder.toString()
    }

    protected Object encodeBaseValue(entryValue) {
        def result = BaseAttribute.checkValue(entryValue);
        if (result instanceof Date) {
            Map map = new HashMap();
            map.put(DATE_TYPE_KEY, new SimpleDateFormat(ISO8601_FORMAT).format(result));
            result = map;
        } else if (result instanceof BigDecimal) {
            Map map = new HashMap();
            map.put(BIGDECIMAL_TYPE_KEY, result.toString());
            result = map;
        } else if (result instanceof Float) {
            Map map = new HashMap();
            map.put(FLOAT_TYPE_KEY, Float.toString(result));
            result = map;
        } else if (result instanceof Double) {
            Map map = new HashMap();
            map.put(DOUBLE_TYPE_KEY, Double.toString(result));
            result = map;
        }
        return result;
    }

    @Override
    public List<Command> decode(String transmitted) {
        List<Command> result = new LinkedList<>();
        Object got = new JsonSlurper().parseText(transmitted);

        String validPackagePrefix = Command.class.getPackage().getName();

        got.findAll { cmd ->
            cmd['className'] != null && String.class.cast(cmd['className']).startsWith(validPackagePrefix);
        }.each { cmd ->
            Command responseCommand = Class.forName(cmd['className']).newInstance();
            cmd.each { key, value ->
                if (key == 'className') {
                    return;
                }
                if (key == 'id') { // id is only set for NamedCommand and SignalCommand others are dynamic
                    if (responseCommand instanceof SignalCommand) {
                        ((SignalCommand) responseCommand).setId(value);
                    }
                    if (responseCommand in NamedCommand) {
                        ((NamedCommand) responseCommand).setId(value);
                    }
                    return;
                }
                if (key == 'tag') {
                    value = Tag.tagFor(value);
                } else if (value instanceof List) {        // some commands may have collective values
                    for (Map entryMap : value) {
                        entryMap.each { entryKey, entryValue ->
                            entryMap[entryKey] = decodeBaseValue(entryValue);
                        }
                    }
                } else {
                    value = decodeBaseValue(value);
                }
                responseCommand[key] = value;
            }
            LOG.finest("decoded command " + responseCommand);
            result.add(responseCommand);
        }
        return result;
    }

    public Object decodeBaseValue(Object encodedValue) {
        Object result = encodedValue;
        if (encodedValue instanceof Map && encodedValue.size() == 1) {
            if (encodedValue.containsKey(DATE_TYPE_KEY)) {
                result = new SimpleDateFormat(ISO8601_FORMAT).parse(encodedValue.get(DATE_TYPE_KEY));
            } else if (encodedValue.containsKey(BIGDECIMAL_TYPE_KEY)) {
                result = new BigDecimal(encodedValue.get(BIGDECIMAL_TYPE_KEY));
            } else if (encodedValue.containsKey(FLOAT_TYPE_KEY)) {
                result = Float.parseFloat(encodedValue.get(FLOAT_TYPE_KEY));
            } else if (encodedValue.containsKey(DOUBLE_TYPE_KEY)) {
                result = Double.parseDouble(encodedValue.get(DOUBLE_TYPE_KEY));
            }
        }
        return result;
    }
}
