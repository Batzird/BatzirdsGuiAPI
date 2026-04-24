package com.batzirdbuilds.batzirdsGuiAPI.runtime;

import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiSessionManager.GuiSession;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public final class GuiListener implements Listener {

    private final GuiSessionManager sessionManager;

    public GuiListener(final GuiSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final UUID playerId = player.getUniqueId();
        final GuiSession session = sessionManager.resolve(playerId, event.getView().getTopInventory());
        if (session == null) {
            return;
        }

        final int rawSlot = event.getRawSlot();
        final boolean clickedTopInventory = rawSlot >= 0 && rawSlot < session.inventory().getSize();
        if (!clickedTopInventory) {
            if (session.cancelClicksByDefault()) {
                event.setCancelled(true);
            }
            return;
        }

        if (session.cancelClicksByDefault()) {
            event.setCancelled(true);
        }

        final Consumer<InventoryClickEvent> slotAction = session.slotActions().get(rawSlot);
        if (slotAction != null) {
            slotAction.accept(event);
        }
    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        final GuiSession session = sessionManager.resolve(player.getUniqueId(), event.getView().getTopInventory());
        if (session == null) {
            return;
        }

        if (session.cancelClicksByDefault()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        final GuiSession session = sessionManager.resolve(player.getUniqueId(), event.getInventory());
        if (session == null) {
            return;
        }

        sessionManager.removeSession(player.getUniqueId());
        sessionManager.removeSession(event.getInventory());
    }
}
