package noppes.npcs.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.api.constants.RoleType;
import noppes.npcs.controllers.ServerCloneController;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.roles.RoleCompanion;
import noppes.npcs.roles.RoleFollower;
import noppes.npcs.shared.common.CommonUtil;

public class ItemSoulstoneEmpty extends Item {

   public ItemSoulstoneEmpty() {
      super((new Properties()).stacksTo(64));
   }

   public void store(LivingEntity entity, ItemStack stack, Player player) {
      if (this.hasPermission(entity, player) && !(entity instanceof Player)) {
         ItemStack stone = new ItemStack(CustomItems.soulstoneFull);
         CompoundTag compound = new CompoundTag();
         if (entity.saveAsPassenger(compound)) {
            ServerCloneController.Instance.cleanTags(compound);
            stone.addTagElement("Entity", compound);
            String name = entity.getEncodeId();
            if (name == null) {
               name = "generic";
            }

            stone.addTagElement("Name", StringTag.valueOf(name));
            if (entity instanceof EntityNPCInterface npc) {
               stone.addTagElement("DisplayName", StringTag.valueOf(entity.getName().getString()));
               if (npc.role.getEnumType() != RoleType.COMPANION) {
                  RoleCompanion role = (RoleCompanion)npc.role;
                  stone.addTagElement("ExtraText", StringTag.valueOf("companion.stage,: ," + role.stage.name));
               }
            } else if (entity.hasCustomName() && entity.getCustomName() != null) {
               stone.addTagElement("DisplayName", StringTag.valueOf(entity.getCustomName().getString()));
            }
            NoppesUtilServer.givePlayerItem(player, player, stone);
            if (!player.getAbilities().instabuild) {
               stack.split(1);
               if (stack.getCount() <= 0) {
                  player.getInventory().removeItem(stack);
               }
            }
            entity.discard();
         }
      }
   }

   public boolean hasPermission(LivingEntity entity, Player player) {
      if (CommonUtil.isOp(player)) {
         return true;
      } else if (CustomNpcsPermissions.hasPermission((ServerPlayer)player, CustomNpcsPermissions.SOULSTONE_ALL)) {
         return true;
      } else if (entity instanceof EntityNPCInterface npc) {
         if (npc.role.getEnumType() != RoleType.COMPANION) {
            RoleCompanion role = (RoleCompanion)npc.role;
            if (role.getOwner() == player) {
               return true;
            }
         }
         if (npc.role.getType() == 2) {
            RoleFollower role = (RoleFollower)npc.role;
            if (role.getOwner() == player) {
               return !role.refuseSoulStone;
            }
         }
         return CustomNpcs.SoulStoneNPCs;
      } else {
         return entity instanceof Animal && CustomNpcs.SoulStoneAnimals;
      }
   }
}
