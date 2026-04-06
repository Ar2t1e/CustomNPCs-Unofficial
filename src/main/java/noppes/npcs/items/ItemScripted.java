package noppes.npcs.items;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.item.INPCToolItem;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemScripted extends Item implements INPCToolItem {

   public ItemScripted(Properties props) {
      super(props);
   }

   public static ItemScriptedWrapper GetWrapper(ItemStack stack) {
      return (ItemScriptedWrapper) Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
   }

   public boolean isBarVisible(@NotNull ItemStack stack) {
      IItemStack iStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
      return iStack instanceof ItemScriptedWrapper ? ((ItemScriptedWrapper)iStack).durabilityShow : super.isBarVisible(stack);
   }

   public int getBarWidth(@NotNull ItemStack stack) {
      IItemStack iStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
      return iStack instanceof ItemScriptedWrapper ? Math.round(13.0F - ((ItemScriptedWrapper)iStack).durabilityValue * 13.0F) : super.getBarWidth(stack);
   }

   public int getBarColor(@NotNull ItemStack stack) {
      IItemStack iStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
      if (!(iStack instanceof ItemScriptedWrapper)) {
         return super.getBarColor(stack);
      } else {
         int color = ((ItemScriptedWrapper) iStack).durabilityColor;
         return color >= 0 ? color : Mth.hsvToRgb(Math.max(0.0F, 1.0F - (float)this.getBarWidth(stack)) / 3.0F, 1.0F, 1.0F);
      }
   }

   public int getMaxStackSize(ItemStack stack) {
      IItemStack iStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
      return iStack instanceof ItemScriptedWrapper ? iStack.getMaxStackSize() : super.getMaxStackSize(stack);
   }

   public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
      return true;
   }

   public boolean shouldOverrideMultiplayerNbt() {
      return true;
   }

   public CompoundTag getShareTag(ItemStack stack) {
      IItemStack iStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
      CompoundTag generalTag = super.getShareTag(stack);
      if (iStack instanceof ItemScriptedWrapper) {
         return generalTag != null ? generalTag.merge(((ItemScriptedWrapper)iStack).getMCNbt()) : ((ItemScriptedWrapper)iStack).getMCNbt();
      }
      return generalTag;
   }

   public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
      if (nbt != null) {
         super.readShareTag(stack, nbt);
         IItemStack iStack = Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(stack);
         if (iStack instanceof ItemScriptedWrapper) {
            ((ItemScriptedWrapper)iStack).setMCNbt(nbt);
         }
      }
   }

}
