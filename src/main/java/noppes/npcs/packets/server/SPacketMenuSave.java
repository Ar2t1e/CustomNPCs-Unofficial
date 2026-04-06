package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.NBTTags;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.controllers.data.MarkData;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.shared.common.PacketServerBasic;

public class SPacketMenuSave extends PacketServerBasic {

   protected static int channelId;
   private EnumMenuType type;
   private NBTTagCompound data;

   public SPacketMenuSave() { }

   public SPacketMenuSave(EnumMenuType typeIn, NBTTagCompound dataIn) {
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
   public CustomNpcsPermissions.Permission getPermission() {
      switch (type) {
         case ADVANCED:
         case MARK:
         case TRANSFORM: return CustomNpcsPermissions.NPC_ADVANCED;
         case STATS: return CustomNpcsPermissions.NPC_STATS;
         case INVENTORY: return CustomNpcsPermissions.NPC_INVENTORY;
         case AI: return CustomNpcsPermissions.NPC_AI;
         case MOVING_PATH: return CustomNpcsPermissions.TOOL_PATHER;
         default: return CustomNpcsPermissions.NPC_DISPLAY;
      }
   }

   @Override
   public void encode(FriendlyByteBuf buf) {
      buf.writeEnum(type);
      buf.writeNbt(data);
   }

   @Override
   public void decode(FriendlyByteBuf buf) {
      type = buf.readEnum(EnumMenuType.class);
      data = buf.readNbt();
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
         case MOVING_PATH: npc.ais.setMovingPath(NBTTags.getIntegerArraySet(data.getTagList("MovingPathNew", 10))); break;
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
