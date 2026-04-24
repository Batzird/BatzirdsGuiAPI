package com.batzirdbuilds.batzirdsGuiAPI.action;

import com.batzirdbuilds.batzirdsGuiAPI.gui.ClickContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Use this when a click should force the player to send a chat message.
 */
public final class ChatAction implements GuiAction {

    private final String message;

    public ChatAction(@NotNull String message) {
        this.message = message;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull ClickContext clickContext) {
        player.chat(message
                .replace("{player}", clickContext.player().getName())
                .replace("{player_name}", clickContext.player().getName()));
    }
}
