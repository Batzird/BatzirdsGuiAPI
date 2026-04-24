package com.batzirdbuilds.batzirdsGuiAPI.action;

import com.batzirdbuilds.batzirdsGuiAPI.gui.ClickContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * Use this when a click should run a command as either the server console or the player.
 */
public final class CommandAction implements GuiAction {

    public enum DispatchType {
        SERVER,
        PLAYER
    }

    private final DispatchType dispatchType;
    private final String command;

    public CommandAction(@NotNull DispatchType dispatchType, @NotNull String command) {
        this.dispatchType = dispatchType;
        this.command = sanitize(command);
    }

    @Override
    public void execute(@NotNull Player player, @NotNull ClickContext clickContext) {
        String resolved = applyPlaceholders(command, clickContext);
        CommandSender sender = dispatchType == DispatchType.SERVER ? Bukkit.getConsoleSender() : player;
        Bukkit.dispatchCommand(sender, resolved);
    }

    public static @NotNull CommandAction server(@NotNull String command) {
        return new CommandAction(DispatchType.SERVER, command);
    }

    public static @NotNull CommandAction player(@NotNull String command) {
        return new CommandAction(DispatchType.PLAYER, command);
    }

    private static @NotNull String sanitize(@NotNull String command) {
        return command.startsWith("/") ? command.substring(1) : command;
    }

    private static @NotNull String applyPlaceholders(@NotNull String input, @NotNull ClickContext context) {
        Player player = context.player();
        return input
                .replace("{player}", player.getName())
                .replace("{player_name}", player.getName())
                .replace("{player_uuid}", player.getUniqueId().toString())
                .replace("%player%", player.getName())
                .replace("%player_name%", player.getName())
                .replace("%player_uuid%", player.getUniqueId().toString())
                .replace("{click_type}", context.clickType().name().toLowerCase(Locale.ROOT));
    }
}
