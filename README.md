# BatzirdsGuiAPI Quick Start

Create inventory GUIs with:
- click actions (commands, chat, Java functions)
- click protection (prevent item stealing)
- cosmetic helpers (borders/templates/fills)

## 1) Simple Example: One menu, one button

```java
import com.batzirdbuilds.batzirdsGuiAPI.gui.GuiBuilder;
import com.batzirdbuilds.batzirdsGuiAPI.gui.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

// Create and open a 3-row menu
public void openSimpleMenu(Player player) {
    GuiBuilder builder = GuiBuilder.create("simple-menu")
            .title("Simple Menu")
            .rows(3)
            .cancelAllClicks(true)              // prevent item taking from GUI
            .allowPlayerInventoryClicks(false); // lock player inventory while GUI open

    // Put a clickable item in slot 13
    builder.setItem(13, GuiItem.of(new ItemStack(Material.EMERALD), "reward"));

    // Bind click behavior for slot 13
    builder.onClick(13, event -> {
        Player p = (Player) event.getWhoClicked();
        p.sendMessage("You clicked the reward button!");
        p.performCommand("spawn");
    });

    builder.open(player);
}
