package noppes.npcs.containers;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;

import javax.annotation.Nonnull;

public class ContainerDead extends AbstractContainerMenu {

    public final int size;
    public final int pos;
    public final Component playerParent;

    public ContainerDead(int containerId, Inventory inv, Container container, Component name, int p) {
        super(CustomContainer.container_npc_dead, containerId);
        size = (int) Math.ceil((double) container.getContainerSize() / 9.0d);
        pos = p;
        playerParent = name;
        int h = 54 - 9 - size * 9;
        int w = 8;
        // Dead Inventory
        for (int id = 0; id < container.getContainerSize(); ++id) {
            int x = id % 9;
            int y = (int) Math.floor((double) id / 9.0d);
            addSlot(new Slot(container, id, w + x * 18, h + y * 18));
        }
        h += 6 + size * 18;
        // Player Inventory
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlot(new Slot(inv, j + i * 9 + 9, w + j * 18, h + i * 18));
            }
        }
        h += 58;
        for (int j = 0; j < 9; ++j) {
            addSlot(new Slot(inv, j, w + j * 18, h));
        }
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) { return false; }

    @Override
    public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int slotId) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(slotId);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();
            if (slotId < size * 9) {
                if (!moveItemStackTo(slotStack, size * 9, slots.size(), true)) { return ItemStack.EMPTY; }
            }
            else if (!moveItemStackTo(slotStack, 0, size * 9, true)) { return ItemStack.EMPTY; }
            if (slotStack.getCount() == 0) { slot.set(ItemStack.EMPTY); }
            else { slot.setChanged(); }
        }
        return stack;
    }

}