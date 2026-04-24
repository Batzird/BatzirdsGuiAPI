package com.batzirdbuilds.batzirdsGuiAPI.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Immutable details for a GUI click callback.
 */
public record ClickContext(
        @NotNull Player player,
        @NotNull ClickType clickType,
        @NotNull InventoryClickEvent event
) {
}
