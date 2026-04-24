package com.batzirdbuilds.batzirdsGuiAPI.runtime;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class GuiSessionManager {

    private final Map<UUID, GuiSession> sessionsByPlayer = new ConcurrentHashMap<>();
    private final Map<Inventory, GuiSession> sessionsByInventory = new ConcurrentHashMap<>();

    public void trackSession(UUID playerId, Inventory inventory, Map<Integer, Consumer<InventoryClickEvent>> slotActions,
                             boolean cancelClicksByDefault) {
        GuiSession session = new GuiSession(playerId, inventory, new ConcurrentHashMap<>(slotActions), cancelClicksByDefault);
        sessionsByPlayer.put(playerId, session);
        sessionsByInventory.put(inventory, session);
    }

    public void trackSession(Player player, Inventory inventory, Map<Integer, Consumer<InventoryClickEvent>> slotActions,
                             boolean cancelClicksByDefault) {
        trackSession(player.getUniqueId(), inventory, slotActions, cancelClicksByDefault);
    }

    public GuiSession findByPlayer(UUID playerId) {
        return sessionsByPlayer.get(playerId);
    }

    public GuiSession findByInventory(Inventory inventory) {
        return sessionsByInventory.get(inventory);
    }

    public GuiSession resolve(UUID playerId, Inventory inventory) {
        GuiSession byInventory = sessionsByInventory.get(inventory);
        if (byInventory != null && byInventory.playerId().equals(playerId)) {
            return byInventory;
        }

        GuiSession byPlayer = sessionsByPlayer.get(playerId);
        if (byPlayer != null && byPlayer.inventory().equals(inventory)) {
            return byPlayer;
        }

        return null;
    }

    public void removeSession(UUID playerId) {
        GuiSession removed = sessionsByPlayer.remove(playerId);
        if (removed != null) {
            sessionsByInventory.remove(removed.inventory(), removed);
        }
    }

    public void removeSession(Inventory inventory) {
        GuiSession removed = sessionsByInventory.remove(inventory);
        if (removed != null) {
            sessionsByPlayer.remove(removed.playerId(), removed);
        }
    }

    public int activeSessions() {
        return sessionsByPlayer.size();
    }

    public record GuiSession(UUID playerId, Inventory inventory,
                             Map<Integer, Consumer<InventoryClickEvent>> slotActions,
                             boolean cancelClicksByDefault) {
    }
}
