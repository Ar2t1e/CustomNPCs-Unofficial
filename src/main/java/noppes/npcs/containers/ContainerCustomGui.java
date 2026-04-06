package noppes.npcs.containers;

import java.util.Iterator;
import java.util.Objects;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomContainer;
import noppes.npcs.EventHooks;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.wrapper.gui.CustomGuiWrapper;
import noppes.npcs.api.wrapper.gui.GuiComponentsScrollableWrapper;
import noppes.npcs.containers.slots.SlotCustomGui;
import noppes.npcs.mixin.world.inventory.IAbstractContainerMenuMixin;
import noppes.npcs.util.CustomNPCsScheduler;

import javax.annotation.Nonnull;

public class ContainerCustomGui extends AbstractContainerMenu {

   public CustomGuiWrapper customGui;
   public CustomGuiWrapper activeGui;
   public SimpleContainer guiInventory;
   public CompoundTag data;

   public ContainerCustomGui(int containerId, CompoundTag dataIn) {
      super(CustomContainer.container_customgui, containerId);
      data = dataIn;
      guiInventory = new SimpleContainer(0);
   }

   @Override
   public boolean stillValid(@Nonnull Player playerIn) { return true; }

   @Override
   public @Nonnull ItemStack quickMoveStack(@Nonnull Player playerIn, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = slots.get(index);
      if (slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (index < guiInventory.getContainerSize()) {
            if (!moveItemStackTo(itemstack1, guiInventory.getContainerSize(), slots.size(), true)) { return ItemStack.EMPTY; }
         }
         else if (!moveItemStackTo(itemstack1, 0, guiInventory.getContainerSize(), false)) { return ItemStack.EMPTY; }
         if (itemstack1.isEmpty()) { slot.set(ItemStack.EMPTY); }
         else { slot.setChanged(); }
      }
      return itemstack;
   }

   @Override
   public void clicked(int slotId, int dragType, @Nonnull ClickType clickTypeIn, @Nonnull Player playerIn) {
      if (slotId < 0) { super.clicked(slotId, dragType, clickTypeIn, playerIn); }
      else {
         if (!playerIn.level().isClientSide()) {
            IItemStack heldItem = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(playerIn.inventoryMenu.getCarried());
            SlotCustomGui slot = (SlotCustomGui) getSlot(slotId);
            if (!EventHooks.onCustomGuiSlotClicked((PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(playerIn), ((ContainerCustomGui) playerIn.containerMenu).activeGui, slot.slot, dragType, clickTypeIn.toString(), heldItem)) {
               super.clicked(slotId, dragType, clickTypeIn, playerIn);
               CustomNPCsScheduler.runTack(this::sendAllDataToRemote, 10);
            }
         }
      }
   }

   @Override
   public void removed(@Nonnull Player playerIn) {
      super.removed(playerIn);
      if (!playerIn.level().isClientSide) {
         EventHooks.onCustomGuiClose((PlayerWrapper<?>) Objects.requireNonNull(NpcAPI.Instance()).getIEntity(playerIn), customGui);
      }
   }

   public void setGui(CustomGuiWrapper gui, Player player) {
      activeGui = gui.getActiveGui();
      guiInventory = new SimpleContainer(activeGui.getSlots().size() + activeGui.getScrollingPanel().getSlots().size());
      customGui = gui;
      slots.clear();
      ((IAbstractContainerMenuMixin) this).getRemoteSlots().clear();
      ((IAbstractContainerMenuMixin) this).getLastSlots().clear();
      for (IItemSlot slot : activeGui.getSlots()) {
         Slot s = addSlot(new SlotCustomGui(gui, guiInventory, slot.getId(), slot, player));
         guiInventory.setItem(s.index, slot.getStack().getMCItemStack());
      }
      GuiComponentsScrollableWrapper panel = activeGui.getScrollingPanel();
      Iterator<IItemSlot> var9 = panel.getSlots().iterator();
      IItemSlot slot;
      while(var9.hasNext()) {
         slot = var9.next();
         Slot s = addSlot((new SlotCustomGui(gui, guiInventory, slot.getId(), slot, player)).update(panel.x, panel.y));
         guiInventory.setItem(s.index, slot.getStack().getMCItemStack());
      }
      var9 = activeGui.getPlayerSlots().iterator();
      while(var9.hasNext()) {
         slot = var9.next();
         addSlot(new SlotCustomGui(gui, player.getInventory(), slot.getId(), slot, player));
      }
      update();
   }

   public void update() {
      GuiComponentsScrollableWrapper panel = activeGui.getScrollingPanel();
      for(int i = 0; i < activeGui.getScrollingPanel().getSlots().size(); ++i) {
         SlotCustomGui slot = (SlotCustomGui) getSlot(i + activeGui.getSlots().size());
         if (panel.isVisible(slot.slot)) { slot.update(panel.x, panel.y - panel.scrollAmount); }
         else { slot.update(-1073741824, -1073741824); }
      }
   }

}
