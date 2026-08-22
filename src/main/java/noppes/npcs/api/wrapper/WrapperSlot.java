package noppes.npcs.api.wrapper;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import noppes.npcs.api.IContainer;
import noppes.npcs.api.ISlot;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;

import java.util.Objects;

public class WrapperSlot implements ISlot {

    private final Slot slot;

    public WrapperSlot(Slot slotIn) { slot = slotIn; }

    @Override
    public int getX() { return slot.xPos; }

    @Override
    public int getY() { return slot.yPos; }

    @Override
    public int getId() { return slot.slotNumber; }

    @Override
    public int getIndex() { return slot.getSlotIndex(); }

    @Override
    public IItemStack getItem() { return Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(slot.getStack()); }

    @Override
    public Slot getMCSlot() { return slot; }

    @Override
    public IContainer getIContainer() {
        return Objects.requireNonNull(NpcAPI.Instance()).getIContainer(slot.inventory);
    }

    @Override
    public IInventory getMCInventory() { return slot.inventory; }

}
