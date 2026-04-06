package noppes.npcs.constants;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

public enum EnumCompanionTalent {

   INVENTORY(Item.BY_BLOCK.getOrDefault(Blocks.CRAFTING_TABLE, Items.AIR)),
   ARMOR(Items.IRON_CHESTPLATE),
   SWORD(Items.DIAMOND_SWORD),
   RANGED(Items.BOW),
   ACROBATS(Items.LEATHER_BOOTS),
   INTEL(Items.BOOK);

   public final ItemStack item;

   EnumCompanionTalent(Item item) { this.item = new ItemStack(item); }

}
