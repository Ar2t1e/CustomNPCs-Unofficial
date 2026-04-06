package noppes.npcs.api.wrapper;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.api.item.IItemArmor;

public class ItemArmorWrapper extends ItemStackWrapper implements IItemArmor {

   protected ArmorItem armor;

   protected ItemArmorWrapper(ItemStack item) {
      super(item);
      this.armor = (ArmorItem)item.getItem();
   }

   public int getType() {
      return 3;
   }

   public int getArmorSlot() {
      return this.armor.getEquipmentSlot().getIndex();
   }

   public String getArmorMaterial() {
      return this.armor.getMaterial().getName();
   }

}
