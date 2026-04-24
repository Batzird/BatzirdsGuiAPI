package com.batzirdbuilds.batzirdsGuiAPI.action;

import com.batzirdbuilds.batzirdsGuiAPI.gui.ClickContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface GuiAction {

    void execute(@NotNull Player player, @NotNull ClickContext clickContext);
}
