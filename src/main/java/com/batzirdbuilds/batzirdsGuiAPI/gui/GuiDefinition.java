package com.batzirdbuilds.batzirdsGuiAPI.gui;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable GUI metadata generated from the builder.
 */
public record GuiDefinition(
        String id,
        String title,
        int rows,
        Map<Integer, GuiItem> slotMap,
        GuiOptions options
) {

    public GuiDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(title, "title");
        Objects.requireNonNull(slotMap, "slotMap");
        Objects.requireNonNull(options, "options");
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("rows must be between 1 and 6");
        }
        slotMap = Map.copyOf(slotMap);
    }

    public int size() {
        return rows * 9;
    }
}
