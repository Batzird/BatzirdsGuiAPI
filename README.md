# BatzirdsGuiAPI

A lightweight Paper API for building inventory GUIs quickly with:

- fluent GUI construction
- click handlers per slot
- click-protection policies to prevent item theft
- cosmetic layout helpers (borders/patterns/templates)
- optional logical column addressing for compact menu authoring

> Target platform: **Paper 1.21.11**

---

## Table of Contents

1. [Installation](#installation)
2. [Core Concepts](#core-concepts)
3. [Quick Start](#quick-start)
4. [Examples](#examples)
   - [Example 1: Minimal GUI](#example-1-minimal-gui)
   - [Example 2: Commands, chat, and Java logic](#example-2-commands-chat-and-java-logic)
   - [Example 3: Logical columns (compact authoring)](#example-3-logical-columns-compact-authoring)
   - [Example 4: Cosmetic utilities](#example-4-cosmetic-utilities)
5. [Click Protection & Options](#click-protection--options)
6. [API Reference (Current Surface)](#api-reference-current-surface)
7. [Design Notes](#design-notes)

---

## Installation

Add the shaded jar to your plugin dependencies, or include this project as a module/dependency in your build.

The plugin descriptor currently targets:

- `api-version: 1.21.11`
- `main: com.batzirdbuilds.batzirdsGuiAPI.BatzirdsGuiAPI`

---

## Core Concepts

### 1) `GuiBuilder`
Use `GuiBuilder` to define menu title/size, add items, bind click logic, and open inventories.

### 2) `GuiItem`
A wrapper for an `ItemStack` plus action metadata key.

### 3) `GuiOptions`
Configures behavior such as:

- `cancelAllClicks`
- `allowPlayerInventoryClicks`
- `closeOnAction`
- interactive slot whitelist and top-inventory strictness

### 4) Listener / Runtime
Click handling and protection are enforced through the GUI listener/session runtime classes.

---

## Quick Start

```java
import com.batzirdbuilds.batzirdsGuiAPI.gui.GuiBuilder;
import com.batzirdbuilds.batzirdsGuiAPI.gui.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public final class Menus {

    public void openMain(Player player) {
        GuiBuilder.create("main-menu")
                .title("Main Menu")
                .rows(3)
                .cancelAllClicks(true)
                .allowPlayerInventoryClicks(false)
                .setItem(13, GuiItem.of(new ItemStack(Material.EMERALD), "main:reward"))
                .onClick(13, event -> {
                    Player p = (Player) event.getWhoClicked();
                    p.sendMessage("You clicked the reward icon.");
                })
                .open(player);
    }
}
```

---

## Examples

## Example 1: Minimal GUI

```java
GuiBuilder builder = GuiBuilder.create("simple")
        .title("Simple GUI")
        .rows(3)
        .cancelAllClicks(true);

builder.setItem(11, GuiItem.of(new ItemStack(Material.DIAMOND), "simple:diamond"));
builder.setItem(15, GuiItem.of(new ItemStack(Material.GOLD_INGOT), "simple:gold"));

builder.onClick(11, e -> ((Player) e.getWhoClicked()).sendMessage("Diamond clicked"));
builder.onClick(15, e -> ((Player) e.getWhoClicked()).sendMessage("Gold clicked"));

builder.open(player);
```

## Example 2: Commands, chat, and Java logic

You can run commands/chat/custom Java logic from click callbacks. The API also includes action helpers in `action/*`.

```java
import com.batzirdbuilds.batzirdsGuiAPI.action.ChatAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.CommandAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.CompositeAction;
import com.batzirdbuilds.batzirdsGuiAPI.action.FunctionAction;
import com.batzirdbuilds.batzirdsGuiAPI.gui.ClickContext;

GuiBuilder builder = GuiBuilder.create("actions")
        .title("Action Demo")
        .rows(3)
        .cancelAllClicks(true)
        .allowPlayerInventoryClicks(false);

builder.setItem(10, GuiItem.of(new ItemStack(Material.COMPASS), "run-command"));
builder.setItem(13, GuiItem.of(new ItemStack(Material.PAPER), "send-chat"));
builder.setItem(16, GuiItem.of(new ItemStack(Material.BLAZE_ROD), "java-fn"));

builder.onClick(10, e -> {
    Player p = (Player) e.getWhoClicked();
    ClickContext ctx = new ClickContext(p, e.getClick(), e);
    CommandAction.server("say {player} clicked the command icon").execute(p, ctx);
});

builder.onClick(13, e -> {
    Player p = (Player) e.getWhoClicked();
    ClickContext ctx = new ClickContext(p, e.getClick(), e);
    ChatAction chat = new ChatAction("Hello from GUI chat action!");
    chat.execute(p, ctx);
});

builder.onClick(16, e -> {
    Player p = (Player) e.getWhoClicked();
    ClickContext ctx = new ClickContext(p, e.getClick(), e);

    CompositeAction.of(
            CommandAction.player("spawn"),
            new FunctionAction((player, click) -> player.giveExpLevels(1))
    ).andThen(new ChatAction("Granted +1 level and ran /spawn"))
     .execute(p, ctx);
});

builder.open(player);
```

## Example 3: Logical columns (compact authoring)

Chest inventories are physically 9 columns wide, but you can author with fewer logical columns.

```java
GuiBuilder builder = GuiBuilder.create("compact")
        .title("Compact 5-Column Layout")
        .rows(4)
        .columns(5) // logical addressing width
        .cancelAllClicks(true);

// row, column addressing (0-based)
builder.setItem(0, 0, GuiItem.of(new ItemStack(Material.RED_STAINED_GLASS_PANE), "top-left"));
builder.setItem(0, 4, GuiItem.of(new ItemStack(Material.GREEN_STAINED_GLASS_PANE), "top-right"));
builder.setItem(3, 2, GuiItem.of(new ItemStack(Material.NETHER_STAR), "bottom-center"));

builder.onClick(3, 2, e -> ((Player) e.getWhoClicked()).sendMessage("Bottom-center clicked"));

builder.open(player);
```

## Example 4: Cosmetic utilities

For decorative menus, use the cosmetic package helpers.

```java
import com.batzirdbuilds.batzirdsGuiAPI.GuiBuilder;
import com.batzirdbuilds.batzirdsGuiAPI.cosmetic.GuiTemplate;
import com.batzirdbuilds.batzirdsGuiAPI.cosmetic.ItemFactory;
import org.bukkit.Material;

ItemStack border = ItemFactory.named(Material.GRAY_STAINED_GLASS_PANE, " ");
ItemStack checkerA = ItemFactory.named(Material.BLACK_STAINED_GLASS_PANE, " ");
ItemStack checkerB = ItemFactory.named(Material.WHITE_STAINED_GLASS_PANE, " ");

GuiBuilder cosmetic = new GuiBuilder(27, "Cosmetics")
        .template(GuiTemplate.Preset.FRAME, border)
        .checkerTemplate(checkerA, checkerB);
```

---

## Click Protection & Options

### Recommended defaults for secure GUI menus

```java
GuiOptions options = GuiOptions.builder()
        .strictTopInventoryProtection(true)
        .allowPlayerInventoryClicks(false)
        .closeOnAction(false)
        .build();
```

Then apply:

```java
GuiBuilder.create("safe")
        .title("Safe Menu")
        .rows(3)
        .options(options);
```

### What protection covers

- shift-click transfers
- hotbar swap / number key actions
- double-click collect-to-cursor paths
- drags into protected top-inventory slots
- optional player-inventory blocking while GUI is open

---

## API Reference (Current Surface)

### `gui.GuiBuilder`

- `create(String id)` / `create()`
- `title(String)`
- `rows(int)`
- `columns(int)`
- `setItem(int slot, GuiItem)`
- `setItem(int row, int column, GuiItem)`
- `setItem(int slot, ItemStack, String actionKey)`
- `setItem(int row, int column, ItemStack, String actionKey)`
- `onClick(int slot, Consumer<InventoryClickEvent>)`
- `onClick(int row, int column, Consumer<InventoryClickEvent>)`
- `options(GuiOptions)`
- `cancelAllClicks(boolean)`
- `allowPlayerInventoryClicks(boolean)`
- `closeOnAction(boolean)`
- `fillBorder(GuiItem)`
- `build()`
- `open(Player)`

### `gui.GuiOptions`

- `defaults()`
- `withFlags(cancelAllClicks, allowPlayerInventoryClicks, closeOnAction)`
- `interactiveSlots()`
- `strictTopInventoryProtection()`
- `cancelAllClicks()`
- `allowPlayerInventoryClicks()`
- `closeOnAction()`
- Builder:
  - `addInteractiveSlot(int)`
  - `addInteractiveSlots(Set<Integer>)`
  - `strictTopInventoryProtection(boolean)`
  - `allowPlayerInventoryClicks(boolean)`
  - `closeOnAction(boolean)`
  - `build()`

### `action.*`

- `CommandAction`
- `ChatAction`
- `FunctionAction`
- `CompositeAction`

---

## Design Notes

1. Physical chest menus in Bukkit are always 9 columns wide.
2. Logical columns are an API-level addressing aid.
3. Prefer short callbacks that call service methods instead of large inline lambdas.
4. Keep GUI creation near command handlers and action logic in dedicated services.
