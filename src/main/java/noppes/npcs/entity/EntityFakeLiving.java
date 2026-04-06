package noppes.npcs.entity;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import noppes.npcs.CustomEntities;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class EntityFakeLiving extends LivingEntity {

   public EntityFakeLiving(Level par1Level) {
      super(CustomEntities.entityCustomNpc, par1Level);
   }

   public @NotNull Iterable<ItemStack> getArmorSlots() {
      return new ArrayList<>();
   }

   public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slotIn) {
      return ItemStack.EMPTY;
   }

   public void setItemSlot(@NotNull EquipmentSlot slotIn, @NotNull ItemStack stack) {
   }

   public @NotNull HumanoidArm getMainArm() {
      return HumanoidArm.RIGHT;
   }

}
