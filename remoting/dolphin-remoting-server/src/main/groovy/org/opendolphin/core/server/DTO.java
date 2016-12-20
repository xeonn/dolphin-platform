package org.opendolphin.core.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A DTO is a <a href="http://en.wikipedia.org/wiki/Data_transfer_object">data transfer object</a>, used to
 * transfer the contents of a presentation model in a single Dolphin data command.
 * <p>
 * A DTO consists of a list of Slot objects, where each slot corresponds to an attribute in the presentation model.
 * A slot consists of a propertyName (String), a value, a baseValue, a qualifier, and a tag.
 *
 * @see Slot
 */
public final class DTO {

    private List<Slot> slots;

    public DTO(List<Slot> newSlots) {
        slots = newSlots;
    }

    public DTO(Slot... newSlots) {
        this(Arrays.asList(newSlots));
    }

    /**
     * Create the representation that is used within commands.
     */
    public List<Map<String, Object>> encodable() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Slot slot : slots) {
            list.add(slot.toMap());
        }

        return list;
    }

    public List<Slot> getSlots() {
        return slots;
    }
}
