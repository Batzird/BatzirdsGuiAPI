package com.batzirdbuilds.batzirdsGuiAPI.gui;

import java.util.Objects;
import org.bukkit.entity.Player;

/**
 * Abstract base class for object-oriented GUI classes (e.g., ShopGUI, SettingsGUI).
 */
public abstract class BaseGUI implements GUI {

    /**
     * Subclasses define layout, items, and actions for a specific viewer.
     */
    protected abstract GuiBuilder createBuilder(Player viewer);

    @Override
    public final void open(final Player player) {
        Objects.requireNonNull(player, "player");
        createBuilder(player).open(player);
    }
}
