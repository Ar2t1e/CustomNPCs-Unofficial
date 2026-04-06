package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomNpcs;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.constants.GuiComponentType;
import noppes.npcs.api.functions.gui.GuiItemSlotUpdate;
import noppes.npcs.api.gui.ICustomGui;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemStackWrapper;
import noppes.npcs.containers.ContainerCustomGui;

import java.util.Objects;

public class CustomGuiItemSlotWrapper extends CustomGuiComponentWrapper implements IItemSlot {

   protected IItemStack stack = ItemStackWrapper.AIR;
   protected int guiType = 1;
   protected Player player;
   protected GuiItemSlotUpdate onSlotUpdate = null;

   public CustomGuiItemSlotWrapper() { }

   public CustomGuiItemSlotWrapper(int x, int y, IItemStack stack) {
      setPos(x, y);
      setSize(14, 14);
      setStack(stack);
   }

   public CustomGuiItemSlotWrapper(int x, int y, Player playerIn) {
      player = playerIn;
      setPos(x, y);
      setSize(14, 14);
   }

   @Override
   public boolean hasStack() { return !stack.isEmpty(); }

   @Override
   public IItemStack getStack() {
      if (player != null) { stack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(player.getInventory().getItem(getId())); }
      return stack;
   }

   @Override
   public IItemSlot setStack(IItemStack stackIn) {
      stack = stackIn == null ? ItemStackWrapper.AIR : stackIn;
      if (player != null) { player.getInventory().setItem(getId(), stack.getMCItemStack()); }
      return this;
   }

   @Override
   public int getGuiType() { return guiType; }

   @Override
   public CustomGuiItemSlotWrapper setGuiType(int type) {
      guiType = type;
      return this;
   }

   @Override
   public Slot getMCSlot() {
      if (player != null && player.containerMenu instanceof ContainerCustomGui container) { return container.getSlot(id); }
      return null;
   }

   @Override
   public int getType() { return GuiComponentType.ITEM_SLOT.get(); }

   @Override
   public CompoundTag toNBT(CompoundTag nbt) {
      super.toNBT(nbt);
      nbt.put("stack", stack.getMCItemStack().serializeNBT());
      nbt.putInt("guiType", guiType);
      nbt.putBoolean("playerSlot", isPlayerSlot());
      return nbt;
   }

   @Override
   public CustomGuiComponentWrapper fromNBT(CompoundTag nbt) {
      super.fromNBT(nbt);
      setStack(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(ItemStack.of(nbt.getCompound("stack"))));
      setGuiType(nbt.getInt("guiType"));
      if (nbt.getBoolean("playerSlot")) { player = CustomNpcs.proxy.getPlayer(); }
      return this;
   }

   @Override
   public boolean isPlayerSlot() { return player != null; }

   @Override
   public CustomGuiItemSlotWrapper setOnUpdate(GuiItemSlotUpdate onPress) {
      onSlotUpdate = onPress;
      return this;
   }

   public final void onUpdate(ICustomGui gui) {
      if (onSlotUpdate != null) { onSlotUpdate.onUpdate(gui, this); }
   }

}
