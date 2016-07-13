package org.opendolphin;

import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class ShortFormatter extends SimpleFormatter {

    public synchronized String format(LogRecord record) {
        return "[" + record.getLevel() + "] " + record.getMessage() + "\n";
    }

}
