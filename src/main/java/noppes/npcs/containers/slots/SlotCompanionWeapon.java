package noppes.npcs.containers.slots;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.NpcAPI;
import noppes.npcs.roles.RoleCompanion;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class SlotCompanionWeapon extends Slot {

   final RoleCompanion role;

   public SlotCompanionWeapon(RoleCompanion roleIn, Container container, int id, int x, int y) {
      super(container, id, x, y);
      role = roleIn;
   }

   @Override
   public int getMaxStackSize() { return 1; }

   @Override
   public boolean mayPlace(@NotNull ItemStack itemstack) {
      return !NoppesUtilServer.isItemStackNull(itemstack) &&
              role.isWeapon(itemstack) &&
              role.canWearWeapon(Objects.requireNonNull(NpcAPI.Instance()).getIItemStack(itemstack));
   }

}
