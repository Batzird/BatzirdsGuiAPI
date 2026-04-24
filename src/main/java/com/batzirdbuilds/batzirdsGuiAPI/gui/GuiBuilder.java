package com.batzirdbuilds.batzirdsGuiAPI.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Fluent builder for creating and opening inventory-backed GUIs.
 */
public final class GuiBuilder {

    private final String id;
    private String title = "GUI";
    private int rows = 3;
    private GuiOptions options = GuiOptions.defaults();

    private final Map<Integer, GuiItem> slotItems = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> slotActions = new HashMap<>();

    private GuiBuilder(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    public static GuiBuilder create(String id) {
        return new GuiBuilder(id);
    }

    public static GuiBuilder create() {
        return new GuiBuilder("gui-" + UUID.randomUUID());
    }

    // Layout: configure high-level dimensions and per-slot item placement.

    public GuiBuilder title(String title) {
        this.title = Objects.requireNonNull(title, "title");
        return this;
    }

    public GuiBuilder rows(int rows) {
        if (rows < 1 || rows > 6) {
            throw new IllegalArgumentException("rows must be between 1 and 6");
        }
        this.rows = rows;
        return this;
    }

    public GuiBuilder setItem(int slot, GuiItem item) {
        validateSlot(slot);
        slotItems.put(slot, Objects.requireNonNull(item, "item"));
        return this;
    }

    public GuiBuilder setItem(int slot, ItemStack itemStack, String actionKey) {
        return setItem(slot, GuiItem.of(itemStack, actionKey));
    }

    // Behavior: bind click handlers and toggle interaction policies.

    public GuiBuilder onClick(int slot, Consumer<InventoryClickEvent> action) {
        validateSlot(slot);
        slotActions.put(slot, Objects.requireNonNull(action, "action"));
        return this;
    }

    public GuiBuilder options(GuiOptions options) {
        this.options = Objects.requireNonNull(options, "options");
        return this;
    }

    public GuiBuilder cancelAllClicks(boolean value) {
        this.options = new GuiOptions(value, options.allowPlayerInventoryClicks(), options.closeOnAction());
        return this;
    }

    public GuiBuilder allowPlayerInventoryClicks(boolean value) {
        this.options = new GuiOptions(options.cancelAllClicks(), value, options.closeOnAction());
        return this;
    }

    public GuiBuilder closeOnAction(boolean value) {
        this.options = new GuiOptions(options.cancelAllClicks(), options.allowPlayerInventoryClicks(), value);
        return this;
    }

    // Cosmetics: utility helpers for common visual decoration.

    public GuiBuilder fillBorder(GuiItem item) {
        Objects.requireNonNull(item, "item");
        int size = rows * 9;
        int lastRowStart = size - 9;
        for (int slot = 0; slot < size; slot++) {
            boolean border = slot < 9 || slot >= lastRowStart || slot % 9 == 0 || slot % 9 == 8;
            if (border && !slotItems.containsKey(slot)) {
                slotItems.put(slot, item);
            }
        }
        return this;
    }

    public GuiDefinition build() {
        return new GuiDefinition(id, title, rows, slotItems, options);
    }

    public Inventory open(Player player) {
        Objects.requireNonNull(player, "player");
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        slotItems.forEach((slot, guiItem) -> inventory.setItem(slot, guiItem.itemStack()));
        player.openInventory(inventory);
        return inventory;
    }

    public Map<Integer, Consumer<InventoryClickEvent>> slotActions() {
        return Map.copyOf(slotActions);
    }

    private void validateSlot(int slot) {
        int size = rows * 9;
        if (slot < 0 || slot >= size) {
            throw new IllegalArgumentException("slot must be between 0 and " + (size - 1));
        }
    }
}
