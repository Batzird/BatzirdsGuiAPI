package com.batzirdbuilds.batzirdsGuiAPI.cosmetic;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Reusable menu layout presets.
 */
public final class GuiTemplate {

    private GuiTemplate() {
    }

    public enum Preset {
        FRAME,
        HEADER_FOOTER,
        SIDE_COLUMNS,
        CORNERS,
        DIAGONAL,
        FULL
    }

    public static void apply(Inventory inventory, Preset preset, ItemStack itemStack) {
        Objects.requireNonNull(inventory, "inventory");
        Objects.requireNonNull(itemStack, "itemStack");
        Objects.requireNonNull(preset, "preset");

        switch (preset) {
            case FRAME -> GuiPatterns.border(inventory, itemStack);
            case HEADER_FOOTER -> {
                GuiPatterns.fillRow(inventory, 0, itemStack);
                GuiPatterns.fillRow(inventory, rowCount(inventory) - 1, itemStack);
            }
            case SIDE_COLUMNS -> {
                GuiPatterns.fillColumn(inventory, 0, itemStack);
                GuiPatterns.fillColumn(inventory, 8, itemStack);
            }
            case CORNERS -> GuiPatterns.corners(inventory, itemStack);
            case DIAGONAL -> GuiPatterns.diagonal(inventory, itemStack);
            case FULL -> GuiPatterns.fill(inventory, itemStack);
        }
    }

    public static void applyChecker(Inventory inventory, ItemStack primary, ItemStack secondary) {
        Objects.requireNonNull(inventory, "inventory");
        GuiPatterns.checker(inventory, primary, secondary);
    }

    private static int rowCount(Inventory inventory) {
        if (inventory.getSize() % 9 != 0) {
            throw new IllegalArgumentException("Inventory size must be a multiple of 9: " + inventory.getSize());
        }
        return inventory.getSize() / 9;
    }
}
