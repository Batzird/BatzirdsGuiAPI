package com.batzirdbuilds.batzirdsGuiAPI.gui;

import com.batzirdbuilds.batzirdsGuiAPI.BatzirdsGuiAPI;
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
    private int columns = 9;
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

    /**
     * Sets logical column count for slot addressing (1-9).
     * <p>
     * Bukkit chest inventories are always 9 columns wide, so this controls API addressing only.
     */
    public GuiBuilder columns(int columns) {
        if (columns < 1 || columns > 9) {
            throw new IllegalArgumentException("columns must be between 1 and 9");
        }
        this.columns = columns;
        return this;
    }

    public GuiBuilder setItem(int slot, GuiItem item) {
        validateVirtualSlot(slot);
        slotItems.put(toRawSlot(slot), Objects.requireNonNull(item, "item"));
        return this;
    }

    public GuiBuilder setItem(int row, int column, GuiItem item) {
        return setItem(toVirtualSlot(row, column), item);
    }

    public GuiBuilder setItem(int slot, ItemStack itemStack, String actionKey) {
        return setItem(slot, GuiItem.of(itemStack, actionKey));
    }

    public GuiBuilder setItem(int row, int column, ItemStack itemStack, String actionKey) {
        return setItem(row, column, GuiItem.of(itemStack, actionKey));
    }

    // Behavior: bind click handlers and toggle interaction policies.

    public GuiBuilder onClick(int slot, Consumer<InventoryClickEvent> action) {
        validateVirtualSlot(slot);
        slotActions.put(toRawSlot(slot), Objects.requireNonNull(action, "action"));
        return this;
    }

    public GuiBuilder onClick(int row, int column, Consumer<InventoryClickEvent> action) {
        return onClick(toVirtualSlot(row, column), action);
    }

    public GuiBuilder options(GuiOptions options) {
        this.options = Objects.requireNonNull(options, "options");
        return this;
    }

    public GuiBuilder cancelAllClicks(boolean value) {
        this.options = options.withFlags(value, options.allowPlayerInventoryClicks(), options.closeOnAction());
        return this;
    }

    public GuiBuilder allowPlayerInventoryClicks(boolean value) {
        this.options = options.withFlags(options.cancelAllClicks(), value, options.closeOnAction());
        return this;
    }

    public GuiBuilder closeOnAction(boolean value) {
        this.options = options.withFlags(options.cancelAllClicks(), options.allowPlayerInventoryClicks(), value);
        return this;
    }

    // Cosmetics: utility helpers for common visual decoration.

    public GuiBuilder fillBorder(GuiItem item) {
        Objects.requireNonNull(item, "item");
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < columns; column++) {
                boolean border = row == 0 || row == rows - 1 || column == 0 || column == columns - 1;
                if (border) {
                    int rawSlot = toRawSlot(toVirtualSlot(row, column));
                    if (!slotItems.containsKey(rawSlot)) {
                        slotItems.put(rawSlot, item);
                    }
                }
            }
        }
        return this;
    }


    public GuiBuilder fillRow(int row, GuiItem item) {
        Objects.requireNonNull(item, "item");
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("row must be between 0 and " + (rows - 1));
        }

        for (int column = 0; column < columns; column++) {
            int rawSlot = toRawSlot(toVirtualSlot(row, column));
            if (!slotItems.containsKey(rawSlot)) {
                slotItems.put(rawSlot, item);
            }
        }
        return this;
    }

    public GuiBuilder fillColumn(int column, GuiItem item) {
        Objects.requireNonNull(item, "item");
        if (column < 0 || column >= columns) {
            throw new IllegalArgumentException("column must be between 0 and " + (columns - 1));
        }

        for (int row = 0; row < rows; row++) {
            int rawSlot = toRawSlot(toVirtualSlot(row, column));
            if (!slotItems.containsKey(rawSlot)) {
                slotItems.put(rawSlot, item);
            }
        }
        return this;
    }

    public GuiDefinition build() {
        return new GuiDefinition(id, title, rows, columns, slotItems, options);
    }

    public Inventory open(Player player) {
        Objects.requireNonNull(player, "player");
        Inventory inventory = Bukkit.createInventory(null, rows * 9, title);
        slotItems.forEach(inventory::setItem);
        player.openInventory(inventory);

        // Track callback actions so runtime listener can resolve and execute them.
        BatzirdsGuiAPI api = BatzirdsGuiAPI.getGuiApi();
        if (api != null && api.getSessionManager() != null) {
            api.getSessionManager().trackSession(player, inventory, slotActions, options.cancelAllClicks());
        }

        return inventory;
    }

    public Map<Integer, Consumer<InventoryClickEvent>> slotActions() {
        return Map.copyOf(slotActions);
    }

    private void validateVirtualSlot(int slot) {
        int logicalSize = rows * columns;
        if (slot < 0 || slot >= logicalSize) {
            throw new IllegalArgumentException("slot must be between 0 and " + (logicalSize - 1));
        }
    }

    private int toVirtualSlot(int row, int column) {
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("row must be between 0 and " + (rows - 1));
        }
        if (column < 0 || column >= columns) {
            throw new IllegalArgumentException("column must be between 0 and " + (columns - 1));
        }
        return row * columns + column;
    }

    private int toRawSlot(int virtualSlot) {
        int row = virtualSlot / columns;
        int column = virtualSlot % columns;
        return row * 9 + column;
    }
}
