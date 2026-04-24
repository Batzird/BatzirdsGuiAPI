package com.batzirdbuilds.batzirdsGuiAPI.gui;

import org.bukkit.entity.Player;

/**
 * High-level GUI contract for class-based menu implementations.
 */
public interface GUI {

    /**
     * Open this GUI for the provided player.
     */
    void open(Player player);
}
