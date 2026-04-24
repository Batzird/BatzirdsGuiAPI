package com.batzirdbuilds.batzirdsGuiAPI;

import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiListener;
import com.batzirdbuilds.batzirdsGuiAPI.runtime.GuiSessionManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class BatzirdsGuiAPI extends JavaPlugin {

    private GuiSessionManager guiSessionManager;

    @Override
    public void onEnable() {
        guiSessionManager = new GuiSessionManager();
        getServer().getPluginManager().registerEvents(new GuiListener(guiSessionManager), this);
    }

    @Override
    public void onDisable() {
        guiSessionManager = null;
    }

    public GuiSessionManager getGuiSessionManager() {
        return guiSessionManager;
    }
}
