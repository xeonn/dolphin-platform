package com.canoo.dolphin.impl.codec;

import com.google.gson.JsonObject;
import org.opendolphin.core.comm.Command;

public interface CommandEncoder<C extends Command> {

    JsonObject encode(C command);

    C decode(JsonObject jsonObject);

}
