package com.batzirdbuilds.batzirdsGuiAPI.cosmetic;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Slot-selection patterns for GUI cosmetics.
 */
public final class GuiPatterns {

    private static final int ROW_WIDTH = 9;

    private GuiPatterns() {
    }

    /**
     * Strategy: selects every slot from index {@code 0} to {@code size - 1}.
     */
    public static void fill(Inventory inventory, ItemStack itemStack) {
        validate(inventory, itemStack);
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            inventory.setItem(slot, itemStack.clone());
        }
    }

    /**
     * Strategy: selects top row, bottom row, and the first/last slot in each middle row.
     */
    public static void border(Inventory inventory, ItemStack itemStack) {
        validate(inventory, itemStack);

        int rows = rowCount(inventory);
        if (rows == 0) {
            return;
        }

        fillRow(inventory, 0, itemStack);
        if (rows > 1) {
            fillRow(inventory, rows - 1, itemStack);
        }

        for (int row = 1; row < rows - 1; row++) {
            int rowStart = row * ROW_WIDTH;
            inventory.setItem(rowStart, itemStack.clone());
            inventory.setItem(rowStart + ROW_WIDTH - 1, itemStack.clone());
        }
    }

    /**
     * Strategy: alternates two items by parity of (row + column), creating a checkerboard pattern.
     */
    public static void checker(Inventory inventory, ItemStack primary, ItemStack secondary) {
        Objects.requireNonNull(primary, "primary");
        Objects.requireNonNull(secondary, "secondary");
        Objects.requireNonNull(inventory, "inventory");

        int rows = rowCount(inventory);
        for (int row = 0; row < rows; row++) {
            for (int column = 0; column < ROW_WIDTH; column++) {
                int slot = row * ROW_WIDTH + column;
                ItemStack selected = ((row + column) % 2 == 0 ? primary : secondary).clone();
                inventory.setItem(slot, selected);
            }
        }
    }

    /**
     * Strategy: selects the 9 contiguous slots in one row ({@code row * 9} to {@code row * 9 + 8}).
     */
    public static void fillRow(Inventory inventory, int row, ItemStack itemStack) {
        validate(inventory, itemStack);

        int rows = rowCount(inventory);
        if (row < 0 || row >= rows) {
            throw new IllegalArgumentException("Row out of range: " + row + " (rows=" + rows + ")");
        }

        int start = row * ROW_WIDTH;
        for (int offset = 0; offset < ROW_WIDTH; offset++) {
            inventory.setItem(start + offset, itemStack.clone());
        }
    }

    /**
     * Strategy: selects every slot with the same column index across all rows ({@code row * 9 + column}).
     */
    public static void fillColumn(Inventory inventory, int column, ItemStack itemStack) {
        validate(inventory, itemStack);

        if (column < 0 || column >= ROW_WIDTH) {
            throw new IllegalArgumentException("Column out of range: " + column);
        }

        int rows = rowCount(inventory);
        for (int row = 0; row < rows; row++) {
            inventory.setItem(row * ROW_WIDTH + column, itemStack.clone());
        }
    }

    private static void validate(Inventory inventory, ItemStack itemStack) {
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(itemStack, "itemStack");
    }

    private static int rowCount(Inventory inventory) {
        if (inventory.getSize() % ROW_WIDTH != 0) {
            throw new IllegalArgumentException("Inventory size must be a multiple of 9: " + inventory.getSize());
        }
        return inventory.getSize() / ROW_WIDTH;
    }
}
