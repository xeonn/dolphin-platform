package org.opendolphin.core.comm;

import java.util.Map;

/**
 * A command that allows the receiving side to read the data without
 * necessarily working with presentation models if that is not appropriate.
 * This is very similar to a REST response.
 */
public class DataCommand extends Command {
    public DataCommand(Map data) {
        this.data = data;
    }

    public DataCommand() {
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    private Map data;
}
