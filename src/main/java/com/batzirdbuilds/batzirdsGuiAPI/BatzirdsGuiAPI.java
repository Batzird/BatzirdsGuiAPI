package com.batzirdbuilds.batzirdsGuiAPI;

import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiListener;
import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin bootstrap for the GUI API runtime services.
 */
public final class BatzirdsGuiAPI extends JavaPlugin {

    private static volatile BatzirdsGuiAPI instance;

    private GuiSessionManager sessionManager;

    public static BatzirdsGuiAPI getGuiApi() {
        return instance;
    }

    public GuiSessionManager getSessionManager() {
        return sessionManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        this.sessionManager = new GuiSessionManager();

        final PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new GuiListener(sessionManager), this);

        getLogger().info("BatzirdsGuiAPI enabled.");
    }

    @Override
    public void onDisable() {
        if (sessionManager != null) {
            sessionManager.clear();
        }

        sessionManager = null;
        instance = null;

        getLogger().info("BatzirdsGuiAPI disabled.");
    }
}
