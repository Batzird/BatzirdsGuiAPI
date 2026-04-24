package com.batzirdbuilds.batzirdsGuiAPI.gui;

import com.batzirdbuilds.batzirdsGuiAPI.action.ChatAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.CommandAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.CompositeAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.FunctionAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.GuiAction;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class GuiItem {

    private final Map<ClickType, GuiAction> clickActions = new EnumMap<>(ClickType.class);
    private GuiAction defaultAction;

    public @NotNull GuiItem onLeftClickCommand(@NotNull String command) {
        return onClick(ClickType.LEFT, CommandAction.server(command));
    }

    public @NotNull GuiItem onClickChat(@NotNull String text) {
        return onClick(new ChatAction(text));
    }

    public @NotNull GuiItem onClick(@NotNull Consumer<ClickContext> consumer) {
        return onClick(new FunctionAction(consumer));
    }

    public @NotNull GuiItem onClick(@NotNull GuiAction action) {
        if (defaultAction == null) {
            defaultAction = action;
            return this;
        }

        if (defaultAction instanceof CompositeAction compositeAction) {
            compositeAction.andThen(action);
            return this;
        }

        defaultAction = CompositeAction.of(defaultAction, action);
        return this;
    }

    public @NotNull GuiItem onClick(@NotNull ClickType clickType, @NotNull GuiAction action) {
        GuiAction existing = clickActions.get(clickType);
        if (existing == null) {
            clickActions.put(clickType, action);
            return this;
        }

        if (existing instanceof CompositeAction compositeAction) {
            compositeAction.andThen(action);
            return this;
        }

        clickActions.put(clickType, CompositeAction.of(existing, action));
        return this;
    }

    public void trigger(@NotNull Player player, @NotNull ClickContext context) {
        Optional.ofNullable(clickActions.get(context.clickType())).ifPresent(action -> action.execute(player, context));
        Optional.ofNullable(defaultAction).ifPresent(action -> action.execute(player, context));
    }
}
