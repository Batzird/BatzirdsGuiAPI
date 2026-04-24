package com.batzirdbuilds.batzirdsGuiAPI.gui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Runtime options controlling click behavior and top-inventory protection.
 */
public final class GuiOptions {

    private final Set<Integer> interactiveSlots;
    private final boolean strictTopInventoryProtection;
    private final boolean cancelAllClicks;
    private final boolean allowPlayerInventoryClicks;
    private final boolean closeOnAction;

    /**
     * Compatibility constructor used by the fluent GUI builder mutators.
     */
    public GuiOptions(
            final boolean cancelAllClicks,
            final boolean allowPlayerInventoryClicks,
            final boolean closeOnAction
    ) {
        this(Collections.emptySet(), cancelAllClicks, allowPlayerInventoryClicks, closeOnAction, cancelAllClicks);
    }

    private GuiOptions(
            final Set<Integer> interactiveSlots,
            final boolean cancelAllClicks,
            final boolean allowPlayerInventoryClicks,
            final boolean closeOnAction,
            final boolean strictTopInventoryProtection
    ) {
        this.interactiveSlots = Collections.unmodifiableSet(new HashSet<>(interactiveSlots));
        this.cancelAllClicks = cancelAllClicks;
        this.allowPlayerInventoryClicks = allowPlayerInventoryClicks;
        this.closeOnAction = closeOnAction;
        this.strictTopInventoryProtection = strictTopInventoryProtection;
    }

    public GuiOptions withFlags(
            final boolean cancelAllClicks,
            final boolean allowPlayerInventoryClicks,
            final boolean closeOnAction
    ) {
        return new GuiOptions(interactiveSlots, cancelAllClicks, allowPlayerInventoryClicks, closeOnAction, strictTopInventoryProtection);
    }

    public static GuiOptions defaults() {
        return new GuiOptions(true, false, false);
    }

    /**
     * @return immutable set of top-inventory slot indexes that are allowed to receive normal clicks.
     */
    public Set<Integer> interactiveSlots() {
        return interactiveSlots;
    }

    /**
     * @return true when non-whitelisted top-inventory clicks should be cancelled by default.
     */
    public boolean strictTopInventoryProtection() {
        return strictTopInventoryProtection;
    }

    public boolean cancelAllClicks() {
        return cancelAllClicks;
    }

    public boolean allowPlayerInventoryClicks() {
        return allowPlayerInventoryClicks;
    }

    public boolean closeOnAction() {
        return closeOnAction;
    }

    public boolean isInteractiveSlot(final int slot) {
        return interactiveSlots.contains(slot);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Set<Integer> interactiveSlots = new HashSet<>();
        private boolean strictTopInventoryProtection = true;
        private boolean allowPlayerInventoryClicks;
        private boolean closeOnAction;

        private Builder() {
        }

        public Builder addInteractiveSlot(final int slot) {
            if (slot < 0) {
                throw new IllegalArgumentException("Slot index must be zero or greater");
            }
            interactiveSlots.add(slot);
            return this;
        }

        public Builder addInteractiveSlots(final Set<Integer> slots) {
            Objects.requireNonNull(slots, "slots");
            for (final int slot : slots) {
                addInteractiveSlot(slot);
            }
            return this;
        }

        /**
         * Loosens the default click lock by allowing non-whitelisted top-inventory clicks.
         * <p>
         * Dangerous transfer paths (shift-click/hotbar swap/double-click collect/drag into GUI) remain protected.
         */
        public Builder strictTopInventoryProtection(final boolean strictTopInventoryProtection) {
            this.strictTopInventoryProtection = strictTopInventoryProtection;
            return this;
        }

        public Builder allowPlayerInventoryClicks(final boolean allowPlayerInventoryClicks) {
            this.allowPlayerInventoryClicks = allowPlayerInventoryClicks;
            return this;
        }

        public Builder closeOnAction(final boolean closeOnAction) {
            this.closeOnAction = closeOnAction;
            return this;
        }

        public GuiOptions build() {
            return new GuiOptions(
                    interactiveSlots,
                    strictTopInventoryProtection,
                    allowPlayerInventoryClicks,
                    closeOnAction,
                    strictTopInventoryProtection
            );
        }
    }
}
