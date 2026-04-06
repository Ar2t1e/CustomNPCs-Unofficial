package noppes.npcs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public class SlotAvailability extends Slot {

    public Container container;
    public int slot;

    public SlotAvailability(Container inventoryIn, int indexIn, int xPosition, int yPosition) {
        super(inventoryIn, indexIn, xPosition, yPosition);
        container = inventoryIn;
        slot = indexIn;
    }

    @Override
    public @Nonnull ItemStack getItem() { return container.getItem(slot); }

    @Override
    public void set(@Nonnull ItemStack stack) {
        container.setItem(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() { container.setChanged(); }

    @Override
    public int getMaxStackSize() { return container.getMaxStackSize(); }

    @Override
    public @NotNull ItemStack remove(int p_40227_) { return this.container.removeItem(slot, p_40227_); }

    @Override
    public boolean isSameInventory(Slot other) { return container == other.container; }

    @Override
    public int getSlotIndex()
    {
        return slot;
    }

    public void setSlotIndex(int newSlotID, boolean isCheck) {
        if (newSlotID < 0) { newSlotID = 0; }
        if (isCheck && newSlotID >= container.getContainerSize()) { return; }
        slot = newSlotID;
    }

    public void setInventory(Container newInventory) {
        if (newInventory == null) { return; }
        container = newInventory;
        if (slot >= container.getContainerSize()) { slot = container.getContainerSize() - 1; }
    }

}
