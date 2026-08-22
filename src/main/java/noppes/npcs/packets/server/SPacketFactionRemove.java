package noppes.npcs.packets.server;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomItems;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

import java.util.Collections;
import java.util.List;

public class SPacketFactionRemove extends PacketServerBasic {

   protected static int channelId;
   private int id;

   public SPacketFactionRemove() {}

   public SPacketFactionRemove(int idIn) { id = idIn; }

   @Override
   public boolean requiresNpc() { return false; }

   @Override
   public boolean toolAllowed(ItemStack item) { return item.getItem() == CustomItems.wand; }


   @Override
   public List<CustomNpcsPermissions.Permission> getPermission() { return Collections.singletonList(CustomNpcsPermissions.GLOBAL_FACTION); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      FactionController.instance.delete(id);
      SPacketFactionsGet.sendFactionDataAll(player);
      NBTTagCompound compound = new NBTTagCompound();
      (new Faction()).save(compound);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
