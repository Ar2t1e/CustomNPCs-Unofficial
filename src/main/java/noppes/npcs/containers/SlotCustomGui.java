package noppes.npcs.containers;

import java.util.Objects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiItemSlotWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;

public class SlotCustomGui extends Slot {

    protected final CustomGuiWrapper gui;
    protected final EntityPlayer player;
    public final IItemSlot slot;

    public SlotCustomGui(CustomGuiWrapper guiIn, IInventory inventoryIn, int id, IItemSlot slotIn, EntityPlayer playerIn) {
        super(inventoryIn, id, slotIn.getPosX(), slotIn.getPosY());
        gui = guiIn;
        player = playerIn;
        slot = slotIn;
    }

    public SlotCustomGui update(int xIn, int yIn) {
        xPos += slot.getPosX();
        yPos += slot.getPosY();
        return this;
    }

    @Override
    public void putStack(@Nonnull ItemStack is) {
        super.putStack(is);
        if (!player.world.isRemote && getStack() != slot.getStack().getMCItemStack()) {
            if (!slot.isPlayerSlot()) {
                slot.setStack(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(getStack()));
                ((CustomGuiItemSlotWrapper)slot).onUpdate(gui);
            }
            if (player.openContainer instanceof ContainerCustomGui) {
                IItemStack heldItem = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(player.inventory.getCurrentItem());
                EventHooks.onCustomGuiSlot((PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player),
                        ((ContainerCustomGui) player.openContainer).customGui, slot, heldItem);
            }
        }
    }

}
