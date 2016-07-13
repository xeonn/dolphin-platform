package org.opendolphin.core.comm;

import java.util.List;

public interface Codec {

   String encode(List<Command> commands);

   List<Command> decode(String transmitted);
    
}
