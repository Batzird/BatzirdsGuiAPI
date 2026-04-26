import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

import java.util.Set;

public class SilentConsoleSender implements ConsoleCommandSender {

    private final ConsoleCommandSender delegate;
    private final PermissibleBase perm;

    public SilentConsoleSender(ConsoleCommandSender delegate) {
        this.delegate = delegate;
        this.perm = new PermissibleBase(this);
    }

    // --- Suppress all output ---
    @Override public void sendMessage(String message) {}
    @Override public void sendMessage(String... messages) {}

    // --- Identity ---
    @Override public String getName() { return "SilentConsole"; }
    @Override public Server getServer() { return delegate.getServer(); }

    // --- Permissions (full access) ---
    @Override public boolean isPermissionSet(String name) { return true; }
    @Override public boolean isPermissionSet(Permission perm) { return true; }
    @Override public boolean hasPermission(String name) { return true; }
    @Override public boolean hasPermission(Permission perm) { return true; }
    @Override public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value) { return this.perm.addAttachment(plugin, name, value); }
    @Override public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin) { return this.perm.addAttachment(plugin); }
    @Override public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, String name, boolean value, int ticks) { return this.perm.addAttachment(plugin, name, value, ticks); }
    @Override public PermissionAttachment addAttachment(org.bukkit.plugin.Plugin plugin, int ticks) { return this.perm.addAttachment(plugin, ticks); }
    @Override public void removeAttachment(PermissionAttachment attachment) { this.perm.removeAttachment(attachment); }
    @Override public void recalculatePermissions() { this.perm.recalculatePermissions(); }
    @Override public Set<PermissionAttachmentInfo> getEffectivePermissions() { return this.perm.getEffectivePermissions(); }

    // --- OP ---
    @Override public boolean isOp() { return true; }
    @Override public void setOp(boolean value) {}

    // --- Spigot ---
    @Override public Spigot spigot() { return delegate.spigot(); }
}
