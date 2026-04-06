package noppes.npcs.containers.slots;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SlotNPCArmor extends Slot {

   final EquipmentSlot armorType;

   public SlotNPCArmor(Container container, int slotIndex, int x, int y, EquipmentSlot slot) {
      super(container, slotIndex, x, y);
      armorType = slot;
   }

   public int getMaxStackSize() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return Pair.of(InventoryMenu.BLOCK_ATLAS, SlotCompanionArmor.ARMOR_SLOT_TEXTURES[armorType.getIndex()]);
   }

   public boolean mayPlace(ItemStack itemstack) {
      if (itemstack.getItem() instanceof ElytraItem) { return ((ElytraItem)itemstack.getItem()).getEquipmentSlot() == armorType; }
      else if (itemstack.getItem() instanceof ArmorItem) { return ((ArmorItem) itemstack.getItem()).getEquipmentSlot() == armorType; }
      else if (itemstack.getItem() instanceof BlockItem) { return armorType == EquipmentSlot.HEAD; }
      else { return false; }
   }

}
