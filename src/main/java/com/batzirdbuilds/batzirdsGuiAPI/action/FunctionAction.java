package com.batzirdbuilds.batzirdsGuiAPI.action;

import com.batzirdbuilds.batzirdsGuiAPI.gui.ClickContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/**
 * Use this when click behavior is custom Java logic instead of a command/chat string.
 */
public final class FunctionAction implements GuiAction {

    @FunctionalInterface
    public interface PlayerClickFunction {
        void execute(@NotNull Player player, @NotNull ClickContext context);
    }

    private final PlayerClickFunction callback;

    public FunctionAction(@NotNull Consumer<ClickContext> callback) {
        this.callback = (player, context) -> callback.accept(context);
    }

    public FunctionAction(@NotNull PlayerClickFunction callback) {
        this.callback = callback;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull ClickContext clickContext) {
        callback.execute(player, clickContext);
    }
}
