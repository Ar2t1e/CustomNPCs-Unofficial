package noppes.npcs.packets.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import noppes.npcs.CustomNpcs;
import noppes.npcs.NoppesUtilServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.shared.common.PacketBasic;

public class PacketNpcEdit extends PacketBasic {

   protected static int channelId;
   private int id;

   public PacketNpcEdit() { }

   public PacketNpcEdit(int idIn) { id = idIn; }

   @Override
   public void decode(FriendlyByteBuf buf) { id = buf.readInt(); }

   @Override
   public void encode(FriendlyByteBuf buf) { buf.writeInt(id); }

   @Override
   public int getChannelId() { return channelId; }

   @Override
   protected void handle() {
      CustomNpcs.debugData.start("Packets");
      WorldClient world = Minecraft.getMinecraft().world;
      if (world != null) {
         Entity entity = world.getEntityByID(id);
         if (entity instanceof EntityNPCInterface) { NoppesUtilServer.setEditingNpc(player, (EntityNPCInterface) entity); }
         else { NoppesUtilServer.setEditingNpc(player, null); }
      }
      CustomNpcs.debugData.end("Packets");
   }

}
