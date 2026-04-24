package com.batzirdbuilds.batzirdsGuiAPI.gui;

import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Immutable slot item plus metadata describing which click behavior it should bind to.
 */
public final class GuiItem {

    private final ItemStack itemStack;
    private final String actionKey;
    private final boolean cancelClick;
    private final boolean closeOnAction;

    public GuiItem(ItemStack itemStack, String actionKey, boolean cancelClick, boolean closeOnAction) {
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack").clone();
        this.actionKey = Objects.requireNonNull(actionKey, "actionKey");
        this.cancelClick = cancelClick;
        this.closeOnAction = closeOnAction;
    }

    public static GuiItem of(ItemStack itemStack, String actionKey) {
        return new GuiItem(itemStack, actionKey, true, false);
    }

    public ItemStack itemStack() {
        return itemStack.clone();
    }

    public String actionKey() {
        return actionKey;
    }

    public boolean cancelClick() {
        return cancelClick;
    }

    public boolean closeOnAction() {
        return closeOnAction;
    }
}
