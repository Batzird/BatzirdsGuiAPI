package com.batzirdbuilds.batzirdsGuiAPI.gui;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Runtime options controlling how strongly a GUI inventory is protected.
 */
public final class GuiOptions {

    private final Set<Integer> interactiveSlots;
    private final boolean strictTopInventoryProtection;

    private GuiOptions(final Builder builder) {
        this.interactiveSlots = Collections.unmodifiableSet(new HashSet<>(builder.interactiveSlots));
        this.strictTopInventoryProtection = builder.strictTopInventoryProtection;
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

    public boolean isInteractiveSlot(final int slot) {
        return interactiveSlots.contains(slot);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Set<Integer> interactiveSlots = new HashSet<>();
        private boolean strictTopInventoryProtection = true;

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

        public GuiOptions build() {
            return new GuiOptions(this);
        }
    }
}
