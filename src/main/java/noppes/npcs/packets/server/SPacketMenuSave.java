package noppes.npcs.packets.server;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketServerBasic;

import java.util.Collections;
import java.util.List;

public class SPacketMenuSave extends PacketServerBasic {

   protected static int channelId;
   private final EnumMenuType type;
   private final CompoundTag data;

   public SPacketMenuSave(EnumMenuType typeIn, CompoundTag dataIn) {
      type = typeIn;
      data = dataIn;
   }

   @Override
   public boolean toolAllowed(ItemStack item) {
      return item.getItem() == (type == EnumMenuType.MOVING_PATH ? CustomItems.moving : CustomItems.wand);
   }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<PermissionNode<Boolean>> getPermission() {
      PermissionNode<Boolean> permission = switch (type) {
         case STATS -> CustomNpcsPermissions.NPC_STATS;
         case INVENTORY -> CustomNpcsPermissions.NPC_INVENTORY;
         case AI -> CustomNpcsPermissions.NPC_AI;
         case ADVANCED, MARK, TRANSFORM -> CustomNpcsPermissions.NPC_ADVANCED;
         case MOVING_PATH -> CustomNpcsPermissions.TOOL_PATHER;
         default -> CustomNpcsPermissions.NPC_DISPLAY;
      };
      return Collections.singletonList(permission);
   }

   public static void encode(SPacketMenuSave msg, FriendlyByteBuf buf) {
      buf.writeEnum(msg.type);
      buf.writeNbt(msg.data);
   }

   public static SPacketMenuSave decode(FriendlyByteBuf buf) {
      return new SPacketMenuSave(buf.readEnum(EnumMenuType.class), buf.readNbt());
   }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      switch (type) {
         case DISPLAY: npc.display.load(data); break;
         case STATS: npc.stats.load(data); break;
         case INVENTORY: npc.inventory.load(data); npc.updateAI = true; break;
         case AI: npc.ais.load(data); npc.setHealth(npc.getMaxHealth()); npc.updateAI = true; break;
         case ADVANCED: npc.advanced.load(data); npc.updateAI = true; break;
         case MODEL: ((EntityCustomNpc) npc).modelData.load(data); break;
         case TRANSFORM: {
            boolean isValid = npc.transform.isValid();
            npc.transform.loadOptions(data);
            if (isValid != npc.transform.isValid()) { npc.updateAI = true; }
            break;
         }
         case MOVING_PATH: npc.ais.setMovingPath(NBTTags.getIntegerArraySet(data.getList("MovingPathNew", 10))); break;
         case MARK: {
            MarkData mark = MarkData.get(npc);
            mark.setNBT(data);
            mark.syncClients();
            break;
         }
      }
      npc.updateClient = true;
      CustomNpcs.debugData.end("Packets");
   }

}
