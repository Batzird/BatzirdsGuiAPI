package com.batzirdbuilds.batzirdsGuiAPI;

import com.batzirdbuilds.batzirdsGuiAPI.cosmetic.GuiPatterns;
import com.batzirdbuilds.batzirdsGuiAPI.cosmetic.GuiTemplate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Builder-style wrapper around a Bukkit inventory.
 * Includes ergonomic cosmetic helpers so menu event code can stay focused on behavior.
 */
public final class GuiBuilder {

    private final Inventory inventory;

    public GuiBuilder(int size, String title) {
        if (size <= 0 || size % 9 != 0) {
            throw new IllegalArgumentException("GUI size must be a positive multiple of 9: " + size);
        }
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    public GuiBuilder(Inventory inventory) {
        this.inventory = Objects.requireNonNull(inventory, "inventory");
    }

    public Inventory inventory() {
        return inventory;
    }

    public GuiBuilder set(int slot, ItemStack itemStack) {
        Objects.requireNonNull(itemStack, "itemStack");
        inventory.setItem(slot, itemStack.clone());
        return this;
    }

    public GuiBuilder fill(ItemStack itemStack) {
        GuiPatterns.fill(inventory, itemStack);
        return this;
    }

    public GuiBuilder border(ItemStack itemStack) {
        GuiPatterns.border(inventory, itemStack);
        return this;
    }

    public GuiBuilder checker(ItemStack primary, ItemStack secondary) {
        GuiPatterns.checker(inventory, primary, secondary);
        return this;
    }

    public GuiBuilder fillRow(int row, ItemStack itemStack) {
        GuiPatterns.fillRow(inventory, row, itemStack);
        return this;
    }

    public GuiBuilder fillColumn(int column, ItemStack itemStack) {
        GuiPatterns.fillColumn(inventory, column, itemStack);
        return this;
    }

    public GuiBuilder template(GuiTemplate.Preset preset, ItemStack itemStack) {
        GuiTemplate.apply(inventory, preset, itemStack);
        return this;
    }

    public GuiBuilder checkerTemplate(ItemStack primary, ItemStack secondary) {
        GuiTemplate.applyChecker(inventory, primary, secondary);
        return this;
    }
}
