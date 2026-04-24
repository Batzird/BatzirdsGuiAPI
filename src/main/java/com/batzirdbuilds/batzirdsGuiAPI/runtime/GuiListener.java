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

public class GuiListener implements Listener {

    private final GuiSessionManager sessionManager;

    public GuiListener(GuiSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        // resolve session
        UUID playerId = player.getUniqueId();
        GuiSession session = sessionManager.resolve(playerId, event.getView().getTopInventory());
        if (session == null) {
            return;
        }

        // validate click target
        int rawSlot = event.getRawSlot();
        boolean clickedTopInventory = rawSlot >= 0 && rawSlot < session.inventory().getSize();
        if (!clickedTopInventory) {
            if (session.cancelClicksByDefault()) {
                event.setCancelled(true);
            }
            return;
        }

        // execute action
        if (session.cancelClicksByDefault()) {
            event.setCancelled(true);
        }

        Consumer<InventoryClickEvent> slotAction = session.slotActions().get(rawSlot);
        if (slotAction != null) {
            slotAction.accept(event);
        }

        // cleanup
        if (!event.getWhoClicked().getOpenInventory().getTopInventory().equals(session.inventory())) {
            sessionManager.removeSession(playerId);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        GuiSession session = sessionManager.resolve(player.getUniqueId(), event.getView().getTopInventory());
        if (session == null) {
            return;
        }

        if (session.cancelClicksByDefault()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) {
            return;
        }

        // resolve session
        GuiSession session = sessionManager.resolve(player.getUniqueId(), event.getInventory());
        if (session == null) {
            return;
        }

        // cleanup
        sessionManager.removeSession(player.getUniqueId());
        sessionManager.removeSession(event.getInventory());
    }
}
