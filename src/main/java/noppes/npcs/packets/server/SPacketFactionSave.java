package noppes.npcs.packets.server;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.CustomNpcsPermissions;
import noppes.npcs.controllers.FactionController;
import noppes.npcs.controllers.data.Faction;
import noppes.npcs.shared.common.PacketServerBasic;
import noppes.npcs.packets.Packets;
import noppes.npcs.packets.client.PacketGuiData;

public class SPacketFactionSave extends PacketServerBasic {

   protected static int channelId;
   private NBTTagCompound data;

   public SPacketFactionSave() { }

   public SPacketFactionSave(NBTTagCompound dataIn) { data = dataIn; }

   @Override
   public CustomNpcsPermissions.Permission getPermission() { return CustomNpcsPermissions.GLOBAL_FACTION; }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeNbt(data); }

   @Override
   public void decode(FriendlyByteBuf buf) { data  = buf.readNbt(); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      Faction faction = new Faction();
      faction.load(data);
      FactionController.instance.saveFaction(faction);
      SPacketFactionsGet.sendFactionDataAll(player);
      NBTTagCompound compound = new NBTTagCompound();
      faction.save(compound);
      Packets.send(player, new PacketGuiData(compound));
      CustomNpcs.debugData.end("Packets");
   }

}
