package com.batzirdbuilds.batzirdsGuiAPI.gui;

/**
 * Behavior toggles used when creating and handling GUIs.
 */
public record GuiOptions(
        boolean cancelAllClicks,
        boolean allowPlayerInventoryClicks,
        boolean closeOnAction
) {

    public static GuiOptions defaults() {
        return new GuiOptions(true, false, false);
    }
}
