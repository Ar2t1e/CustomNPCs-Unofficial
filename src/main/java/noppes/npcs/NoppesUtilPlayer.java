package noppes.npcs;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;

public class NoppesUtilPlayer {

   public static boolean compareItems(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
      return !NoppesUtilServer.isItemStackNull(item) &&
              !NoppesUtilServer.isItemStackNull(item2) &&
              compareItemDetails(item, item2, ignoreDamage, ignoreNBT);
   }

   private static boolean compareItemDetails(ItemStack item, ItemStack item2, boolean ignoreDamage, boolean ignoreNBT) {
      if (item.getItem() != item2.getItem()) { return false; }
      if (!ignoreDamage && item.getDamageValue() != item2.getDamageValue()) { return false; }
      return ignoreNBT || Objects.equals(item.getTag(), item2.getTag());
   }

   public static boolean compareItems(Player player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT) {
      int size = 0;
      for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
         ItemStack is = player.getInventory().getItem(i);
         if (!NoppesUtilServer.isItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) {
            size += is.getCount();
         }
      }
      return size >= item.getCount();
   }

   // New from Unofficial (BetaZavr)
   public static boolean compareItems(Player player, ItemStack item, boolean ignoreDamage, boolean ignoreNBT, int amount) {
      int size = 0;
      if (player == null) { return false; }
      for(int i = 0; i < player.getInventory().getContainerSize(); ++i) {
         ItemStack is = player.getInventory().getItem(i);
         if (!NoppesUtilServer.isItemStackNull(is) && compareItems(item, is, ignoreDamage, ignoreNBT)) { size += is.getCount(); }
      }
      return size >= amount;
   }

}
