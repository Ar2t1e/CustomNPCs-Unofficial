package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.constants.EnumMenuType;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketMenuGet extends PacketServerBasic {

   protected static int channelId;
   private EnumMenuType type;

   public SPacketMenuGet() { }

   public SPacketMenuGet(EnumMenuType typeIn) { type = typeIn; }

   @Override
   public boolean toolAllowed(ItemStack item) {
      if (type == EnumMenuType.MOVING_PATH) { return item.getItem() == CustomItems.moving; }
      else { return item.getItem() == CustomItems.wand; }
   }

   @Override
   public boolean requiresNpc() { return true; }

   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.NPC_GUI); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeEnum(type); }

   @Override
   public void decode(FriendlyByteBuf buf) { type = buf.readEnum(EnumMenuType.class); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      NBTTagCompound data = new NBTTagCompound();
      switch (type) {
         case DISPLAY: npc.display.save(data); break;
         case STATS: npc.stats.save(data); break;
         case INVENTORY: npc.inventory.save(data);break;
         case ADVANCED: npc.advanced.save(data); break;
         case TRANSFORM: npc.transform.saveOptions(data); break;
         case AI:
         case MOVING_PATH: npc.ais.save(data); break;
      }
      Packets.send(player, new PacketGuiData(data));
      CustomNpcs.debugData.end("Packets");
   }

}
