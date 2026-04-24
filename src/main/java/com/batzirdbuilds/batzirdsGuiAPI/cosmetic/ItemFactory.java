package com.batzirdbuilds.batzirdsGuiAPI.cosmetic;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

/**
 * Utility for concise item creation used in menu cosmetics.
 */
public final class ItemFactory {

    private ItemFactory() {
    }

    public static ItemStack of(Material material) {
        Objects.requireNonNull(material, "material");
        return new ItemStack(material);
    }

    public static ItemStack named(Material material, String name) {
        return of(material, name, List.of(), null);
    }

    public static ItemStack of(Material material, String name, List<String> lore, Integer customModelData) {
        Objects.requireNonNull(material, "material");
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        if (name != null && !name.isBlank()) {
            meta.displayName(Component.text(name));
        }

        if (lore != null && !lore.isEmpty()) {
            meta.lore(lore.stream().map(Component::text).toList());
        }

        if (customModelData != null) {
            meta.setCustomModelData(customModelData);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
