package com.batzirdbuilds.batzirdsGuiAPI.gui;

import com.batzirdbuilds.batzirdsGuiAPI.action.ChatAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.CommandAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.GuiAction;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class GuiBuilder {

    private final GuiItem guiItem;

    private GuiBuilder(@NotNull GuiItem guiItem) {
        this.guiItem = guiItem;
    }

    public static @NotNull GuiBuilder item() {
        return new GuiBuilder(new GuiItem());
    }

    public @NotNull GuiBuilder onLeftClickCommand(@NotNull String command) {
        guiItem.onLeftClickCommand(command);
        return this;
    }

    public @NotNull GuiBuilder onClickChat(@NotNull String text) {
        guiItem.onClickChat(text);
        return this;
    }

    public @NotNull GuiBuilder onClick(@NotNull Consumer<ClickContext> consumer) {
        guiItem.onClick(consumer);
        return this;
    }

    public @NotNull GuiBuilder onClick(@NotNull GuiAction action) {
        guiItem.onClick(action);
        return this;
    }

    public @NotNull GuiBuilder onClick(@NotNull ClickType clickType, @NotNull GuiAction action) {
        guiItem.onClick(clickType, action);
        return this;
    }

    public @NotNull GuiBuilder onClickPlayerCommand(@NotNull String command) {
        guiItem.onClick(new CommandAction(CommandAction.DispatchType.PLAYER, command));
        return this;
    }

    public @NotNull GuiBuilder onClickServerCommand(@NotNull String command) {
        guiItem.onClick(new CommandAction(CommandAction.DispatchType.SERVER, command));
        return this;
    }

    public @NotNull GuiBuilder onLeftClickChat(@NotNull String text) {
        guiItem.onClick(ClickType.LEFT, new ChatAction(text));
        return this;
    }

    public @NotNull GuiItem build() {
        return guiItem;
    }
}
