package com.batzirdbuilds.batzirdsGuiAPI.gui;

import java.util.Objects;
import java.util.function.Predicate;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;

/**
 * Global guard listener for plugin-owned GUI inventories.
 */
public final class GuiListener implements Listener {

    private final Predicate<InventoryView> guiMatcher;
    private final GuiOptions options;

    public GuiListener(final Predicate<InventoryView> guiMatcher, final GuiOptions options) {
        this.guiMatcher = Objects.requireNonNull(guiMatcher, "guiMatcher");
        this.options = Objects.requireNonNull(options, "options");
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        final InventoryView view = event.getView();
        if (!guiMatcher.test(view)) {
            return;
        }

        final int topSize = view.getTopInventory().getSize();
        final int rawSlot = event.getRawSlot();
        final boolean clickedTopInventory = rawSlot >= 0 && rawSlot < topSize;
        final boolean whitelistedInteractiveSlot = clickedTopInventory && options.isInteractiveSlot(rawSlot);

        // Protected path #1: block shift-transfer in both directions so players cannot inject/extract items.
        if (event.isShiftClick()) {
            event.setCancelled(true);
            return;
        }

        // Protected path #2: block hotbar swap/number-key actions that can bypass normal click restrictions.
        if (event.getClick() == ClickType.NUMBER_KEY
                || event.getAction() == InventoryAction.HOTBAR_SWAP
                || event.getAction() == InventoryAction.HOTBAR_MOVE_AND_READD) {
            event.setCancelled(true);
            return;
        }

        // Protected path #3: block double-click collect to cursor because it can pull items from GUI slots.
        if (event.getClick() == ClickType.DOUBLE_CLICK
                || event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            event.setCancelled(true);
            return;
        }

        // Protected path #4: by default, cancel direct clicks on GUI top inventory unless slot is explicitly whitelisted.
        if (clickedTopInventory && options.strictTopInventoryProtection() && !whitelistedInteractiveSlot) {
            event.setCancelled(true);
            return;
        }

        // Protected path #5: optionally block clicks in the player inventory while a GUI is open.
        if (!clickedTopInventory && !options.allowPlayerInventoryClicks()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        final InventoryView view = event.getView();
        if (!guiMatcher.test(view)) {
            return;
        }

        final int topSize = view.getTopInventory().getSize();

        // Protected path #6: block drag operations that touch any non-whitelisted GUI slot.
        for (final int rawSlot : event.getRawSlots()) {
            if (rawSlot < topSize && !options.isInteractiveSlot(rawSlot)) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
