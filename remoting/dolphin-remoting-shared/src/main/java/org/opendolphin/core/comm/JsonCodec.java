package org.opendolphin.core.comm;

import groovy.json.JsonBuilder;
import groovy.json.JsonSlurper;
import groovy.lang.Closure;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.opendolphin.core.BaseAttribute;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class JsonCodec implements Codec {

    private static final Logger LOG = Logger.getLogger(JsonCodec.class.getName());

    public static final String DATE_TYPE_KEY = Date.class.toString();

    public static final String BIGDECIMAL_TYPE_KEY = BigDecimal.class.toString();

    public static final String FLOAT_TYPE_KEY = Float.class.toString();

    public static final String DOUBLE_TYPE_KEY = Double.class.toString();

    public static final String ISO8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    @Override
    public String encode(List<Command> commands) {
        List<Map> content = DefaultGroovyMethods.collect(commands, new Closure<Map>(this, this) {
            public Map doCall(Command cmd) {
                LOG.finest("encoding command " + cmd);
                final Map entry = DefaultGroovyMethods.getProperties(cmd);
                DefaultGroovyMethods.each(new ArrayList<String>(Arrays.asList("class", "metaClass")), new Closure<Object>(JsonCodec.this, JsonCodec.this) {
                    public Object doCall(String it) {
                        return entry.remove(it);
                    }

                    public Object doCall() {
                        return doCall(null);
                    }

                });
                entry.put("className", cmd.getClass().getName());
                DefaultGroovyMethods.each(entry, new Closure<Object>(JsonCodec.this, JsonCodec.this) {
                    public Object doCall(Object key, Object value) {// prepare against invalid entries
                        if (value instanceof List) {// some commands may have collective values
                            for (Object val : (List)value) {
                                Map<Object, Object> entryMap = (Map) val;
                                for (Map.Entry mapEntry : entryMap.entrySet()) {
                                    entryMap.put(mapEntry.getKey(), encodeBaseValue(mapEntry.getValue()));
                                }

                            }

                        } else if (value instanceof Map) {// DataCommand has map content
                            Map<Object, Object> entryMap = (Map) value;
                            for (Map.Entry mapEntry : entryMap.entrySet()) {
                                ((Map) value).put(mapEntry.getKey(), encodeBaseValue(mapEntry.getValue()));
                            }

                        } else {
                            return entry.put(key, encodeBaseValue(value));
                        }
                        return null;
                    }
                });
                return entry;
            }

        });
        JsonBuilder builder = new JsonBuilder(content);
        return builder.toString();
    }

    protected Object encodeBaseValue(Object entryValue) {
        Object result = BaseAttribute.checkValue(entryValue);
        if (result instanceof Date) {
            Map map = new HashMap();
            map.put(DATE_TYPE_KEY, new SimpleDateFormat(ISO8601_FORMAT).format((Date) result));
            result = map;
        } else if (result instanceof BigDecimal) {
            Map map = new HashMap();
            map.put(BIGDECIMAL_TYPE_KEY, ((BigDecimal) result).toString());
            result = map;
        } else if (result instanceof Float) {
            Map map = new HashMap();
            map.put(FLOAT_TYPE_KEY, Float.toString((float) result));
            result = map;
        } else if (result instanceof Double) {
            Map map = new HashMap();
            map.put(DOUBLE_TYPE_KEY, Double.toString((double) result));
            result = map;
        }

        return result;
    }

    @Override
    public List<Command> decode(String transmitted) {
        final List<Command> result = new LinkedList<Command>();
        Object got = new JsonSlurper().parseText(transmitted);

        final String validPackagePrefix = Command.class.getPackage().getName();

        if(got instanceof List) {
            for(Object listEntry : (List)got) {
                if(listEntry instanceof Map) {
                    Map cmd = (Map) listEntry;
                    if(cmd.get("className") != null && cmd.get("className").toString().startsWith(validPackagePrefix)) {
                        try {
                            final Command responseCommand = (Command) Class.forName((String) cmd.get("className")).newInstance();
                            DefaultGroovyMethods.each(cmd, new Closure<Object>(JsonCodec.this, JsonCodec.this) {
                                public Object doCall(Map.Entry cmdEntry) {
                                    Object key = cmdEntry.getKey();
                                    Object value = cmdEntry.getValue();
                                    if (key.equals("className")) {
                                        return null;
                                    }

                                    if (key.equals("id")) {// id is only set for NamedCommand and SignalCommand others are dynamic
                                        if (responseCommand instanceof SignalCommand) {
                                            ((SignalCommand) responseCommand).setId((String) value);
                                        }

                                        if (responseCommand instanceof NamedCommand) {
                                            ((NamedCommand) responseCommand).setId((String) value);
                                        }
                                        return null;
                                    }

                                    if (value instanceof List) {// some commands may have collective values
                                        for (final Map entryMap : (List<Map>) value) {
                                            DefaultGroovyMethods.each(entryMap, new Closure<Object>(JsonCodec.this, JsonCodec.this) {
                                                public Object doCall(Object entryKey, Object entryValue) {
                                                    return putAt0(entryMap, entryKey, decodeBaseValue(entryValue));
                                                }
                                            });
                                        }
                                    } else {
                                        value = decodeBaseValue(value);
                                    }

                                    DefaultGroovyMethods.putAt(responseCommand, (String) key, value);
                                    return value;
                                }

                            });
                            LOG.finest("decoded command " + responseCommand);
                            result.add(responseCommand);
                        } catch (Exception e) {
                            throw new RuntimeException("Can not decode!", e);
                        }
                    }
                }
            }
        }

        return result;
    }

    public Object decodeBaseValue(Object encodedValue) {
        try {
            Object result = encodedValue;
            if (encodedValue instanceof Map && ((Map) encodedValue).size() == 1) {
                if (((Map) encodedValue).containsKey(DATE_TYPE_KEY)) {
                    result = new SimpleDateFormat(ISO8601_FORMAT).parse((String) ((Map) encodedValue).get(DATE_TYPE_KEY));
                } else if (((Map) encodedValue).containsKey(BIGDECIMAL_TYPE_KEY)) {
                    result = new BigDecimal((String) ((Map) encodedValue).get(BIGDECIMAL_TYPE_KEY));
                } else if (((Map) encodedValue).containsKey(FLOAT_TYPE_KEY)) {
                    result = Float.parseFloat((String) ((Map) encodedValue).get(FLOAT_TYPE_KEY));
                } else if (((Map) encodedValue).containsKey(DOUBLE_TYPE_KEY)) {
                    result = Double.parseDouble((String) ((Map) encodedValue).get(DOUBLE_TYPE_KEY));
                }

            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Can not decode!", e);
        }
    }

    private static <K, V, Value extends V> Value putAt0(Map<K, V> propOwner, K key, Value value) {
        propOwner.put(key, value);
        return value;
    }

}
