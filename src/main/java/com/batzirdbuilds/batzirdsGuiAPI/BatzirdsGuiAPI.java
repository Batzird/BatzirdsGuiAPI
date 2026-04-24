package com.batzirdbuilds.batzirdsGuiAPI;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BatzirdsGuiAPI extends JavaPlugin {

    private static volatile BatzirdsGuiAPI instance;

    private GuiSessionManager guiSessionManager;
    private GuiActionExecutors guiActionExecutors;

    /**
     * Minimal static service provider for external plugin integrations.
     */
    public static BatzirdsGuiAPI getGuiApi() {
        return instance;
    }

    public GuiSessionManager getGuiSessionManager() {
        return guiSessionManager;
    }

    public GuiActionExecutors getGuiActionExecutors() {
        return guiActionExecutors;
    }

    @Override
    public void onEnable() {
        // Startup: initialize the core API services first.
        instance = this;
        this.guiSessionManager = new GuiSessionManager();
        this.guiActionExecutors = new GuiActionExecutors();

        // Startup: register listeners after dependencies are created.
        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(
                new GuiSessionListener(guiSessionManager, guiActionExecutors),
                this
        );

        getLogger().info("BatzirdsGuiAPI enabled: session manager, action executors, and listeners initialized.");
    }

    @Override
    public void onDisable() {
        // Shutdown: safely close and forget any known GUI sessions.
        if (guiSessionManager != null) {
            final int clearedSessions = guiSessionManager.clearOpenSessionsSafely();
            getLogger().info("BatzirdsGuiAPI disabled: cleared " + clearedSessions + " open GUI session(s).");
        } else {
            getLogger().info("BatzirdsGuiAPI disabled: no session manager was initialized.");
        }

        this.guiActionExecutors = null;
        this.guiSessionManager = null;
        instance = null;
    }

    public static final class GuiSessionManager {

        private final Set<UUID> openSessions = ConcurrentHashMap.newKeySet();

        public void openSession(final HumanEntity entity) {
            if (entity != null) {
                openSessions.add(entity.getUniqueId());
            }
        }

        public void closeSession(final HumanEntity entity) {
            if (entity != null) {
                openSessions.remove(entity.getUniqueId());
            }
        }

        public int clearOpenSessionsSafely() {
            int cleared = 0;
            for (UUID sessionOwnerId : openSessions) {
                final Player player = Bukkit.getPlayer(sessionOwnerId);
                if (player == null || !player.isOnline()) {
                    continue;
                }

                player.getOpenInventory();
                player.closeInventory();

                cleared++;
            }

            openSessions.clear();
            return cleared;
        }
    }

    public static final class GuiActionExecutors {

        public void executeClickAction(final InventoryClickEvent event, final GuiSessionManager sessionManager) {
            event.getWhoClicked();
            sessionManager.openSession(event.getWhoClicked());
        }

        public void executeCloseAction(final InventoryCloseEvent event, final GuiSessionManager sessionManager) {
            event.getPlayer();
            sessionManager.closeSession(event.getPlayer());
        }
    }

    private static final class GuiSessionListener implements Listener {

        private final GuiSessionManager sessionManager;
        private final GuiActionExecutors actionExecutors;

        private GuiSessionListener(
                final GuiSessionManager sessionManager,
                final GuiActionExecutors actionExecutors
        ) {
            this.sessionManager = sessionManager;
            this.actionExecutors = actionExecutors;
        }

        @EventHandler
        private void onInventoryClick(final InventoryClickEvent event) {
            actionExecutors.executeClickAction(event, sessionManager);
        }

        @EventHandler
        private void onInventoryClose(final InventoryCloseEvent event) {
            actionExecutors.executeCloseAction(event, sessionManager);
        }
    }
}
