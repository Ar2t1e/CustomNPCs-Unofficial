package noppes.npcs.containers.inventories;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.NBTTags;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.mixin.core.INonNullListMixin;
import noppes.npcs.mixin.world.ISimpleContainerMixin;

import java.util.Arrays;

public class NpcMiscInventory extends SimpleContainer {

   protected final int stackLimit;

   public NpcMiscInventory(int sizeIn) {
      super(sizeIn);
      stackLimit = 64;
   }

   public NpcMiscInventory(int sizeIn, int stackLimitIn) {
      super(sizeIn);
      stackLimit = stackLimitIn;
   }

   @Override
   public int getMaxStackSize() { return stackLimit; }

   @Override
   public boolean isEmpty() {
      for(int slot = 0; slot < getContainerSize(); ++slot) {
         ItemStack item = getItem(slot);
         if (!NoppesUtilServer.isItemStackNull(item)) { return false; }
      }
      return true;
   }

   public CompoundTag save() {
      CompoundTag compound = new CompoundTag();
      compound.put("NpcMiscInv", NBTTags.nbtItemStackList(((ISimpleContainerMixin) this).getItems()));
      return compound;
   }

   public NpcMiscInventory load(CompoundTag compound) {
      NBTTags.getItemStackList(compound.getList("NpcMiscInv", 10), this);
      return this;
   }

   public boolean removeItem(ItemStack eating, int decrease) {
      for(int slot = 0; slot < getContainerSize(); ++slot) {
         ItemStack item = getItem(slot);
         if (!item.isEmpty() && eating == item && item.getCount() >= decrease) {
            item.split(decrease);
            if (item.getCount() <= 0) { setItem(slot, ItemStack.EMPTY); }
            return true;
         }
      }
      return false;
   }

   // New from Unofficial (BetaZavr)
   @SuppressWarnings({"rawtypes", "unchecked"})
   public void setNewSize(int sizeIn) {
      if (sizeIn != getContainerSize()) {
         ((ISimpleContainerMixin) this).setSize(sizeIn);
         ItemStack[] newList = new ItemStack[sizeIn];
         Arrays.fill(newList, ItemStack.EMPTY);
         for (int i = 0; i < getContainerSize() && i < sizeIn; i++) { newList[i] = getItem(i); }
         ((INonNullListMixin) ((ISimpleContainerMixin) this).getItems()).setList(Arrays.asList(newList));
         setChanged();
      }
   }

   public NpcMiscInventory fill(Container inv) {
      clearContent();
      for (int i = 0; i < getContainerSize() && i < inv.getContainerSize(); i++) { setItem(i, inv.getItem(i)); }
      setChanged();
      return this;
   }

   public int getCountEmpty() {
      int c = 0;
      for (int s = 0; s < getContainerSize(); ++s) {
         if (getItem(s).isEmpty()) { c++; }
      }
      return c;
   }

   public NpcMiscInventory copy() {
      return new NpcMiscInventory(getContainerSize()).fill(this);
   }

}
