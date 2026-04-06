package noppes.npcs.containers;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import noppes.npcs.CustomContainer;
import noppes.npcs.constants.EnumCompanionTalent;
import noppes.npcs.containers.slots.SlotCompanionArmor;
import noppes.npcs.containers.slots.SlotCompanionWeapon;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataInventory;
import noppes.npcs.roles.RoleCompanion;
import org.jetbrains.annotations.NotNull;

public class ContainerNPCCompanion extends ContainerNpcInterface {

   public final RoleCompanion role;

   public ContainerNPCCompanion(int containerId, Inventory playerInventory, int entityId) {
      super(CustomContainer.container_companion, containerId, playerInventory);
      EntityNPCInterface npc = (EntityNPCInterface) player.level().getEntity(entityId);
      DataInventory inv;
      if (npc != null) {
         role = (RoleCompanion) npc.role;
         inv = npc.inventory;
      }
      else {
         role = new RoleCompanion(null);
         inv = new DataInventory(null);
      }

      int size;
      int i;
      for(size = 0; size < 3; ++size) {
         for(i = 0; i < 9; ++i) {
            addSlot(new Slot(playerInventory, i + size * 9 + 9, 6 + i * 18, 87 + size * 18));
         }
      }

      for(size = 0; size < 9; ++size) {
         addSlot(new Slot(playerInventory, size, 6 + size * 18, 145));
      }

      if (role.talents.containsKey(EnumCompanionTalent.INVENTORY)) {
         size = (role.getTalentLevel(EnumCompanionTalent.INVENTORY) + 1) * 2;

         for(i = 0; i < size; ++i) {
            addSlot(new Slot(role.inventory, i, 114 + i % 3 * 18, 8 + i / 3 * 18));
         }
      }

      if (role.getTalentLevel(EnumCompanionTalent.ARMOR) > 0) {
         addSlot(new SlotCompanionArmor(role, inv, 0, 6, 8, EquipmentSlot.HEAD));
         addSlot(new SlotCompanionArmor(role, inv, 1, 6, 26, EquipmentSlot.CHEST));
         addSlot(new SlotCompanionArmor(role, inv, 2, 6, 44, EquipmentSlot.LEGS));
         addSlot(new SlotCompanionArmor(role, inv, 3, 6, 62, EquipmentSlot.FEET));
      }

      if (role.getTalentLevel(EnumCompanionTalent.SWORD) > 0) {
         addSlot(new SlotCompanionWeapon(role, inv, 4, 79, 17));
      }

   }

   @Override
   public void removed(@NotNull Player playerIn) {
      super.removed(playerIn);
      role.setStats();
   }

}
