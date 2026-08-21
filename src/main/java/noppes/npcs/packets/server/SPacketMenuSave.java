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

import java.util.Collections;
import java.util.List;

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
   public List<CustomNpcsPermissions.Permission> getPermission() {
      CustomNpcsPermissions.Permission permission;
      switch (type) {
         case ADVANCED:
         case MARK:
         case TRANSFORM: permission = CustomNpcsPermissions.NPC_ADVANCED; break;
         case STATS: permission = CustomNpcsPermissions.NPC_STATS; break;
         case INVENTORY: permission = CustomNpcsPermissions.NPC_INVENTORY; break;
         case AI: permission = CustomNpcsPermissions.NPC_AI; break;
         case MOVING_PATH: permission = CustomNpcsPermissions.TOOL_PATHER; break;
         default: permission = CustomNpcsPermissions.NPC_DISPLAY; break;
      }
      return Collections.singletonList(permission);
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
