package com.batzirdbuilds.batzirdsGuiAPI;

import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiListener;
import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiSessionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Plugin bootstrap for the GUI API runtime services.
 */
public final class BatzirdsGuiAPI extends JavaPlugin, Listener {

    @Override
    public void onEnable() {
        getLogger().info("BatzirdsGuiAPI is not meant to be run as a plugin. Please shade this API into your plugins as a dependency!");
    }

    @Override
    public void onDisable() {
    }
    
}
