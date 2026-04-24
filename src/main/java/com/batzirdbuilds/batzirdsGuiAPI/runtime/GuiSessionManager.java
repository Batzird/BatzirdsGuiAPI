package com.batzirdbuilds.batzirdsGuiAPI.runtime;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public final class GuiSessionManager {

    private final Map<UUID, GuiSession> sessionsByPlayer = new ConcurrentHashMap<>();
    private final Map<Inventory, GuiSession> sessionsByInventory = new ConcurrentHashMap<>();

    public void trackSession(
            final UUID playerId,
            final Inventory inventory,
            final Map<Integer, Consumer<InventoryClickEvent>> slotActions,
            final boolean cancelClicksByDefault
    ) {
        final GuiSession session = new GuiSession(
                playerId,
                inventory,
                Map.copyOf(slotActions),
                cancelClicksByDefault
        );
        sessionsByPlayer.put(playerId, session);
        sessionsByInventory.put(inventory, session);
    }

    public void trackSession(
            final Player player,
            final Inventory inventory,
            final Map<Integer, Consumer<InventoryClickEvent>> slotActions,
            final boolean cancelClicksByDefault
    ) {
        trackSession(player.getUniqueId(), inventory, slotActions, cancelClicksByDefault);
    }

    public GuiSession resolve(final UUID playerId, final Inventory inventory) {
        final GuiSession byInventory = sessionsByInventory.get(inventory);
        if (byInventory != null && byInventory.playerId().equals(playerId)) {
            return byInventory;
        }

        final GuiSession byPlayer = sessionsByPlayer.get(playerId);
        if (byPlayer != null && byPlayer.inventory().equals(inventory)) {
            return byPlayer;
        }

        return null;
    }

    public void removeSession(final UUID playerId) {
        final GuiSession removed = sessionsByPlayer.remove(playerId);
        if (removed != null) {
            sessionsByInventory.remove(removed.inventory(), removed);
        }
    }

    public void removeSession(final Inventory inventory) {
        final GuiSession removed = sessionsByInventory.remove(inventory);
        if (removed != null) {
            sessionsByPlayer.remove(removed.playerId(), removed);
        }
    }

    public void clear() {
        sessionsByPlayer.clear();
        sessionsByInventory.clear();
    }

    public int activeSessions() {
        return sessionsByPlayer.size();
    }

    public record GuiSession(
            UUID playerId,
            Inventory inventory,
            Map<Integer, Consumer<InventoryClickEvent>> slotActions,
            boolean cancelClicksByDefault
    ) {
    }
}
