package com.batzirdbuilds.batzirdsGuiAPI.action;

import com.batzirdbuilds.batzirdsGuiAPI.gui.ClickContext;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Use this when one click should execute multiple actions in a fixed order.
 */
public final class CompositeAction implements GuiAction {

    private final List<GuiAction> actions;

    public CompositeAction(@NotNull Collection<? extends GuiAction> actions) {
        this.actions = new ArrayList<>(actions);
    }

    public static @NotNull CompositeAction of(@NotNull GuiAction... actions) {
        return new CompositeAction(Arrays.asList(actions));
    }

    public @NotNull CompositeAction andThen(@NotNull GuiAction action) {
        actions.add(action);
        return this;
    }

    @Override
    public void execute(@NotNull Player player, @NotNull ClickContext clickContext) {
        for (GuiAction action : actions) {
            action.execute(player, clickContext);
        }
    }
}
