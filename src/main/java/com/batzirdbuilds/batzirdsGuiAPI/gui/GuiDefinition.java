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
        int columns,
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
        if (columns < 1 || columns > 9) {
            throw new IllegalArgumentException("columns must be between 1 and 9");
        }
        slotMap = Map.copyOf(slotMap);
    }

    /**
     * Backing Bukkit chest inventory size. Chest inventories are always 9 columns wide.
     */
    public int size() {
        return rows * 9;
    }

    /**
     * Logical addressable slot count for APIs configured with fewer than 9 columns.
     */
    public int logicalSize() {
        return rows * columns;
    }
}
