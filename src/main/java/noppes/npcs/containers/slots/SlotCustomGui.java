package noppes.npcs.containers.slots;

import java.util.Objects;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiItemSlotWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.containers.ContainerCustomGui;
import noppes.npcs.mixin.world.inventory.ISlotMixin;

import javax.annotation.Nonnull;

public class SlotCustomGui extends Slot {

   protected final CustomGuiWrapper gui;
   protected final Player player;
   public final IItemSlot slot;

   public SlotCustomGui(CustomGuiWrapper guiIn, Container inventoryIn, int id, IItemSlot slotIn, Player playerIn) {
      super(inventoryIn, id, slotIn.getPosX(), slotIn.getPosY());
      gui = guiIn;
      player = playerIn;
      slot = slotIn;
   }

   public SlotCustomGui update(int xIn, int yIn) {
      ((ISlotMixin) this).setX(xIn + slot.getPosX());
      ((ISlotMixin) this).setY(yIn + slot.getPosY());
      return this;
   }

   @Override
   public void set(@Nonnull ItemStack is) {
      super.set(is);
      if (!player.level().isClientSide && getItem() != slot.getStack().getMCItemStack()) {
         if (!slot.isPlayerSlot()) {
            slot.setStack(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(getItem()));
            ((CustomGuiItemSlotWrapper)slot).onUpdate(gui);
         }
         if (player.containerMenu instanceof ContainerCustomGui cont) {
            IItemStack heldItem = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(player.inventoryMenu.getCarried());
            EventHooks.onCustomGuiSlot((PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(player),
                    cont.customGui, slot, heldItem);
         }
      }
   }

}
